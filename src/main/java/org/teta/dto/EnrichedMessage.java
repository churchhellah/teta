package org.teta.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@JsonPropertyOrder({
        "action",
        "page",
        "msisdn",
        "enrichment"
})
@Data
public class EnrichedMessage {
    private String action;
    private String page;
    private String msisdn;
    private Enrichment enrichment;

    @Data
    public static class Enrichment {
        private String firstName;
        private String lastName;
    }
}
