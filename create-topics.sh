#!/bin/bash

BOOTSTRAP_SERVER=localhost:29092

create_topic() {

local TOPIC=$1
local PARTITIONS=$2
local REPLICAS=$3

docker exec kafka-1 \
/opt/kafka/bin/kafka-topics.sh \
--bootstrap-server $BOOTSTRAP_SERVER \
--create \
--if-not-exists \
--topic $TOPIC \
--partitions $PARTITIONS \
--replication-factor $REPLICAS
}

create_topic "order.created" 3 3
create_topic "order.created.DLT" 3 3

create_topic "payment.validated" 3 3
create_topic "payment.validated.DLT" 3 3

create_topic "payment.failed" 3 3
create_topic "payment.failed.DLT" 3 3