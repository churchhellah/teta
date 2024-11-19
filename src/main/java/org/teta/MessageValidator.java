package org.teta;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.teta.dto.Message;

/*
    Сообщение должно быть в формате JSON.
    В JSON должно быть поле msisdn со строковым значением. Остальные поля произвольны.
    Если одно из условий не соблюдается (сообщение не формате JSON, поле msisdn отсутствует или информация не найдена),
    сообщение возвращается в том же виде, в котором пришло.
 */

public class MessageValidator {

    public Message validate(Message message) {
        try {
            // Попытка разобрать входящее сообщение, как JSON
            JsonElement jsonElement = JsonParser.parseString(String.valueOf(message));

            // Проверка, является ли jsonElement объектом
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                // Проверка наличия поля msisdn
                if (jsonObject.has("msisdn") &&
                        jsonObject.get("msisdn").isJsonPrimitive() &&
                        jsonObject.get("msisdn").getAsJsonPrimitive().isString() &&
                        jsonObject.get("msisdn").getAsString().length() == 11) {
                    // Валидация успешна - возвращаем исходное сообщение
                    return message;
                }
            }
        } catch (JsonSyntaxException e) {
            // Если Message не является JSON - возвращаем исходное сообщение
        }

        // Возвращаем исходный Message, если валидация не пройдена
        return message;
    }
}
