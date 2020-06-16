This is just another applicable use case with Amazon Elasticache for redis. For reducing database burden under a heavy transation environment, we just depend on java spring boot's transaction management which is implemeneted with proxy based transcations among different method call (in this case, insert call for aurora rds and update call for redis cache cluster)

