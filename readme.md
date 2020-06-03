
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

Here, we are going to use AWS cloudformation to automate painfull and error-prone infrastucture building. 
You can fild build configuration file which name is stack-build.yaml in subdirectory of this project.

Go to AWS Cloudformation console, and with stack-build.yaml file you need to build infrasture of this project. 
Normally it takes roughly 10 minitues for privision completion.
If you are not good at AWS Cloudformation, refer to this URL (https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/GettingStarted.html)

After login into your EC2 instances provisioned by cloudformation, you need to clone this repository at ec2-user home directory and do some configuration job before application running.








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



