#!/bin/sh

kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic ocktank --from-beginning
