package org.cubegame.infrastructure.repositories.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.cubegame.application.exceptions.incident.internal.Internal;
import org.cubegame.application.exceptions.incident.internal.InternalError;

public class Transformations {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static  <T> String toJson(T object) {
        final String json;
        try {
            json = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new InternalError(Internal.Database.Data.MAPPING, "Cannot serialize object.");
        }
        return json;
    }

    public static  <T> T toObject(Document document, Class<T> clazz) {
        final T object;
        try {
            object = objectMapper.readValue(document.toJson(), clazz);
        } catch (JsonProcessingException e) {

            // TODO add MDC
            throw new InternalError(Internal.Database.Data.PARSING, "Cannot deserialize object.");
        }
        return object;
    }



}
