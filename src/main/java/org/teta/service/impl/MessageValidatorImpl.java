package org.teta.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.teta.service.MessageValidator;

// Реализация валидатора сообщений
public class MessageValidatorImpl implements MessageValidator {
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Проверка на то, является ли переданное сообщение JSON-ом
    @Override
    public boolean isValidJson(String json) {
        // Если переданная строка пустая или null, то вернуть false
        if (json == null || json.isBlank()) {
            return false;
        }
        try {
            // Если удалось десериализовать переданную строку, то вернуть true
            objectMapper.readTree(json);
            return true;
            // Если не удалось (IOException или StreamReadException), то вернуть false
        } catch (Exception e) {
            return false;
        }
    }

    // Проверка на то, содержит ли переданный JSON параметр "msisdn" и что параметр не пустой
    @Override
    public boolean isContainsMsisdn(String json) {
        try {
            // hasNonNull - проверяем, что в строке есть параметр "msisdn" что он не null
            // !..asText().isBlank() - проверяем, что стока не пустая или, что строка состоит не только из символов пробела
            return (objectMapper.readTree(json).hasNonNull("msisdn")) &&
                    (!objectMapper.readTree(json).get("msisdn").asText().isBlank());
        } catch (Exception e) {
            return false;
        }
    }
}
