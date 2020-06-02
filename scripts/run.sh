#!/bin/bash

nohup java -Dspring.profiles.active=prod --server.port=8080 -jar ../target/democache-0.0.1-SNAPSHOT.jar 2&> run.out

