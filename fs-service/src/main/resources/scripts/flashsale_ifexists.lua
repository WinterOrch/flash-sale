local exists = redis.call('exists', KEYS[1])	--判断商品存在与否
local dupOrder = redis.call('exists', KEYS[2])	--已经抢过
	if exists == 1 then
		local stock = redis.call('get', KEYS[1])
		if stock > "0" then
			if dupOrder == 0 then
				redis.call('decr', KEYS[1])		--有库存则减一
				redis.call('set', KEYS[2], "1")	--保存记录
				return 1						--秒杀成功
			else
				return 2						--抢过了
			end
		else
			return 0							--无库存
		end
	else
		return -1								--商品不存在
end