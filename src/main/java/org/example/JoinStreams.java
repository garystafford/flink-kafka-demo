package org.example;

// Purpose: Read products data and sales transaction data from a Kafka topic,
//          joins both datasets into enriched purchases,
//          and writes results to a second Kafka topic.
// Author:  Gary A. Stafford
// Date: 2022-09-12

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.example.model.Product;
import org.example.model.Purchase;
import org.example.model.PurchaseEnriched;
import org.example.schema.ProductDeserializationSchema;
import org.example.schema.PurchaseDeserializationSchema;
import org.example.schema.PurchaseEnrichedSerializationSchema;

public class JoinStreams {

    // assumes PLAINTEXT authentication
    final static String BOOTSTRAP_SERVERS = "kafka:29092";
    final static String CONSUMER_GROUP_ID = "flink_join_demo";
    final static String PRODUCT_TOPIC = "demo.products";
    final static String PURCHASE_TOPIC = "demo.purchases";
    final static String PURCHASES_ENRICHED_TOPIC = "demo.purchases.enriched";

    public static void main(String[] args) throws Exception {
        flinkKafkaPipeline();
    }

    public static void flinkKafkaPipeline() throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        KafkaSource<Product> productSource = KafkaSource.<Product>builder()
                .setBootstrapServers(BOOTSTRAP_SERVERS)
                .setTopics(PRODUCT_TOPIC)
                .setGroupId(CONSUMER_GROUP_ID)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new ProductDeserializationSchema())
                .build();

        DataStream<Product> productsStream = env.fromSource(
                productSource, WatermarkStrategy.noWatermarks(), "Kafka Product Source");

        tableEnv.createTemporaryView("products", productsStream);

        KafkaSource<Purchase> purchasesSource = KafkaSource.<Purchase>builder()
                .setBootstrapServers(BOOTSTRAP_SERVERS)
                .setTopics(PURCHASE_TOPIC)
                .setGroupId(CONSUMER_GROUP_ID)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new PurchaseDeserializationSchema())
                .build();

        DataStream<Purchase> purchasesStream = env.fromSource(
                purchasesSource, WatermarkStrategy.noWatermarks(), "Kafka Purchases Source");

        tableEnv.createTemporaryView("purchases", purchasesStream);

        Table result =
                tableEnv.sqlQuery(
                        "SELECT " +
                                "purchases.transactionTime, " +
                                "purchases.productId, " +
                                "products.category, " +
                                "products.item, " +
                                "products.size, " +
                                "products.cogs, " +
                                "products.price, " +
                                "products.containsFruit, " +
                                "products.containsVeggies, " +
                                "products.containsNuts, " +
                                "products.containsCaffeine, " +
                                "purchases.price, " +
                                "purchases.quantity, " +
                                "purchases.isMember, " +
                                "purchases.memberDiscount, " +
                                "purchases.addSupplements, " +
                                "purchases.supplementPrice, " +
                                "purchases.totalPurchase " +
                        "FROM " +
                                "products " +
                                    "JOIN purchases " +
                                        "ON products.productId = purchases.productId"
                );

        DataStream<PurchaseEnriched> purchasesEnrichedTable = tableEnv.toDataStream(result,
                PurchaseEnriched.class);

        KafkaSink<PurchaseEnriched> sink = KafkaSink.<PurchaseEnriched>builder()
                .setBootstrapServers(BOOTSTRAP_SERVERS)
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic(PURCHASES_ENRICHED_TOPIC)
                        .setValueSerializationSchema(new PurchaseEnrichedSerializationSchema())
                        .build()
                )
                .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();

        purchasesEnrichedTable.sinkTo(sink);

        env.execute("Stream Join Demo");

    }
}
