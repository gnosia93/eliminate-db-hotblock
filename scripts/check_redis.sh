#!/bin/bash

REDIS=<your-redis-cluster-endpoint>
telnet $REDIS 6379
get sell_cnt_1004