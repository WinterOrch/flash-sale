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
				return 3
			else
				return 0							--库存不足
			end
		end
	else
		return -1								--商品不存在
end