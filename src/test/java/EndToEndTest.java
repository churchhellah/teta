import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.teta.dto.Message;
import org.teta.dto.UserInfo;
import org.teta.service.EnrichmentService;
import org.teta.service.MessageEnricher;
import org.teta.service.MessageValidator;
import org.teta.service.UserRepository;
import org.teta.service.impl.InMemoryUserRepository;
import org.teta.service.impl.MSISDNEnricher;
import org.teta.service.impl.MessageValidatorImpl;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static junit.framework.Assert.assertEquals;

// End-to-End тест для проверки работы системы в многопоточной среде
@Slf4j
public class EndToEndTest {

    private InMemoryUserRepository userRepository;
    private MessageValidator messageValidator;
    private MessageEnricher enricher;
    private EnrichmentService enrichmentService;
    private boolean terminated;

    @BeforeEach
    public void setUp() {
        // Создаются экземпляры репозитория, валидатора, обогатителя и сервиса
        userRepository = new InMemoryUserRepository();
        messageValidator = new MessageValidatorImpl();
        enricher = new MSISDNEnricher(userRepository);
        enrichmentService = new EnrichmentService(messageValidator, enricher);

        // В репозиторий добавляется пользователь
        userRepository.addUserInfo("88005553535", new UserInfo("Vasya", "Ivanov"));
        userRepository.addUserInfo("88005553536", new UserInfo("Lucya", "Chebotina"));
        userRepository.addUserInfo("88005553537", new UserInfo("Egor", "Kreed"));
        userRepository.addUserInfo("88005553538", new UserInfo("Guf", "Dead"));
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    public void testConcurrentEnrichment(String messageContent, int threadCount, int taskCount) throws InterruptedException {
        // Создание пула потоков - максимум потоков в пуле получаем из входных параметров
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        // Создание синхронизатора для ожидания завершения всех задач
        // максимум задач в потоке берем из входных параметров
        CountDownLatch latch = new CountDownLatch(taskCount);

        // Цикл для создания и отправки 10 задач на выполнение
        for (int i = 0; i < taskCount; i++) {
            executorService.submit(() -> {
                // Каждая задача создает объект сообщения,
                // устанавливает его содержимое и тип обогащения,
                // вызывает метод обогащения сервиса и проверяет результат
                try {
                    Message message = new Message();
                    message.setContent(messageContent);
                    message.setEnrichmentType(Message.EnrichmentType.MSISDN);

                    String result = enrichmentService.enrich(message);

                    if (messageContent.equals("{\"action\":\"button_click\",\"page\":\"book_card\",\"msisdn\":\"88005553535\"}")) {
                        Assertions.assertEquals("{\"action\":\"button_click\",\"page\":\"book_card\",\"msisdn\":\"88005553535\",\"enrichment\":{\"firstName\":\"Vasya\",\"lastName\":\"Ivanov\"}}", result);
                    } else if (messageContent.equals("{\"msisdn\":\"88005553536\"}")) {
                        Assertions.assertEquals("{\"msisdn\":\"88005553536\",\"enrichment\":{\"firstName\":\"Lucya\",\"lastName\":\"Chebotina\"}}", result);
                    } else if (messageContent.equals("{\"msisdn\":\"88005553537\"}")) {
                        Assertions.assertEquals("{\"msisdn\":\"88005553537\",\"enrichment\":{\"firstName\":\"Egor\",\"lastName\":\"Kreed\"}}", result);
                    } else if (messageContent.equals("{\"msisdn\":\"88005553538\"}")) {
                        Assertions.assertEquals("{\"msisdn\":\"88005553538\",\"enrichment\":{\"firstName\":\"Guf\",\"lastName\":\"Dead\"}}", result);
                    }
                } catch (Exception e) {
                    log.error("Error occurred: {}", e);
                    Assertions.fail("Error during execution: " + e.getMessage());
                }
                finally {
                    // После завершения каждой задачи уменьшается счетчик синхронизатора
                    latch.countDown();
                }
            });
        }
        // Основной поток ждет, пока все задачи не завершатся, с таймаутом в 10 секунд
        latch.await(10, TimeUnit.SECONDS);
        // Завершение работы пула потоков
        executorService.shutdown();
        // Ожидание завершения всех задач в пуле потоков
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        terminated = executorService.awaitTermination(10, TimeUnit.SECONDS);
        Assertions.assertTrue(terminated, "Not all threads finished in time!");
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
                Arguments.of("{\"action\":\"button_click\",\"page\":\"book_card\",\"msisdn\":\"88005553535\"}", 10, 10),
                Arguments.of("{\"msisdn\":\"88005553536\"}", 5, 5),
                Arguments.of("{\"msisdn\":\"88005553537\"}", 15, 15),
                Arguments.of("{\"msisdn\":\"88005553538\"}", 20, 20)
        );
    }
}
