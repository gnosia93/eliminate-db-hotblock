
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



### Infra Setup ###

- cloudformation 


### Application Setup ###



### App Execution ###

```
$ java -Dspring.profiles.active=prod -Dserver.port=8080 -jar democache-0.0.1-SNAPSHOT.jar

```


### BenchMark ###

- AB
- Performance Graph


### Pricing ###

- Well Architected.



