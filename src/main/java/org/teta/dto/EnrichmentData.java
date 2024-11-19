package org.teta.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnrichmentData {
    private String firstName;
    private String lastName;

    public EnrichmentData(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
