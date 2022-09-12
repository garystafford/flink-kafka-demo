package org.example.schema;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.model.Product;

import java.io.IOException;

public class ProductDeserializationSchema implements
        DeserializationSchema<Product> {

    static ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Override
    public Product deserialize(byte[] bytes) throws IOException {
        return objectMapper.readValue(bytes, Product.class);
    }

    @Override
    public boolean isEndOfStream(Product product) {
        return false;
    }

    @Override
    public TypeInformation<Product> getProducedType() {
        return TypeInformation.of(Product.class);
    }
}
