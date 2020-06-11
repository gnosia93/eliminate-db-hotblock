
## Eliminating Database Hot Blocks with with Amazon ElastiCache ##


The cache system occupies part of the modern web applications regardless of the scale of services.    
As the number of users increases and various events are held for enlarging sales revenues without any patterns frequently,
Many application services become to face critical performance issues, especially in the area of database system.

For normal heavy read or write bottleneck with wide range of items,
we can easily mitigate or eliminate performance problems with various solutions.
But if you meet performance degrations with narrow range of hot write, it is not easy to deal with.

Databse sharding or adoption of NOSQL could be one candidate solution, but 
What if the update is concentrated on one or two items,
it become big service problem and eventually could be connected with your business risk. 

In this blog post, I wanna share with you how to eliminate database hot block with Amazon Elasticache,
and demonstrate performnce gain when you replace database update operation into redis key update.

### *Disclaimer* ### 

*This is just another applicable use case with Amazon Elasticache for redis.
For reducing database burden under a heavy transation environment, we just leverage java spring boot's proxy based transaction management which support transcations among different storage backend system(in this case between aurora rdbms and redis)*

*Tough Amazon elasticache for redis provide HA configuration and dramatic HA failover functionality, it's failover is implemented with DNS level failover.
When primary instnace is abnormaly shutdown or failed, service is not available for certiain priod time(failover time).
So this use case is not suitable for your application workload. But by walking throught this example, I am sure that you are able to get a good and new perspective about cache system like Amazon Elasticache when you are implementing scalable web scale services.* 


## Architecture ##

This is simple architecture of 3 tier web application. But at the backend database tier, Amazon Elasticache for redis is occupied to eliminate database hot block which degrade web application performance severely.

Both Web and API service have a public end point(public dns name) respectively.
and sharing same public subnet of VPC for supporting simple performance test from outside of Amazon VPC. 

