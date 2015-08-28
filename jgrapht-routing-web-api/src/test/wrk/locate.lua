-- example script that demonstrates adding a random
-- 10-50ms delay before each request

path = ""
max = 0
min_lon = 3.67094063945916
min_lat = 44.1422006596464
max_lon = 7.13385567629149
max_lat = 46.4636352867284
headers = {}

function init(args)
   math.randomseed(os.time())
   path = args[0]
   headers['Content-type'] = 'application/x-www-form-urlencoded; charset=utf-8'
end

function delay()
   return math.random(10, 50)
end

function request()
   lon = min_lon + (max_lon - min_lon) * math.random()
   lat = min_lat + (max_lat - min_lat) * math.random()
   body = 'lon=' .. lon .. '&lat=' .. lat
   return wrk.format("POST", path, headers, body)
end
