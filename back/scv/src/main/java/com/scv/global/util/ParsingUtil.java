package com.scv.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ParsingUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true)
            .enable(SerializationFeature.INDENT_OUTPUT);

    public static <T> T parseJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSON to " + clazz.getSimpleName(), e);
        }
    }

    public static String toJson(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert data to JSON", e);
        }
    }

    public static String getJsonFieldAsString(JsonNode rootNode, String fieldName) {
        JsonNode fieldNode = rootNode.path(fieldName);
        try {
            return fieldNode.isMissingNode() || fieldNode.isNull() || fieldNode.toString().isEmpty() ? "{}" : objectMapper.writeValueAsString(fieldNode);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
