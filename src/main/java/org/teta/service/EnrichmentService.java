package org.teta.service;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.teta.dto.Message;

// Основной сервис для обогащения сообщений
@AllArgsConstructor
@Slf4j
public class EnrichmentService {
    private final MessageValidator validator;
    private final MessageEnricher enricher;

    public String enrich(Message message) {
        log.info("Method 'enrich' called with message {}", message);
        String content = message.getContent();
        String enrichedContent;
        log.info("Got content from received message: {}", content);

        // Если content НЕ валиден - обогащение сообщения не происходит,
        // content возвращается в неизменном виде
        if (!validator.isValidJson(content) || !validator.isContainsMsisdn(content)) {
            log.warn("Message validation failed. Enrichment aborting. Return {}", content);
            return content;
        }
        // Если content валиден - обогащаем сообщение,
        // content возвращается в обогащенным
        enrichedContent = enricher.enrich(content);
        log.info("Message validation success. Enrichment result: {}", enrichedContent);
        return enrichedContent;
    }
}
