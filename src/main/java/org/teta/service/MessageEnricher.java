package org.teta.service;

// Интерфейс для обогащения сообщений
public interface MessageEnricher {
    // Метод, реализующий логику обогащения полученного сообщения новой информацией
    String enrich (String content);
}
