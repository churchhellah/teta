package org.teta;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageValidatorImpl implements MessageValidator {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean isValidJson(String json) {
        try {
            objectMapper.readTree(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean containsMsisdn(String json) {
        try {
            return objectMapper.readTree(json).has("msisdn");
        } catch (Exception e) {
            return false;
        }
    }
}
