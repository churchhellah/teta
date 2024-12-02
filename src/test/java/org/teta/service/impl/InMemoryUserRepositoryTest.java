package org.teta.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.teta.dto.UserInfo;

import java.util.stream.Stream;

public class InMemoryUserRepositoryTest {
    private InMemoryUserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository = new InMemoryUserRepository();
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    void getUserInfoTest(String msisdn, String firstName, String lastName) {
        userRepository.addUserInfo(msisdn, new UserInfo(firstName, lastName));
        UserInfo userInfo = userRepository.getUserInfo(msisdn);

        Assertions.assertNotNull(userInfo, "User info should not be null");
        Assertions.assertEquals(firstName, userInfo.getFirstName());
        Assertions.assertEquals(lastName, userInfo.getLastName());
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
                Arguments.of("88005553540", "Oleg", "Mongol"),
                Arguments.of("88005553541", "Olga", "Buzova"),
                Arguments.of("88005553542", "Irishka", "Chikipiki"),
                Arguments.of("88005553543", "Pasha", "Technique"),
                Arguments.of("88005553544", "Pasha", "Bezumniy")
        );
    }
}
