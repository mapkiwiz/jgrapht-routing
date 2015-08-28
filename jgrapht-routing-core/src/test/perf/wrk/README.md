# Performance tests

## Prerequisites

1. Build and install ``wrk``
   https://github.com/wg/wrk

If you wand to record statistics during test execution :

2. Install statsd

```
npm install statsd
```

3. Install graphite

```
pip install graphite-web
```

## Running the tests

If required, start the statsd server :

```
./node_modules/statsd/bin/statsd statsd.config.js &
cd /opt/graphite
gunicorn wsgi &
cd -
```

Run the tests with 5 concurrent threads for 10 minutes :

```
wrk -t 5 -c 5 -d 10m -s routing.lua http://localhost:8080/routing/api/v1/route -- random_points.csv
```
