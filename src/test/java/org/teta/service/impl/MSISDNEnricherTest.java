package org.teta.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.teta.dto.UserInfo;

import java.util.stream.Stream;

public class MSISDNEnricherTest {
    private InMemoryUserRepository userRepository;
    private MSISDNEnricher enricher;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        enricher = new MSISDNEnricher(userRepository);

        userRepository.addUserInfo("88005553535", new UserInfo("Vasya", "Ivanov"));
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void enrich(String content, String expectedResult) {
        Assertions.assertEquals(expectedResult, enricher.enrich(content));
    }

    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
                Arguments.of(
                        "{\"action\":\"button_click\",\"page\":\"book_card\",\"msisdn\":\"88005553535\"}",
                        "{\"action\":\"button_click\",\"page\":\"book_card\",\"msisdn\":\"88005553535\",\"enrichment\":{\"firstName\":\"Vasya\",\"lastName\":\"Ivanov\"}}"
                ),
                Arguments.of(
                        "{\"action\":\"button_click\",\"page\":\"book_card\"}",
                        "{\"action\":\"button_click\",\"page\":\"book_card\"}"
                )
        );
    }
}
