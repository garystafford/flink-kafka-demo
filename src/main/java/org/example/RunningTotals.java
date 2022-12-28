package org.example;

// Purpose: Read sales transaction data from a Kafka topic,
//          aggregates transactions, quantities, and sales by product,
//          and writes results to a second Kafka topic.
// Author:  Gary A. Stafford
// Date: 2022-12-28

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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RunningTotals {

    public static void main(String[] args) throws Exception {
        Properties prop = getProperties();
        flinkKafkaPipeline(prop);
    }

    private static Properties getProperties() {
        Properties prop = new Properties();

        try (InputStream propsInput =
                     JoinStreams.class.getClassLoader().getResourceAsStream("config.properties")) {
            prop.load(propsInput);
            return prop;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }

    public static void flinkKafkaPipeline(Properties prop) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // assumes PLAINTEXT authentication
        KafkaSource<Purchase> source = KafkaSource.<Purchase>builder()
                .setBootstrapServers(prop.getProperty("BOOTSTRAP_SERVERS"))
                .setTopics(prop.getProperty("PURCHASES_TOPIC"))
                .setGroupId("flink_reduce_demo")
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
                .setBootstrapServers(prop.getProperty("BOOTSTRAP_SERVERS"))
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic(prop.getProperty("RUNNING_TOTALS_TOPIC"))
                        .setValueSerializationSchema(new RunningTotalSerializationSchema())
                        .build()
                ).setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();

        runningTotals.sinkTo(sink);

        env.execute("Flink Running Totals Demo");

    }
}
