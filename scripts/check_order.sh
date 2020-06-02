#!/bin/bash

mysql -u demo -pdemo12345 -h git-db.cluster-cjywfhnks8rs.ap-northeast-1.rds.amazonaws.com -e "select * from shop.order order by order_id desc"