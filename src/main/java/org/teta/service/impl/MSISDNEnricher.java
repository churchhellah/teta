package org.teta.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.teta.dto.EnrichedMessage;
import org.teta.dto.UserInfo;
import org.teta.service.MessageEnricher;
import org.teta.service.UserRepository;

// Реализация обогащения по MSISDN
public class MSISDNEnricher implements MessageEnricher {
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public MSISDNEnricher (UserRepository userRepository) {
        this.userRepository = userRepository;
        this.objectMapper = new ObjectMapper();
    }
    @Override
    public String enrich(String content) {
        try {
            EnrichedMessage enrichedMessage = objectMapper.readValue(content, EnrichedMessage.class);
            String msisdn = enrichedMessage.getMsisdn();
            UserInfo userInfo = userRepository.getUserInfo(msisdn);
            if (userInfo != null) {
                EnrichedMessage.Enrichment enrichment = new EnrichedMessage.Enrichment();
                enrichment.setFirstName(userInfo.getFirstName());
                enrichment.setLastName(userInfo.getLastName());
                enrichedMessage.setEnrichment(enrichment);
            }
            return objectMapper.writeValueAsString(enrichedMessage);
        } catch (Exception e) {
            return content;
        }
    }
}
