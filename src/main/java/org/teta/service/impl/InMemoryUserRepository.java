package org.teta.service.impl;

import org.teta.dto.UserInfo;
import org.teta.service.UserRepository;

import java.util.concurrent.ConcurrentHashMap;

// Реализация хранилища пользователей в памяти
public class InMemoryUserRepository implements UserRepository {
    private final ConcurrentHashMap<String, UserInfo> userMap = new ConcurrentHashMap<>();

    @Override
    public UserInfo getUserInfo(String msisdn) {
        return userMap.get(msisdn);
    }
    public void addUserInfo(String msisdn, UserInfo userInfo) {
        userMap.put(msisdn, userInfo);
    }
}
