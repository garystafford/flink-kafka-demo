# Apache Flink / Apache Kafka Streaming Analytics Demo

Apache Flink / Apache Kafka streaming data analytics demonstration
using [Streaming Synthetic Sales Data Generator](https://github.com/garystafford/streaming-sales-generator). Outputs
running total of individual drink quantities and total purchases to output Kafka topic.

* Demonstration uses
  Kafka/Flink [Docker Swarm Stack](https://github.com/garystafford/streaming-sales-generator/blob/main/docker-compose.yml)
  from 'Sales Data Generator' project.

* Uber JAR for demonstration built with Gradle using JDK 8 Update 121.

## Video Demonstration

Short [YouTube video](https://youtu.be/ja0M_2zdbfs) demonstration of this project (video only - no audio).

## Input Topic: Purchases

Sample messages:

```txt
{"transaction_time": "2022-09-05 13:01:49.530434", "product_id": "IS01", "price": 5.49, "quantity": 2, "is_member": false, "member_discount": 0.0, "add_supplements": false, "supplement_price": 0.0, "total_purchase": 10.98}
{"transaction_time": "2022-09-05 13:01:50.761210", "product_id": "IS02", "price": 5.49, "quantity": 1, "is_member": false, "member_discount": 0.0, "add_supplements": false, "supplement_price": 0.0, "total_purchase": 5.49}
{"transaction_time": "2022-09-05 13:01:53.005900", "product_id": "SC05", "price": 5.99, "quantity": 1, "is_member": true, "member_discount": 0.1, "add_supplements": true, "supplement_price": 1.99, "total_purchase": 7.18}
{"transaction_time": "2022-09-05 13:01:55.283643", "product_id": "SC04", "price": 5.99, "quantity": 1, "is_member": false, "member_discount": 0.0, "add_supplements": false, "supplement_price": 0.0, "total_purchase": 5.99}
{"transaction_time": "2022-09-05 13:01:58.074699", "product_id": "CS08", "price": 4.99, "quantity": 1, "is_member": false, "member_discount": 0.0, "add_supplements": false, "supplement_price": 0.0, "total_purchase": 4.99}
{"transaction_time": "2022-09-05 13:02:00.938037", "product_id": "SF05", "price": 5.99, "quantity": 1, "is_member": false, "member_discount": 0.0, "add_supplements": true, "supplement_price": 1.99, "total_purchase": 7.98}
{"transaction_time": "2022-09-05 13:02:03.193997", "product_id": "IS02", "price": 5.49, "quantity": 1, "is_member": true, "member_discount": 0.1, "add_supplements": false, "supplement_price": 0.0, "total_purchase": 4.94}
{"transaction_time": "2022-09-05 13:02:04.587453", "product_id": "IS03", "price": 5.49, "quantity": 3, "is_member": true, "member_discount": 0.1, "add_supplements": false, "supplement_price": 0.0, "total_purchase": 14.82}
{"transaction_time": "2022-09-05 13:02:05.741791", "product_id": "CS10", "price": 4.99, "quantity": 1, "is_member": false, "member_discount": 0.0, "add_supplements": false, "supplement_price": 0.0, "total_purchase": 4.99}
{"transaction_time": "2022-09-05 13:02:07.981253", "product_id": "CS04", "price": 4.99, "quantity": 1, "is_member": false, "member_discount": 0.0, "add_supplements": false, "supplement_price": 0.0, "total_purchase": 4.99}
```

## Output Topic: Totals

Sample messages:

```txt
{"event_time":"2022-09-05T12:55:16.185565","product_id":"CS08","quantity":20,"total_purchases":106.76}
{"event_time":"2022-09-05T12:55:16.188792","product_id":"IS01","quantity":4,"total_purchases":21.96}
{"event_time":"2022-09-05T12:55:16.171876","product_id":"CS09","quantity":10,"total_purchases":48.90}
{"event_time":"2022-09-05T12:56:33.800787","product_id":"SC04","quantity":16,"total_purchases":106.17}
{"event_time":"2022-09-05T12:56:33.800787","product_id":"SC04","quantity":17,"total_purchases":114.15}
{"event_time":"2022-09-05T12:55:16.185565","product_id":"CS08","quantity":21,"total_purchases":113.74}
{"event_time":"2022-09-05T12:55:16.169814","product_id":"SC05","quantity":12,"total_purchases":84.01}
{"event_time":"2022-09-05T12:55:16.173546","product_id":"SF07","quantity":12,"total_purchases":77.24}
{"event_time":"2022-09-05T12:55:16.188792","product_id":"IS01","quantity":5,"total_purchases":29.44}
{"event_time":"2022-09-05T12:55:16.185565","product_id":"CS08","quantity":23,"total_purchases":123.72}
```

## Apache Flink Dashboard Preview

![Apache Flink Dashboard 1](screengrabs/flink_dashboard1.png)

![Apache Flink Dashboard 2](screengrabs/flink_dashboard2.png)

## Compile and Run Flink Job

```shell
# build uber jar using Gradle
./gradlew clean shadowJar

# Upload via the Flink UI or copy to Flink Docker image
FLINK_CONTAINER=$(docker container ls --filter  name=kafka-flink_jobmanager --format "{{.ID}}")
docker cp build/libs/flink-kafka-demo-1.0-SNAPSHOT-all.jar ${FLINK_CONTAINER}:/tmp
docker exec -it ${FLINK_CONTAINER} bash

flink run -c org.example.Main /tmp/flink-kafka-demo-1.0-SNAPSHOT-all.jar
```

## Check the Results

```shell
export BOOTSTRAP_SERVERS="localhost:9092"
export INPUT_TOPIC="demo.purchases"
export OUTPUT_TOPIC="demo.totals"

kafka-console-consumer.sh \
    --bootstrap-server $BOOTSTRAP_SERVERS \
    --topic $INPUT_TOPIC --from-beginning

kafka-console-consumer.sh \
    --bootstrap-server $BOOTSTRAP_SERVERS \
    --topic $OUTPUT_TOPIC --from-beginning
```

## Docker Stack

See [bitnami/kafka](https://hub.docker.com/r/bitnami/kafka) on Docker Hub for more information about running Kafka
locally using Docker.

```shell
# optional: delete previous stack
docker stack rm kafka-flink

# deploy kafka stack
docker swarm init
docker stack deploy kafka-flink --compose-file docker-compose.yml

# optional: to exec into Kafka container
docker exec -it $(docker container ls --filter  name=kafka-flink_kafka --format "{{.ID}}") bash
```

### Containers

Example containers:

```text
CONTAINER ID   IMAGE                      PORTS                                    NAMES
69ad1556eb3a   flink:latest               6123/tcp, 8081/tcp                       kafka-flink_taskmanager.1...
9f9b8e43eb21   flink:latest               6123/tcp, 8081/tcp                       kafka-flink_jobmanager.1...
6114dc4a9824   bitnami/kafka:latest       9092/tcp                                 kafka-flink_kafka.1...
837c0cdd1498   bitnami/zookeeper:latest   2181/tcp, 2888/tcp, 3888/tcp, 8080/tcp   kafka-flink_zookeeper.1...
```

## References

* <https://www.baeldung.com/kafka-flink-data-pipeline>
* <https://github.com/eugenp/tutorials/tree/master/apache-kafka/src/main/java/com/baeldung/flink>

---

The contents of this repository represent my viewpoints and not of my past or current employers, including Amazon Web Services (AWS). All third-party libraries, modules, plugins, and SDKs are the property of their respective owners. The author(s) assumes no responsibility or liability for any errors or omissions in the content of this site. The information contained in this site is provided on an "as is" basis with no guarantees of completeness, accuracy, usefulness or timeliness.
