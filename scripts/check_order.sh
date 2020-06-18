#!/bin/bash

mysql -u demo -pdemo12345 -h <your-aurora-writer-endpoint> -e "select * from shop.order order by order_id desc"