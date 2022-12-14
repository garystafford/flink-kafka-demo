package org.example.schema;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.model.Purchase;

import java.io.IOException;

public class PurchaseDeserializationSchema implements
        DeserializationSchema<Purchase> {

    static ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Override
    public Purchase deserialize(byte[] bytes) throws IOException {
        return objectMapper.readValue(bytes, Purchase.class);
    }

    @Override
    public boolean isEndOfStream(Purchase purchase) {
        return false;
    }

    @Override
    public TypeInformation<Purchase> getProducedType() {
        return TypeInformation.of(Purchase.class);
    }
}
