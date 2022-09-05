# Apache Flink / Apache Kafka Streaming Data Demonstration

Apache Flink / Apache Kafka streaming data demonstration
using [Streaming Synthetic Sales Data Generator](https://github.com/garystafford/streaming-sales-generator). Outputs
running total of individual drink quantities and total purchases to output Kafka topic. Demonstration uses Kafka/Flink 
Docker Swarm Stack from 'Sales Data Generator' project.

## Input Purchases Topic

Sample messages:

```txt
{"transaction_time":"2022-09-0503:28:23.492804","product_id":"CS07","price":4.99,"quantity":1,"is_member":false,"member_discount":0.0,"add_supplements":false,"supplement_price":0.0,"total_purchase":4.99}
{"transaction_time":"2022-09-0503:28:26.719131","product_id":"SF02","price":5.99,"quantity":3,"is_member":true,"member_discount":0.1,"add_supplements":true,"supplement_price":1.99,"total_purchase":21.55}
{"transaction_time":"2022-09-0503:28:29.001819","product_id":"CS06","price":4.99,"quantity":3,"is_member":true,"member_discount":0.1,"add_supplements":false,"supplement_price":0.0,"total_purchase":13.47}
{"transaction_time":"2022-09-0503:28:32.147178","product_id":"SF07","price":5.99,"quantity":2,"is_member":true,"member_discount":0.1,"add_supplements":false,"supplement_price":0.0,"total_purchase":10.78}
{"transaction_time":"2022-09-0503:28:34.377841","product_id":"SF02","price":5.99,"quantity":2,"is_member":true,"member_discount":0.1,"add_supplements":true,"supplement_price":1.99,"total_purchase":14.36}
{"transaction_time":"2022-09-0503:28:37.604685","product_id":"SF07","price":5.99,"quantity":1,"is_member":false,"member_discount":0.0,"add_supplements":true,"supplement_price":1.99,"total_purchase":7.98}
{"transaction_time":"2022-09-0503:28:39.734322","product_id":"SF07","price":5.99,"quantity":1,"is_member":true,"member_discount":0.1,"add_supplements":false,"supplement_price":0.0,"total_purchase":5.39}
{"transaction_time":"2022-09-0503:28:42.862669","product_id":"SC04","price":5.99,"quantity":1,"is_member":false,"member_discount":0.0,"add_supplements":true,"supplement_price":1.99,"total_purchase":7.98}
{"transaction_time":"2022-09-0503:28:44.083328","product_id":"CS02","price":4.99,"quantity":1,"is_member":false,"member_discount":0.0,"add_supplements":false,"supplement_price":0.0,"total_purchase":4.99}
{"transaction_time":"2022-09-0503:28:45.327688","product_id":"SF05","price":5.99,"quantity":1,"is_member":true,"member_discount":0.1,"add_supplements":false,"supplement_price":0.0,"total_purchase":5.39}
```

## Output Totals Topic

Sample messages:

```txt
{"transaction_time":"2022-09-05T03:23:40.645734","product_id":"IS03","quantity":25,"total_purchases":138.06999}
{"transaction_time":"2022-09-05T03:23:40.676989","product_id":"SF05","quantity":17,"total_purchases":125.29001}
{"transaction_time":"2022-09-05T03:23:40.687200","product_id":"CS02","quantity":19,"total_purchases":107.240005}
{"transaction_time":"2022-09-05T03:23:40.670973","product_id":"CS06","quantity":13,"total_purchases":73.31999}
{"transaction_time":"2022-09-05T03:23:40.676989","product_id":"SF05","quantity":19,"total_purchases":141.25002}
{"transaction_time":"2022-09-05T03:23:40.670878","product_id":"CS07","quantity":19,"total_purchases":99.86997}
{"transaction_time":"2022-09-05T03:23:40.687200","product_id":"CS02","quantity":20,"total_purchases":112.23}
{"transaction_time":"2022-09-05T03:23:40.687126","product_id":"CS04","quantity":11,"total_purchases":58.869995}
{"transaction_time":"2022-09-05T03:23:40.661675","product_id":"SC01","quantity":14,"total_purchases":92.600006}
{"transaction_time":"2022-09-05T03:23:40.676894","product_id":"SC04","quantity":17,"total_purchases":112.15999}
```

## Sample Apache Flink Dashboard Screengrabs

![Apache Flink Dashboard 1](screengrabs/flink_dashboard1.png)

![Apache Flink Dashboard 2](screengrabs/flink_dashboard2.png)

## Helpful Commands

```shell
./gradlew clean shadowJar

# Upload via the Flink UI or copy to Flink Docker image

FLINK_CONTAINER="4aca1f0e5940"

docker cp build/libs/flink-kafka-demo-1.0-SNAPSHOT-all.jar ${FLINK_CONTAINER}:/tmp

docker exec -it ${FLINK_CONTAINER} bash

flink run -c org.example.Main /tmp/flink-kafka-demo-1.0-SNAPSHOT-all.jar
```

## References

* <https://www.baeldung.com/kafka-flink-data-pipeline>
* <https://github.com/eugenp/tutorials/tree/master/apache-kafka/src/main/java/com/baeldung/flink>