package org.teta.service;

// Интерфейс для валидации сообщений

public interface MessageValidator {

    // Флаг валидности сообщения (формат и т.д.)
    boolean isValidJson (String content);

    // Флаг налаичия в сообщении обязательного параметра msisdn
    boolean isContainsMsisdn(String content);
}