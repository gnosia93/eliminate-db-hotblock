#!/bin/sh

host=localhost:8080
target=http://$host/order/add
ab -l -p order-payload.json -T 'application/json;charset=utf-8' -e order-payload.csv -c 150 -n 3000 $target
