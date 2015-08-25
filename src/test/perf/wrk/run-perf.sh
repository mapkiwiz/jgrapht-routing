#!/bin/sh

WRK_BIN=/home/crousson/projects/ssp/wrk/wrk
BASE_URL=http://localhost:8080/routing/api/v1
METHOD=route
TEST_POINTS=random_points.csv
DURATION=5m
SCRIPT=routing.lua
TIMEOUT=1m

for k in 5
do
    $WRK_BIN -c $k -d $DURATION -t $k -s $SCRIPT --timeout $TIMEOUT $BASE_URL/$METHOD -- $TEST_POINTS
    sleep 10
done
