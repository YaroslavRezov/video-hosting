package com.example.videohosting.utils;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.util.*;

import tools.jackson.databind.cfg.DateTimeFeature;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public final class TestUtils {

    private TestUtils() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    public static final JsonMapper JSON_MAPPER = prepareJsonMapper();

    public static String asJsonString(final Object obj) {
        try {
            return JSON_MAPPER.writeValueAsString(obj);
        } catch (JacksonException e) {
            throw new RuntimeException("Failed to convert object to JSON string", e);
        }
    }

    public static <T> T asObject(String json, Class<T> clazz) {
        try {
            return JSON_MAPPER.readValue(json, clazz);
        } catch (JacksonException e) {
            throw new RuntimeException("Failed to parse JSON string", e);
        }
    }

    public static <T> List<T> asList(String json, Class<T> clazz) {
        try {
            return JSON_MAPPER.readValue(json,
                    JSON_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (JacksonException e) {
            throw new RuntimeException("Failed to parse JSON string to list", e);
        }
    }

    private static JsonMapper prepareJsonMapper() {
        return JsonMapper.builder()
                // Dates are ISO-8601 by default in Jackson 3, so WRITE_DATES_AS_TIMESTAMPS
                // is already false. We keep the explicit disable for clarity.
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .changeDefaultPropertyInclusion(inclusion ->
                        inclusion.withValueInclusion(JsonInclude.Include.NON_NULL))
                .build();
    }
}