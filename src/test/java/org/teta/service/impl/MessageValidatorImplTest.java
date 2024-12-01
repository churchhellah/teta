package org.teta.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class MessageValidatorImplTest {
    MessageValidatorImpl validator;
    @BeforeEach
    public void setUp() {
        validator = new MessageValidatorImpl();
    }
    @ParameterizedTest
    @ValueSource(strings = {
            "{}",
            "{\"action\":\"button_click\",\"page\":\"book_card\",\"msisdn\":\"88005553535\"}",
            "{\"msisdn\":\"88005553535\"}"
    })
    void isValidJsonTrue(String json) {
        Assertions.assertTrue(validator.isValidJson(json));
}
    @ParameterizedTest
    @ValueSource(strings = {
            "{\"action\":\"button_click\",\"page\":\"book_card\",\"msisdn\":\"88005553535\"}",
            "{\"msisdn\":\"88005553535\"}"
    })
    void isContainsMsisdnTrue(String json) {
        Assertions.assertTrue(
                validator.isContainsMsisdn(json)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><note><to>Tove</to><from>Jani</from><heading>Reminder</heading><body>Don't forget me this weekend!</body></note>",
            "note:\n" +
                    "  to: Tove\n" +
                    "  from: Jani\n" +
                    "  heading: Reminder\n" +
                    "  body: Don't forget me this weekend!\n",
            ""
    })
    void isValidJsonFalse(String json) {
        Assertions.assertFalse(validator.isValidJson(json));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"action\":\"button_click\",\"page\":\"book_card\"}",
            "{\"action\":\"button_click\",\"page\":\"book_card\",\"phone\":\"88005553535\"}",
            "{\"msisdn\":\"\"}"
    })
    void isContainsMsisdnFalse(String json) {
        Assertions.assertFalse(validator.isContainsMsisdn(json));
    }
}