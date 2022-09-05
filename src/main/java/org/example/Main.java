package org.example;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.AllWindowedStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

import java.lang.reflect.GenericArrayType;
import java.text.DecimalFormat;
import java.text.NumberFormat;


public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello world!");
        ReadKafka();
    }


    public static void ReadKafka() throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<Purchase> source = KafkaSource.<Purchase>builder()
                .setBootstrapServers("kafka:29092")
                .setTopics("demo.purchases")
                .setGroupId("test_group")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new PurchaseDeserializationSchema())
                .build();

        env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");

        DataStream<Purchase> streamSource = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");

        KeyedStream<Purchase, String>  keyedStream = streamSource.keyBy(value -> value.productId);

        DataStream<Tuple3<String, Integer, Float>> totals = keyedStream.flatMap(new FlatMapFunction<Purchase, Tuple3<String, Integer, Float>>() {
            @Override
            public void flatMap(Purchase value, Collector<Tuple3<String, Integer, Float>> out) {
                out.collect(new Tuple3<>(value.getProductId(), value.getQuantity(), value.getTotalPurchase()));
            }
        });


        DataStream<Tuple3<String, Integer, Float>> runningTotals = totals
                .keyBy(value -> value.f0)
                .reduce((d1, d2) -> {
                    d1.f1 += d2.f1;
                    d1.f2 += d2.f2;
                    return d1;
                });

        KafkaSink<Tuple3<String, Integer, Float>> sink = KafkaSink.<Tuple3<String, Integer, Float>>builder()
                .setBootstrapServers("kafka:29092")
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic("demo.totals")
                        .setValueSerializationSchema(new SerializationSchema<Tuple3<String, Integer, Float>>() {
                            @Override
                            public void open(InitializationContext context) throws Exception {
                                SerializationSchema.super.open(context);
                            }

                            @Override
                            public byte[] serialize(Tuple3<String, Integer, Float> element) {
                                NumberFormat formatter = new DecimalFormat("0.00");
                                String message = String.format("%s: %s: %s", element.f0, element.f1, formatter.format(element.f2));
                                return message.getBytes();
                            }
                        })
                        .build()
                )
                .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();

//        KafkaSink<Purchase> sink = KafkaSink.<Purchase>builder()
//                .setBootstrapServers("kafka:29092")
//                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
//                        .setTopic("demo.output")
//                        .setValueSerializationSchema(new PurchaseSerializationSchema())
//                        .build()
//                )
//                .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
//                .build();

        runningTotals.sinkTo(sink);

        env.execute("Kafka Flink Demo");

    }
}
