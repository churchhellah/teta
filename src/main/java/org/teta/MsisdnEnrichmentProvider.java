package org.teta;

import org.teta.dto.EnrichmentData;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MsisdnEnrichmentProvider implements EnrichmentProvider {
    private final Map<String, EnrichmentData> userData = new ConcurrentHashMap<>();

    public MsisdnEnrichmentProvider() {
        // Пример данных
        userData.put("88005553535", new EnrichmentData("Vasya", "Ivanov"));
        userData.put("1234567890", new EnrichmentData("Petr", "Petrov"));
    }

    @Override
    public Optional<EnrichmentData> enrich(String msisdn) {
        return Optional.ofNullable(userData.get(msisdn));
    }
}
