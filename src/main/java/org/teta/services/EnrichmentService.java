package org.teta.services;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
//import lombok.var;
import org.teta.EnrichmentProvider;
import org.teta.MessageValidator;
import org.teta.dto.Message;

import java.util.ArrayList;
import java.util.List;

public class EnrichmentService {
    private final MessageValidator validator;
    private final EnrichmentProvider enrichmentProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<String> enrichedMessages = new ArrayList<>();
    private final List<String> failedMessages = new ArrayList<>();

    public EnrichmentService(MessageValidator validator, EnrichmentProvider enrichmentProvider) {
        this.validator = validator;
        this.enrichmentProvider = enrichmentProvider;
    }

    public String enrich(Message message) {
        String content = message.getContent();

        if (!validator.isValidJson(content) || !validator.containsMsisdn(content)) {
            failedMessages.add(content);
            return content;
        }

        try {
            JsonNode jsonNode = objectMapper.readTree(content);
            String msisdn = jsonNode.get("msisdn").asText();
            var enrichment = enrichmentProvider.enrich(msisdn);

            if (enrichment.isPresent()) {
                ((ObjectNode) jsonNode).putPOJO("enrichment", enrichment.get());
                String enrichedContent = objectMapper.writeValueAsString(jsonNode);
                enrichedMessages.add(enrichedContent);
                return enrichedContent;
            }
        } catch (Exception e) {
            // TODO логгер
        }

        failedMessages.add(content);
        return content;
    }

    public List<String> getEnrichedMessages() {
        return new ArrayList<>(enrichedMessages);
    }

    public List<String> getFailedMessages() {
        return new ArrayList<>(failedMessages);
    }
}
