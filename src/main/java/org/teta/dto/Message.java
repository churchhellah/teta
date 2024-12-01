package org.teta.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Класс для передачи данных

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String content;
    private EnrichmentType enrichmentType;

    public enum EnrichmentType {
        MSISDN
    }
}