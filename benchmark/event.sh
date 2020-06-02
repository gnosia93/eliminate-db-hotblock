#!/bin/sh

host=git-webappa-1x1n85amayiwf-759140899.ap-northeast-1.elb.amazonaws.com:8080
target=http://$host/order//event-add
ab -l -p order-payload.json -T 'application/json;charset=utf-8' -e order-payload.csv -c 1 -n 1 $target