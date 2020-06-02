#!/bin/bash

APP_HOME=/home/ec2-user/demo-cache
echo $APP_HOME; cd $APP_HOME
echo "when first execution, it takes about 3 minitues for required java packages download"; mvn package
nohup java -Dspring.profiles.active=prod -jar $APP_HOME/target/mbp-0.0.1-SNAPSHOT.jar --server.port=8080 > $APP_HOME/tomcat.log 2>&1 &


