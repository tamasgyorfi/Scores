package hu.bets.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.IOException;

public class JsonUtils {

    private static final Logger LOGGER = Logger.getLogger(JsonUtils.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            LOGGER.error("Unable to parse json: " + json, e);
        }

        return null;
    }

    public String toJson(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LOGGER.error("Unable to convert to json; object was: " + object);
        }

        return "";
    }

}
