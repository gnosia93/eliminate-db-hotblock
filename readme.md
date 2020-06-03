
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



### Infra Building ###

Here, we will use AWS cloudformation to automate painfull and error-prone infrastucture building. 
You can find cloudformation configuration file which name is stack-build.yaml in the subdirectory of this project.

Go to AWS Cloudformation console, and with stack-build.yaml you need to build infrasture of this project. 
Normally it takes roughly 10 minitues for all infra provisioning.
If you are not good at AWS Cloudformation, refer to this URL (https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/GettingStarted.html)

Below screen is the example outputs of cloudformation which can be founded at statck menu.
You can identify web, api endpoint url, and provisioned EC2 instances public address, etc.

![cf-outputs](https://github.com/gnosia93/demo-cache/blob/master/document/cf-outputs.png)

Addtionally you have to clone this repository from all your ec2 instances.
Login into each ec2 instnaces and execute commands like followings.

```
$ ssh -i your-ec2-key.pem ec2-user@your-ec2-instance-publicname

$ git clone https://github.com/gnosia93/demo-cache.git
$ cd democache

```







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



