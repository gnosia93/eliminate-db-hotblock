#! /bin/sh
#java -jar ./target/mbp-0.0.1-SNAPSHOT.jar --server.port=80
nohup java -Dspring.profiles.active=prod -jar /home/ec2-user/deploy/target/mbp-0.0.1-SNAPSHOT.jar --server.port=80 > /home/ec2-user/tomcat.log 2>&1 &
exit 0

