local skuStockKey = KEYS[1];
local skuSerialKey = KEYS[2];
local needStock = tonumber(ARGV[1]);
local serialId = ARGV[2];
if (redis.call('exists', skuStockKey) == 0) then
    return -2
end
local currentStockStr = redis.call('get', skuStockKey);
local currentStock = tonumber(currentStockStr);
local repeatErr = redis.call('SISMEMBER', skuSerialKey, serialId);
-- 幂等重复
if repeatErr == 1 then
    return -4;
end
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
