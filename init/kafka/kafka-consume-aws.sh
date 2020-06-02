#!/bin/sh

kafka-console-consumer.sh --bootstrap-server b-1.kafka-public.f24lff.c2.kafka.ap-northeast-2.amazonaws.com:9092,b-2.kafka-public.f24lff.c2.kafka.ap-northeast-2.amazonaws.com:9092,b-3.kafka-public.f24lff.c2.kafka.ap-northeast-2.amazonaws.com:9092 --topic ocktank --from-beginning
