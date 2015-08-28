-- example script that demonstrates adding a random
-- 10-50ms delay before each request

Statsd = require 'statsd'
statsd = Statsd({
   host = '127.0.0.1',
   port = 8125,
   namespace = 'routing'
})

path = ""
n = 0
i = 0
min_lon = 3.67094063945916
min_lat = 44.1422006596464
max_lon = 7.13385567629149
max_lat = 46.4636352867284
headers = {}
random_points = {}

-- function startup()
--    statsd:gauge('client.threads', 0)
-- end

function init(args)
   math.randomseed(os.time())
   path = args[0]
   headers['Content-type'] = 'application/x-www-form-urlencoded; charset=utf-8'
   io.input(args[1])
   for l in io.lines() do
      j = string.find(l, ',', 0)
      k = string.find(l, ',', j+1)
      p = {}
      p[0] = tonumber(string.sub(l, 0, j-1))
      p[1] = tonumber(string.sub(l, j+1, k-1))
      random_points[n] = p
      n = n + 1
    end
    i = 1 + math.random(n) -- start at random point in random_points
    -- statsd:gauge('client.threads', '+1')
end

function delay()
   return math.random(10, 50)
end

function nexti()
   if i >= n then
      i = 1 -- skip header line
   else
      i = i + 1
   end
end

function request()
   nexti()
   local s_lon = random_points[i][0] -- min_lon + (max_lon - min_lon) * math.random()
   local s_lat = random_points[i][1] -- min_lat + (max_lat - min_lat) * math.random()
   nexti()
   local t_lon = random_points[i][0] -- min_lon + (max_lon - min_lon) * math.random()
   local t_lat = random_points[i][1] -- min_lat + (max_lat - min_lat) * math.random()
   body = 'source=' .. s_lon .. ',' .. s_lat .. '&target=' .. t_lon .. ',' .. t_lat
   return wrk.format("POST", path, headers, body)
end

function response(status, time, headers, body)
    if status == 200 then
	statsd:timer('client.time', time / 1000)
        statsd:increment('client.status_ok', 1)
    else
	statsd:increment('client.status_' .. status, 1)
    end
end

-- function shutdown()
--   statsd:gauge('client.threads', '-1')
-- end

-- function done(summary, latency, requests)
--   print 'Done'
-- end
