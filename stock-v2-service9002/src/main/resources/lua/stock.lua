local skuStockKey = KEYS[1];
local needStock = tonumber(ARGV[1]);
if (redis.call('exists', skuStockKey) == 0) then
    return -2
end
local currentStockStr = redis.call('get', skuStockKey);
local currentStock = tonumber(currentStockStr);
-- 库存满足
if currentStock >= needStock then
    local restStock = redis.call('decrby', skuStockKey, needStock);
    return restStock;
else
    -- 库存不足
    return -3;
end
return -1;
