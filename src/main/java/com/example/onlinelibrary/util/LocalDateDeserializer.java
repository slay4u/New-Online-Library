package com.example.onlinelibrary.util;

import com.example.onlinelibrary.exception.LocalDateParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateDeserializer extends StdDeserializer<LocalDate> {

    public LocalDateDeserializer() {
        this(null);
    }

    public LocalDateDeserializer(Class<?> vc) {
        super(vc);
    }

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public LocalDate deserialize(JsonParser jp, DeserializationContext context)
            throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        LocalDate parsedLocalDate;
        try {
            parsedLocalDate = LocalDate.parse(node.asText(), dateFormat);
        } catch (DateTimeParseException e) {
            throw new LocalDateParseException("LocalDate field deserialization failed! Either empty or invalid pattern!");
        }
        return parsedLocalDate;
    }
}
