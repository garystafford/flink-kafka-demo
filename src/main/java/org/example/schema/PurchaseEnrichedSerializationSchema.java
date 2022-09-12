package org.example.schema;

import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.model.PurchaseEnriched;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PurchaseEnrichedSerializationSchema
        implements SerializationSchema<PurchaseEnriched> {

    ObjectMapper objectMapper;
    Logger logger = LoggerFactory.getLogger(PurchaseEnrichedSerializationSchema.class);

    @Override
    public byte[] serialize(PurchaseEnriched purchaseEnriched) {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper()
                    .registerModule(new JavaTimeModule());
        }
        try {
            return objectMapper.writeValueAsString(purchaseEnriched).getBytes();
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse JSON", e);
        }
        return new byte[0];
    }
}