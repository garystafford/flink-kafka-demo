# Flink-Kafka Demo

```shell
./gradlew clean shadowJar

# Upload via the Flink UI or copy to Flink Docker image

FLINK_CONTAINER="4aca1f0e5940"

docker cp build/libs/flink-kafka-demo-1.0-SNAPSHOT-all.jar ${FLINK_CONTAINER}:/tmp

docker exec -it ${FLINK_CONTAINER} bash

flink run -c org.example.Main /tmp/flink-kafka-demo-1.0-SNAPSHOT-all.jar
```