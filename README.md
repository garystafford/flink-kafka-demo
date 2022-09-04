

```shell
./gradlew clean shadowJar

docker cp build/libs/flink-kafka-demo-1.0-SNAPSHOT-all.jar 4aca1f0e5940:/tmp

docker exec -it 4aca1f0e5940 bash

flink run -c org.example.Main /tmp/flink-kafka-demo-1.0-SNAPSHOT-all.jar
```