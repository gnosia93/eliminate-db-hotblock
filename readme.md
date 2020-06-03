
# Improving Appication Performance with AWS ElastiCache #

- DB Result Caching
- Session Store
- Eliminating DB Hot Blocks when heavy transcation occurs in small range of data. 

In this blog, We are going to go through how to eliminate database hotblock with amazon elasticache redis and
java springboot web application.

We don't deal with application implementation details in here, but If you are either developer or someone who can read
java language, you can easily catch up with details.


### Business Problems ###


### ElastiCache Briefs ###

Before diving deeply, I want to just introduce about AWS elastiCache for your understanding about this article.

### Architecture ###



### Infra Provisioning ###

Here, we are going to use AWS cloudformation to automate painfull and error-prone infrastucture setup. 
All operations is composed of two phases.
at phase I, you will upload stack-build.yaml file located in cloudformation directory of this demo into the your acccount s3 bucket.
at phase II, you will build infrasture using cloudformation. 
normally total required time is about above 10 minitues until all infra build is completed. 





- cloudformation 



### Preparing Sample DB ###

Next step is to create schema objects and initialize tables. please execute command like below at ec2 instance console,
and you need to confirm product table's row count is 10000. 

If you are good at MySQL database and compatibles, you can login auroa RDS using mysql client and check sample schema,
auto generated datas.

(optional) check and refer information in create-schame.sql file for login. 

```
$ cd /home/ec2-user/demo-cache/init/sql
$ sh create-schema.sh 
mysql: [Warning] Using a password on the command line interface can be insecure.
mysql: [Warning] Using a password on the command line interface can be insecure.
+-----------------+
| gen_product_cnt |
+-----------------+
|           10000 |
+-----------------+
```




### Application Setup ###



### App Execution ###

```
$ java -Dspring.profiles.active=prod -Dserver.port=8080 -jar democache-0.0.1-SNAPSHOT.jar

```


### BenchMark ###

- AB

```
$ yum install -y httpd-tools

```

- Performance Graph


### Pricing ###

- Well Architected.



