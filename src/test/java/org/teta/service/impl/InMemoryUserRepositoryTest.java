package org.teta.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
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
        try {
            UserInfo userInfo = userRepository.getUserInfo(msisdn);
            if (msisdn.equals("88005553541")) {
                Assertions.assertEquals("Olga", userInfo.getFirstName());
                Assertions.assertEquals("Buzova", userInfo.getLastName());
            } else if (msisdn.equals("88005553542")) {
                Assertions.assertEquals("Irishka", userInfo.getFirstName());
                Assertions.assertEquals("Chikipiki", userInfo.getLastName());
            } else if (msisdn.equals("88005553543")) {
                Assertions.assertEquals("Pasha", userInfo.getFirstName());
                Assertions.assertEquals("Technique", userInfo.getLastName());
            } else if (msisdn.equals("88005553544")) {
                Assertions.assertEquals("Pasha", userInfo.getFirstName());
                Assertions.assertEquals("Bezumniy", userInfo.getLastName());
            }
        } catch (Exception e) {
            Assertions.fail("Error occurred" + e.getMessage());
        }
    }

    @Test
    void addUserInfoWithMsisdnAlreadyAddedTest() {

    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
                Arguments.of("88005553540, Oleg, Mongol"),
                Arguments.of("88005553541, Olga, Buzova"),
                Arguments.of("88005553542, Irishka, Chikipiki"),
                Arguments.of("88005553543, Pasha, Technique"),
                Arguments.of("88005553544, Pasha, Bezumniy")
        );
    }
}
