package org.example;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

public class InputMessageDeserializationSchema implements
        DeserializationSchema<Purchase> {

    static ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Override
    public Purchase deserialize(byte[] bytes) throws IOException {
        return objectMapper.readValue(bytes, Purchase.class);
    }

    @Override
    public boolean isEndOfStream(Purchase inputMessage) {
        return false;
    }

    @Override
    public TypeInformation<Purchase> getProducedType() {
        return TypeInformation.of(Purchase.class);
    }
}
