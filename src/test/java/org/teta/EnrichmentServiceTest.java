package org.teta;

import org.junit.jupiter.api.Test;
import org.teta.dto.Message;
import org.teta.services.EnrichmentService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class EnrichmentServiceTest {

    @Test
    void testMultithreadedEnrichment() throws InterruptedException {
        MessageValidator validator = new MessageValidatorImpl();
        EnrichmentProvider provider = new MsisdnEnrichmentProvider();
        EnrichmentService service = new EnrichmentService(validator, provider);

        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10);

        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                try {
                    Message message = new Message();
                    message.setContent("""
                        {
                            "action": "button_click",
                            "page": "book_card",
                            "msisdn": "88005553535"
                        }
                    """);
                    message.setEnrichmentType(Message.EnrichmentType.MSISDN);

                    String result = service.enrich(message);
                    assertTrue(result.contains("Vasya"));
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(10, service.getEnrichedMessages().size());
        assertEquals(0, service.getFailedMessages().size());
    }
}