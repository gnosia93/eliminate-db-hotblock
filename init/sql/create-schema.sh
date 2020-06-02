#!/bin/sh

mysql -u demo -pdemo12345 -h git-db.cluster-cjywfhnks8rs.ap-northeast-1.rds.amazonaws.com < aurora.sql
mysql -u demo -pdemo12345 -h git-db.cluster-cjywfhnks8rs.ap-northeast-1.rds.amazonaws.com -e "select count(1) as 'gen_product_cnt' from shop.product"

