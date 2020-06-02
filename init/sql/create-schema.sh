#!/bin/sh

mysql -u demo -pdemo12345 -h git-db.cluster-cjywfhnks8rs.ap-northeast-1.rds.amazonaws.com < aurora.sql
