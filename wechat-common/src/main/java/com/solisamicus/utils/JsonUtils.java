package com.solisamicus.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class JsonUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);

    /**
     * Converts an object to its JSON string representation.
     *
     * @param data the object to be converted to JSON
     * @return the JSON string representation of the object, or {@code null} if an error occurs
     */
    public static String objectToJson(Object data) {
        try {
            return MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error converting object to JSON", e);
        }
        return null;
    }

    /**
     * Converts a JSON string to a Java object of the specified type.
     *
     * @param jsonData the JSON string to be converted
     * @param beanType the class of the type to convert the JSON string to
     * @param <T>      the type of the result
     * @return the Java object, or {@code null} if an error occurs
     */
    public static <T> T jsonToPojo(String jsonData, Class<T> beanType) {
        try {
            return MAPPER.readValue(jsonData, beanType);
        } catch (IOException e) {
            LOGGER.error("Error converting JSON to POJO", e);
        }
        return null;
    }

    /**
     * Converts a JSON string to a list of Java objects of the specified type.
     *
     * @param jsonData the JSON string to be converted
     * @param beanType the class of the type of elements in the list
     * @param <T>      the type of elements in the list
     * @return the list of Java objects, or {@code null} if an error occurs
     */
    public static <T> List<T> jsonToList(String jsonData, Class<T> beanType) {
        JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, beanType);
        try {
            return MAPPER.readValue(jsonData, javaType);
        } catch (IOException e) {
            LOGGER.error("Error converting JSON to List", e);
        }
        return null;
    }
}
