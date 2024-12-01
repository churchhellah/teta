package org.teta.service;


import org.teta.dto.UserInfo;

// Интерфейс для хранения информации о пользователях
public interface UserRepository {

    UserInfo getUserInfo (String msisdn);

}
