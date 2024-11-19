package org.teta;

import org.teta.dto.EnrichmentData;

import java.util.Optional;

public interface EnrichmentProvider {
    Optional<EnrichmentData> enrich(String msisdn);
}
