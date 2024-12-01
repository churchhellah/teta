package org.teta.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.teta.dto.Message;
import org.teta.dto.UserInfo;
import org.teta.service.impl.InMemoryUserRepository;
import org.teta.service.impl.MSISDNEnricher;
import org.teta.service.impl.MessageValidatorImpl;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EnrichmentServiceTest {

    private InMemoryUserRepository userRepository;
    private MessageValidator messageValidator;
    private MessageEnricher enricher;
    private EnrichmentService service;

    @BeforeEach
    public void setUp() {
        userRepository = new InMemoryUserRepository();
        messageValidator = new MessageValidatorImpl();
        enricher = new MSISDNEnricher(userRepository);
        service = new EnrichmentService(messageValidator, enricher);

        userRepository.addUserInfo("88005553535", new UserInfo("Vasya", "Ivanov"));
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void testEnrich(String content, String expectedResult) {
        Message message = new Message();
        message.setContent(content);
        message.setEnrichmentType(Message.EnrichmentType.MSISDN);

        String result = service.enrich(message);
        assertEquals(expectedResult, result);
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