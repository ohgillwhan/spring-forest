local result = 'empty';
for i, v in pairs(ARGV) do
    local exists = redis.call("HEXISTS", KEYS[1], v)
    if(exists == 1) then
--         local value = redis.call("HGET", KEYS[1], v)
--         if(value > 0) then
            result = 'exists';
--         end
    end
end

for i, v in pairs(ARGV) do
    redis.call('HINCRBY', KEYS[1], v, 1)
end
return result;