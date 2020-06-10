
# Improving Appication Performance with AWS ElastiCache #

Today's application is ... 

현대의 웹어플리케이션은 복잡하고, 빠른 성능을 제공해야 하며, 이벤트시 몰려서 죽거나.. 한다.

그래서 케시 시스템은 이러한 문제를 방지하기 위한 솔루션으로 자주 사용되어 지곤 한다..


Cache system have some portion of large scale web application.
Usually the common use case of cache system is divided into three category blows.  

* DB Result Caching
* Session Store 
* Hot read with stale data
 
In this Scenario, I am going to share with you another use case of cache system for large scale web application
having frequent update about specific data items, which is saved in rdbms. 

~~ 쇼핑 컴퍼니, 주문 폭주, 이벤트 데이 주문 처리.. 수량 체크

~~ 게시판 글수 카운트.

~~ 특정 상품에 대한 실시간 판매 건수 계산.

~~ wo

케시를 사용하면 얼마나 빠르고, 비용효율적인를 보여주겠다..

We are going to go through how to eliminate database hotblock with amazon elasticache redis and
java spring boot web application.
We don't deal with application implementation details in here, but If you are either developer or someone who can read
java language, you can easily catch up with details.

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

Now, our web page is working..

<< 첫화면 그림>>



## BenchMarking ##

Apache Bench (ab) is a tool from the Apache organization for benchmarking a Hypertext Transfer Protocol (HTTP) web server. Although it is designed to measure the performance of Apache web server, yet it can also be used to test any other web server that is equally good. With this tool, you can quickly know how many requests per second your web server is capable of serving.

If you want to find more detailed information about Apache Brench, please goto 
https://www.tutorialspoint.com/apache_bench/apache_bench_overview.htm


![bench-arch](https://github.com/gnosia93/demo-cache/blob/master/document/benchmark-architecture.png)

As shown above test architecture diagram, we have two kinds of test senario with ab.
In the left side senario, we only use aurora rds whenever new order happens.
On the contrast, in the right side senario, we use both Amazon Elasticache and aurora rds.
When new order is received, new order database record is inserted into aurora database table, 
and update cache object value coresponding key (here, key is composed with new order's product number.)

With ab, we will make order request from the our laptop. 
Incurred workload's concurrency is 150 and total volume of request is 3000 per one trial.

Go to benchmark sub directory under this project, check following information and replace <your-api-endpoint> to yours.

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

http-tools package is containing Apache Bench tool, If you are using Mac OS, install Apache Bench with homebrew like below.
```
$ brew install httpd-tools
```



* AB 에 대한 간략한 설명 및 노트북 인스톨

* json 데이터 설명, .sh 설명

* 테스트 실행.

- AB


* /site-address/order/add
* /site-address/order/event-add

<< Performance Graph >>


### Addtional Contents (Planned) ##

- develop material in HA cases, how it works and how many times is required for completion of HA

- deep dive to spring boot implementaion for transaction processing between redis and rdbms.

- pricing comparison (Well Architected view)

- 일관성 테스트...
