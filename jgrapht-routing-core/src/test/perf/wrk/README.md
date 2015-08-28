# Performance tests

## Prerequisites

Build and install ``wrk``

```   
git clone https://github.com/wg/wrk
cd wrk
make
sudo make install
```

If you wand to record statistics during test execution :

Install statsd :

```
npm install statsd
```

Install graphite :

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
