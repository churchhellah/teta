package org.teta;

public interface MessageValidator {

    boolean isValidJson (String json);

    boolean containsMsisdn (String json);
}