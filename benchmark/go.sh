#!/bin/sh

host=localhost:8081
target=http://$host/order/add
ab -l -p order-payload.json -T 'application/json;charset=utf-8' -e order-payload.csv -c 1 -n 1 $target
