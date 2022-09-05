package org.example;

// Purpose: Read sales transaction data from Kafka topic,
//          implement transformations on the data stream,
//          and writes resulting data to a second Kafka topic.
// Author:  Gary A. Stafford
// Date: 2022-09-05

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.example.model.Purchase;
import org.example.model.Total;
import org.example.schema.PurchaseDeserializationSchema;
import org.example.schema.TotalSerializationSchema;

import java.time.LocalDateTime;

public class Main {

    // assumes PLAINTEXT authentication
    static String BOOTSTRAP_SERVERS = "kafka:29092";
    static String CONSUMER_GROUP_ID = "demo_group";
    static String INPUT_TOPIC = "demo.purchases";
    static String OUTPUT_TOPIC = "demo.totals";

    public static void main(String[] args) throws Exception {
        flinkKafkaPipeline();
    }

    public static void flinkKafkaPipeline() throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<Purchase> source = KafkaSource.<Purchase>builder()
                .setBootstrapServers(BOOTSTRAP_SERVERS)
                .setTopics(INPUT_TOPIC)
                .setGroupId(CONSUMER_GROUP_ID)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new PurchaseDeserializationSchema())
                .build();

        DataStream<Purchase> streamSource = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");

        DataStream<Total> runningTotals = streamSource
                .flatMap((FlatMapFunction<Purchase, Total>) (value, out) -> out.collect(
                        new Total(
                                LocalDateTime.now().toString(),
                                value.getProductId(),
                                value.getQuantity(),
                                value.getTotalPurchase()
                        ))
                ).returns(Total.class)
                .keyBy(Total::getProductId)
                .reduce((t1, t2) -> {
                    t1.setQuantity(t1.getQuantity() + t2.getQuantity());
                    t1.setTotalPurchases(t1.getTotalPurchases().add(t2.getTotalPurchases()));
                    return t1;
                });

        KafkaSink<Total> sink = KafkaSink.<Total>builder()
                .setBootstrapServers(BOOTSTRAP_SERVERS)
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic(OUTPUT_TOPIC)
                        .setValueSerializationSchema(new TotalSerializationSchema())
                        .build()
                )
                .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();

        runningTotals.sinkTo(sink);

        env.execute("Flink Kafka Pipeline");

    }
}
