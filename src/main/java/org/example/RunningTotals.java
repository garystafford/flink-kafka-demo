package org.example;

// Purpose: Read sales transaction data from a Kafka topic,
//          aggregates product transactions, quantities, and sales on data stream,
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
import org.example.model.RunningTotal;
import org.example.schema.PurchaseDeserializationSchema;
import org.example.schema.RunningTotalSerializationSchema;

public class RunningTotals {

    // assumes PLAINTEXT authentication
    final static String BOOTSTRAP_SERVERS = "kafka:29092";
    final static String CONSUMER_GROUP_ID = "flink_reduce_demo";
    final static String INPUT_TOPIC = "demo.purchases";
    final static String OUTPUT_TOPIC = "demo.running.totals";

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

        DataStream<RunningTotal> runningTotals = purchases
                .flatMap((FlatMapFunction<Purchase, RunningTotal>) (purchase, out) -> out.collect(
                        new RunningTotal(
                                purchase.getTransactionTime(),
                                purchase.getProductId(),
                                1,
                                purchase.getQuantity(),
                                purchase.getTotalPurchase()
                        ))
                ).returns(RunningTotal.class)
                .keyBy(RunningTotal::getProductId)
                .reduce((runningTotal1, runningTotal2) -> {
                    runningTotal2.setTransactions(runningTotal1.getTransactions() + runningTotal2.getTransactions());
                    runningTotal2.setQuantities(runningTotal1.getQuantities() + runningTotal2.getQuantities());
                    runningTotal2.setSales(runningTotal1.getSales().add(runningTotal2.getSales()));
                    return runningTotal2;
                });

        KafkaSink<RunningTotal> sink = KafkaSink.<RunningTotal>builder()
                .setBootstrapServers(BOOTSTRAP_SERVERS)
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic(OUTPUT_TOPIC)
                        .setValueSerializationSchema(new RunningTotalSerializationSchema())
                        .build()
                )
                .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();

        runningTotals.sinkTo(sink);

        env.execute("Running Totals Demo");

    }
}