![infra](https://github.com/gnosia93/demo-cache/blob/master/document/infra-architecture.png)

There is two internet facing ALB. the one is for web interface and the other is for json API.
They both serve at port 80 and you can order certain product with both web user interface and json api call. 

Under the each ALB, there are two EC2 instnaces which contain node.js web application or api service which is implemented with spring boot application respectively.

At the data layer, we have Amazon Elasticache for redis which have cache objects counting product selling
and auroa database cluster which is composed of just one master node, no replica.
and Aurora database have two DB tables which name is product and order to handle our senarios.


## Infrastructure Building ##

### Infra Provisioning with CloudFormation ###

Here, we will use AWS cloudformation to automate painfull and error-prone infrastucture building. 
You can find cloudformation configuration file which name is stack-build.yaml in the subdirectory of this project.

Go to AWS Cloudformation console, and build infrasture of this project with stack-build.yaml.
Normally it takes about 10 minitues for all infra provisioning.
If you are not good at AWS Cloudformation, refer to this URL (https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/GettingStarted.html)

Below screen is the sample outputs of cloudformation which can be founded at stack menu.
You can easily identify web, api endpoint, and provisioned EC2 instances public address, etc.

![cf-outputs](https://github.com/gnosia93/demo-cache/blob/master/document/cf-outputs.png)

AuroraCluster and Redis URL is used at JAVA springboot application configuration.
Both WebEndPoint and ApiEndPoint is load balancer url having public ip address, served at port 80.


At the moment, web server has a dependancy with backend API sever, 
we will set up API server first rather than web server.

### Configure API Server ###

In order to configure API server, log into your api-server with ssh or compatible ssh client and then set up your backend connection for both redis and aurora database repectively. You have to bear in mind that we have two api server.
Please refer following instruction to do your settings.

```
$ ssh -i <your-pem-file> ec2-user@your-api-instance-dnsname

The authenticity of host 'your-api-instance-dnsname (your-api-ip)' can't be established.
ECDSA key fingerprint is SHA256:f1leNwUtSQdTwHqsusHlzEef812DWDtqgJ7oVwlUOzg.
Are you sure you want to continue connecting (yes/no)? yes
Warning: Permanently added 'ec2-13-114-101-172.ap-northeast-1.compute.amazonaws.com,13.114.101.172' (ECDSA) to the list of known hosts.

       __|  __|_  )
       _|  (     /   Amazon Linux 2 AMI
      ___|\___|___|

https://aws.amazon.com/amazon-linux-2/
$ 
$ cd ~/demo-cache/src/main/resources
$ vi application-prod.properties
```
Replace `<your-aurora-writer-endpoint>` and `<your-redis-cluster-endpoint>` to your configuration.
You can find all required connection address from cloudformation stack outputs tab like above. 

[application-prod.properties]
```
spring.datasource.jdbc-url=jdbc:mysql://<your-aurora-writer-endpoint>:3306/shop?serverTimezone=UTC
spring.datasource.url=jdbc:mysql://<your-aurora-writer-endpoint>:3306/shop?serverTimezone=UTC
spring.datasource.username=demo
spring.datasource.password=demo12345
spring.datasource.maximum-pool-size=100

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

spring.redis.lettuce.pool.max-active=10
spring.redis.lettuce.pool.max-idle=10
spring.redis.lettuce.pool.min-idle=2
spring.redis.port=6379
spring.redis.host=<your-redis-cluster-endpoint>        
```

### Initialize Database ###

In this project, we are using Aurora MySQL datbase which has only primary node,
and making database tables, procedure and buidling sample data of product table.
Please execute a command like below at the ec2 instance console of either api server instances 
and you need to confirm product table's row count is 10000. 

If you are good at MySQL database and compatibles, you can login Auroa RDS using mysql client and check sample schema,
auto generated datas.

Goto init/sql sub directory under the demo-cache project root,  
```
$ cd ~/demo-cache/init/sql
$ vi create-schema.sh 
````

and change `<your-aurora-address>` into yours.(refer to cloudformation outputs)
As you see, We will database user which name is demo and password demo12345
Don't modify this database user login information. 

[create-schema.sh]
```
AURORA=<your-aurora-address>
mysql -u demo -pdemo12345 -h $AURORA < aurora.sql
mysql -u demo -pdemo12345 -h $AURORA -e "select count(1) as 'gen_product_cnt' from shop.product"
```    

You can check schema build result from execution create-schema.sh like below.
```
$ sh create-schema.sh 
mysql: [Warning] Using a password on the command line interface can be insecure.
mysql: [Warning] Using a password on the command line interface can be insecure.
+-----------------+
| gen_product_cnt |
+-----------------+
|           10000 |
+-----------------+                                                 
```

### Execute Application and Check the right result ###

Now, We have done all the configuration and sample database building.
Finally, execute run.sh to start spring boot java application and check if java web application is working properly.
The result of curl must be like belows, it must have to return json health checking result output.
If you have any problem, please check [application-prod.properties] configuration file and tomcat.log in project root.
After you modify your miss configuration, you need to execute mvn clean and mvn package at the root of this project.
Because [application-prod.properties] file is bundled with execution jar, you need to clean a previous jar artifact and
do a re-packaging new one with right property configuration.

```
$ cd ~/demo-cache
$ mvn clean; mvn package
$ sh scripts/run.sh

$ curl localhost
{"localDateTime":"2020-06-10T12:00:40.45","code":200,"message":"ok","data":{"localDateTime":"2020-06-10T12:00:40.45","code":200,"message":"I am working!","data":""}}
```

Also check web browser output connecting to api load balancer endpoint. 
Load balancing functionality is mandatory for next benchmark step.    

![browser-output](https://github.com/gnosia93/demo-cache/blob/master/document/brower-ouput.png)


like wise, if you don't watch output like above please check your configuration.


### Configure Web Server ###

As shown in the architecture diagram, web server need to comunicate with API endpoint, 
so you need t set up API endpoint at node.js application configuration.
Log into your web-server with ssh or compatible ssh client and then set up your api endpoint for node.js appliation
You have to bear in mind that we have two web server. so you have to do this configuration for each server.
Please refer following instruction to do your settings.

```
$ ssh -i <your-pem-file> ec2-user@your-web-instance-dnsname

The authenticity of host 'your-web-instance-dnsname (your-api-ip)' can't be established.
ECDSA key fingerprint is SHA256:f1leNwUtSQdTwHqsusHlzEef812DWDtqgJ7oVwlUOzg.
Are you sure you want to continue connecting (yes/no)? yes
Warning: Permanently added 'your-web-instance-dnsname' (ECDSA) to the list of known hosts.

       __|  __|_  )
       _|  (     /   Amazon Linux 2 AMI
      ___|\___|___|

https://aws.amazon.com/amazon-linux-2/
$ 
$ cd ~/demo-cache-front/config
$ vi .env.prod
```
Replace `<your-api-endpoint>` section with your configuration.
You can find all required connection address from cloudformation stack outputs tab like above. 

[.env.prod]
```
WS_SERVER_URL="ws://localhost"
API_ENDPOINT="http://<your-api-endpoint>"
```

Goto project root directory and excute following commands. 
```
$ cd ~/demo-cache-front/
$ curl -sL https://rpm.nodesource.com/setup_12.x | sudo -E bash -
$ sudo yum install -y nodejs
$ sudo npm install cross-env -g
$ npm audit fix
$ sh run.sh 
```

Now, our web site is working..

![home](https://github.com/gnosia93/demo-cache/blob/master/document/home.png)


## BenchMarking ##

Apache Bench (ab) is a tool from the Apache organization for benchmarking a Hypertext Transfer Protocol (HTTP) web server. Although it is designed to measure the performance of Apache web server, yet it can also be used to test any other web server that is equally good. With this tool, you can quickly know how many requests per second your web server is capable of serving.

If you want to find more detailed information about Apache Brench, please goto 
https://www.tutorialspoint.com/apache_bench/apache_bench_overview.htm

and you need to install ab to your computer.
execute following command if operation system is Mac OS and have homebrew package manager.
otherwise, refer Apache Bench site for installation. 
```
$ brew install httpd-tools
```

![bench-arch](https://github.com/gnosia93/demo-cache/blob/master/document/benchmark-architecture.png)

As shown above test architecture diagram, we have two kinds of test senario with ab.
In the left side senario, we only use aurora rds whenever new order happens.
On the contrast, in the right side senario, we use both Amazon Elasticache and aurora rds.
When new order is received, new order is inserted into aurora database table, 
and update cache object value coresponding key (here, key is composed with new order's product number.)

There is two API endpoint url, /add is just only with rds, /event-add is working with both elasticach and rds for order processing.

* /your-api-endpoint/order/add
* /your-api-endpoint/order/event-add


With ab, we will make order request from the our laptop. 
Incurred workload's concurrency is 150 and total volume of request is 3000 per one trial.

Go to benchmark sub directory under this project, check following information and replace `<your-api-endpoint>` to yours.

```
$ cd benchmark
$ ls -la
drwxr-xr-x   6 soonbeom  1896053708   192 Jun 11 02:35 .
drwxr-xr-x  21 soonbeom  1896053708   672 Jun 11 02:30 ..
-rwxr-xr-x   1 soonbeom  1896053708   215 Jun 11 02:35 order-db.sh
-rw-r--r--   1 soonbeom  1896053708  1730 Jun  2 13:50 order-payload.csv
-rw-r--r--   1 soonbeom  1896053708   176 Jun 11 02:31 order-payload.json
-rw-r--r--   1 soonbeom  1896053708   221 Jun 11 02:35 order-redis.sh
```

[order-db.sh]
```
#!/bin/sh

host=<your-api-endpoint>
target=http://$host/order/add
ab -l -p order-payload.json -T 'application/json;charset=utf-8' -e order-payload.csv -c 150 -n 3000 $target
```
[order-redis.sh]
```
#!/bin/sh

host=<your-api-endpoint>
target=http://$host/order//event-add
ab -l -p order-payload.json -T 'application/json;charset=utf-8' -e order-payload.csv -c 150 -n 3000 $target
```

This is our benchmark test payload, default payload use productId 1004, other attributes is not import,
If you want to change productId ordered, make sure corresponding productId exists in database,
in default implementation, you can use productId between 1 to 10000. 
And also both order-redis and order-db shell command use same payload.
```
{
   "orderId": 0,
   "productId": 1004,
   "orderPrice": 1000,
   "payStatus": null,
   "orderDate": null,
   "payDate": null,
   "errorDate": null,
   "errorMessage": null
}
```

After finishing setup order-db and order-redis shell, execute following command in order.
Here, we execute order-db shell first and wait more than 10 sec.. in order to avoid rds storage level interference.

```
$ sh /order-db.sh 

#[wait about 10 sec]

$ sh /order-redis.sh 
```

Below is execution output of ab. Time taken for tests value is total elapsed time since stress test started.
Compare between order-db.sh and order-redis.sh ouput.

```
This is ApacheBench, Version 2.3 <$Revision: 1826891 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/

Benchmarking cachedemo-api-alb-177367678.ap-northeast-1.elb.amazonaws.com (be patient)
Completed 300 requests
Completed 600 requests
Completed 900 requests
Completed 1200 requests
Completed 1500 requests
Completed 1800 requests
Completed 2100 requests
Completed 2400 requests
Completed 2700 requests
Completed 3000 requests
Finished 3000 requests


Server Software:        
Server Hostname:        cachedemo-api-alb-177367678.ap-northeast-1.elb.amazonaws.com
Server Port:            80

Document Path:          /order/add
Document Length:        Variable

Concurrency Level:      150
Time taken for tests:   25.471 seconds
Complete requests:      3000
Failed requests:        0
Total transferred:      1576224 bytes
Total body sent:        1143000
HTML transferred:       994224 bytes
Requests per second:    117.78 [#/sec] (mean)
Time per request:       1273.557 [ms] (mean)
Time per request:       8.490 [ms] (mean, across all concurrent requests)
Transfer rate:          60.43 [Kbytes/sec] received
                        43.82 kb/s sent
                        104.25 kb/s total

```


## BenchMarking Result ##

This benchmarking use follwing test spec servers

- Two API server which instance type is m5d.large(2 vCPU, 9G RAM)
- Two Node Cache Cluster which instance typs is cache.m3.medium(3GB, Moderate Network Speed), 
- One Node Aurora MySQL 5.6 which instance typs is db.r5.large(2 vCPU, 16GB RAM)


#### Elapsed Time ####

![benchmark-output](https://github.com/gnosia93/demo-cache/blob/master/document/benchmark-output.png)


#### CPU Usage #### 

![cpu](https://github.com/gnosia93/demo-cache/blob/master/document/cw-db-cpu.png)

#### DML Latency ####

![cpu](https://github.com/gnosia93/demo-cache/blob/master/document/cw-dml-latency.png)


Former graph case is DB only test case, later is test case with Amazone Elasticache.


#### [Test Result] ####

* Time taken for tests:   25.471 seconds  (left side senario, only aurora rds)
* Time taken for tests:   5.861 seconds   (right side senario, both elasticache and aurora rds)


**`As you can see from the test results, With Redis Benchmark result is about 4.3 times faster than DB only.`**

Remeber that test result can be varied with difference ab parameter(total request and concurrency)
and infra structure spec like rds VM size and IO capacity, and api server spec.
If you need various test result, please do performance test with different configuration. 


![web-product](https://github.com/gnosia93/demo-cache/blob/master/document/web-product.png)

Upper screen shot is product web UI page of this project, go to your web site of this project,
check web result page after executing your benchmark test.

* [구매건수] : total order count of this product, which count is updated in dbms table. 

* [이벤트구매건수] : total order count of this product, which count is updated with redis cache.





## Conclusion ##





## Addtional Contents (Planned) ##

- develop material in HA cases, how it works and how much time takes for completion of HA

- deep dive to spring boot implementaion for transaction processing between redis and rdbms.

- pricing comparison (Well Architected view)

- Consistency testing in the case of various Incident cases.
