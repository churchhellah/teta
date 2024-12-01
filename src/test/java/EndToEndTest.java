import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
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

import static junit.framework.Assert.assertEquals;

// End-to-End тест для проверки работы системы в многопоточной среде
@Slf4j
public class EndToEndTest {

    // Объявляются переменные для хранения экземпляров репозитория, валидатора, обогатителя и сервиса
    private InMemoryUserRepository userRepository;
    private MessageValidator messageValidator;
    private MessageEnricher enricher;
    private EnrichmentService enrichmentService;

    // Метод выполняется перед каждым тестом для настройки окружения
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
    @ValueSource(strings = {
            "{\"action\":\"button_click\",\"page\":\"book_card\",\"msisdn\":\"88005553535\"}",
            "{\"msisdn\":\"88005553536\"}",
            "{\"msisdn\":\"88005553537\"}",
            "{\"msisdn\":\"88005553538\"}"
    })
    public void testConcurrentEnrichment(String messageContent) throws InterruptedException {
        // Создание пула потоков для параллельного выполнения задач
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        // Создание синхронизатора для ожидания завершения всех задач
        CountDownLatch latch = new CountDownLatch(10);

        // Цикл для создания и отправки 10 задач на выполнение
        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                // Каждая задача создает объект сообщения,
                // устанавливает его содержимое и тип обогащения,
                // вызывает метод обогащения сервиса и проверяет результат
                try {
                    Message message = new Message();
                    message.setContent(messageContent);
                    message.setEnrichmentType(Message.EnrichmentType.MSISDN);

                    String result = enrichmentService.enrich(message);
                    System.out.println("Input: " + messageContent);
                    System.out.println("Output: " + result);

//                    switch (messageContent) {
//                        case ("{\"action\":\"button_click\",\"page\":\"book_card\",\"msisdn\":\"88005553535\"}"):
//                            Assertions.assertEquals();
//                    }

                    Assertions.assertEquals("{\"action\":\"button_click\",\"page\":\"book_card\",\"msisdn\":\"88005553535\",\"enrichment\":{\"firstName\":\"Vasya\",\"lastName\":\"Ivanov\"}}", result);
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
    }
}
