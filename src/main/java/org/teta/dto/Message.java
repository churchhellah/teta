package org.teta.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String action;
    private String page;
    @NotBlank(message = "msisdn must not be null and must contain at least one non-whitespace character")
    private String msisdn;
    private EnrichmentType enrichmentType;
    public enum EnrichmentType {
        MSISDN
    }
}
