local skuStockKey = KEYS[1];
local skuSerialKey = KEYS[2];
local needStock = tonumber(ARGV[1]);
local serialId = ARGV[2];
if (redis.call('exists', skuStockKey) == 0) then
    return -2;
end

-- 库存流水不存在
local serialExists = redis.call('SISMEMBER', skuSerialKey, serialId);
if serialExists == 0 then
    return -9;
end
-- TODO 回滚库存，需要新的数据判断，防止回滚多次，数据库回滚时，也要先查一遍，如果要查，最好流水号是递增的
redis.call('srem', skuSerialKey, serialId)

local currentStockStr = redis.call('get', skuStockKey);
local currentStock = tonumber(currentStockStr);



-- 库存满足
if currentStock >= needStock then
    local restStock = redis.call('decrby', skuStockKey, needStock);
    local addSerialResult = redis.call('sadd', skuSerialKey, serialId);
    return restStock;
else
    -- 库存不足
    return -3;
end
return -1;
