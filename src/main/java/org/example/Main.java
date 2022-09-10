package org.example;

// Purpose: Read sales transaction data from a Kafka topic,
//          aggregates product quantities and total sales on data stream,
//          and writes results to a second Kafka topic.
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

import java.time.Instant;
import java.time.LocalDateTime;

public class Main {

    // assumes PLAINTEXT authentication
    static String BOOTSTRAP_SERVERS = "kafka:29092";
    static String CONSUMER_GROUP_ID = "flink_demo";
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

        DataStream<Purchase> purchases = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");

        DataStream<Total> runningTotals = purchases
                .flatMap((FlatMapFunction<Purchase, Total>) (purchase, out) -> out.collect(
                        new Total(
                                purchase.getTransactionTime(),
                                purchase.getProductId(),
                                1,
                                purchase.getQuantity(),
                                purchase.getTotalPurchase()
                        ))
                ).returns(Total.class)
                .keyBy(Total::getProductId)
                .reduce((total1, total2) -> {
                    total2.setTransactions(total1.getTransactions() + total2.getTransactions());
                    total2.setQuantities(total1.getQuantities() + total2.getQuantities());
                    total2.setSales(total1.getSales().add(total2.getSales()));
                    return total2;
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
