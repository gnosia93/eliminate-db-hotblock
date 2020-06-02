#!/bin/bash

APP_HOME=/home/ec2-user/demo-cache
echo $APP_HOME; cd $APP_HOME
echo "at first execution, maven will download required java packages about for 3 min.."; mvn package
nohup java -Dspring.profiles.active=prod \
           -jar $APP_HOME/target/mbp-0.0.1-SNAPSHOT.jar \
           --server.port=8080 > /home/ec2-user/tomcat.log 2>&1 &


