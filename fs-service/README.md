> 目前用到的技术栈：Spring Boot、ZooKeeper、Redis (Spring Data Redis)、Redisson、RabbitMQ (Spring AMQP)



## **商品如何上架**

1. **通过定时任务查询需要上架的秒杀商品**

   > **注意：**秒杀模块需要向`coupon`模块申请更新秒杀活动信息，这是一个内部调用的分布式API，并不会有很高的并发量，实际上频率在5秒左右一次就完全够了

2. **将秒杀商品信息 TO 保存至 Redis 缓存中**

   > 随商品信息一起存入的**随机码(`random`)**是一个出于公平性考虑的设计，因为实际商品的Id有很多途径能够在秒杀活动开始前获知，然后就可以通过脚本在秒杀开始前提前开始频繁刷取。为此，我们将秒杀商品的活动Id设计成 随机码 + 商品Id 的形式，并在秒杀活动开始时将其暴露出来，能够一定程度增加公平性。
   >
   > 为什么说是一定程度呢，因为实际上即使在开始时才暴露出接口，脚本也可以迅速读到接口不是吗，只是增加一个步骤的事，通过接口异构设计来防止脚本还是不太现实的。


</br>

## **秒杀如何进行**

整个秒杀模块没有数据库操作，所以实际上由 Redis 和 JVM 两层缓存组成。

1. 不同服务器的 JVM 缓存通过 ZooKeeper 保持一致性
2. Redis 保存所有秒杀商品 VO，以及剩余库存信息
3. 订单信息通过 RabbitMQ 发送给消费者——订单处理模块

> ### **Todo List**
>
> **主要功能**
>
> - [x] ZooKeeper 同步 JVM 缓存
> - [x] 分离 Redis 下 VO 缓存与库存信息缓存
> - [x] 通过 Feign 接口更新秒杀信息
>
> **优化**
>
> - [x] 设置 RabbitMQ 发送回调
>
> - [x] Pipeline 优化 Redis 缓存写入
>
> - [ ] 通过**正在进行抢购活动表**判断秒杀活动是否过期
>
> - [ ] 在 Redis TO 中加入 VO 信息
>
>   目前商品只有 Id ，没有 VO 信息，这一部分可以在商品 TO 实体中进行添加。
>
> **缓存过期**
>
> - [ ] VO 缓存信息过期清理
> - [ ] JVM 缓存过期清理
> - [x] 库存信息缓存自动过期
> - [ ] 秒杀占位信息自动过期
>
> 为了判断秒杀目标是否还在活动时间内，目前的做法是将每件活动商品的起始、结束时间以毫秒为单位存在 Redis TO 中，处理秒杀请求过程中取出 Redis TO 进行判断，顺便进行商品秒杀数量限制、随机码正确性的检查。个人认为，如果能够直接在 JVM 维护一张正在进行抢购活动的列表，应当能够更有效过滤掉过期请求。

### 秒杀请求处理逻辑

1. **检查 JVM 缓存，判断商品是否已经售完**

   > 实际上，比较好的做法是首先根据 `sessionId` 检查秒杀活动有没有过期。

   JVM 缓存是一个 `ConcurrentHashMap` ，由 Zookeeper (`ZkService`) 负责同步，Zookeeper 客户端通过监视 `\flashsale_goods` 目录进行更新。每次有商品售完后所有服务器才需要更新，因此对于秒杀系统而言，能够以每次商品售完时 JVM 缓存的更新为代价，取代掉大部分秒杀失败请求处理中对 Redis TO 和 商品库存信息的读取，因此这一代价是值得的。

   </br>

2. **从 Redis TO 缓存取出商品 TO**

   随机码、商品活动时间范围、秒杀件数限制都存在 TO 中

3. **检查 <u>随机码是否正确</u> 、<u>当前是否在商品活动时间内</u>、<u>秒杀件数是否符合限制</u>**

4. **进入 LUA 脚本 进行秒杀，`KEYS[2]` 为订单占位键，`KEYS[1]` 为秒杀商品的库存键，`ARGV[1]` 为抢购商品件数**

   ```lua
   local stock = redis.call('get', KEYS[1])
   local dupOrder = redis.call('exists', KEYS[2])	--已经抢过
   	if stock ~= false then
   		if tonumber(stock) >= tonumber(ARGV[1]) then
   			if dupOrder == 0 then
   				redis.call('decrby', KEYS[1], ARGV[1])		--有库存则减
   				redis.call('set', KEYS[2], "1")	--保存记录
   				return 1						--秒杀成功
   			else
   				return 2						--抢过了
   			end
   		else
   			if tonumber(stock) > 0 then
   				return 3							--库存不足
   			else
   				return 0							--已售完
   			end
   		end
   	else
   		return -1								--商品不存在
   end
   ```

   如果商品存在，且库存足够，则存下订单占位符。

   > 这里占位符需要后续加上过期时间

5. **订单建立成功后，加上一个分布式 Id （通过 Twitter SnowFlake 算法生成）作为订单 Id，通过消息队列传给订单处理模块**

   不同服务器的订单 Id 一定不能一致，因此这里用分布式 UID 算法生成订单 Id。消息通过一个 Topic 交换机 `order-event-exchange` 通过 Topic `order.flashsale.order` 发送给订单处理队列 `stock.flashsale.order.queue` 。

### RabbitMQ

`fs-service` $\longrightarrow $ `order-event-exchange` $\longrightarrow$ `stock.flashsale.order.queue` $\longrightarrow$ `fs-order_service`

- **MyConfirmCallback**

  `publisher-confirms` 的回调函数，在 ACK 时清除重发队列中缓存消息，NACK 时直接进行重发

- **MyDataRelation**

  维护 Correlation UUID - FlashSaleOrderMessage 的重发队列

  > RabbitMQ 防止消费者侧消息丢失的策略是 ACK/NACK 回调函数，项目相应的需要维护自己的重发列表，利用 AMQP 中 `CorrelationData` 的 `id` 找到需要重发的消息内容。
  >
  > 项目也开启了 `mendatory` ，用于在路由策略出现，Exchange 找不到对应 Queue 时进行回调，不过这是个 debug 信息，不需要单独建 `ReturnCallback` 。

