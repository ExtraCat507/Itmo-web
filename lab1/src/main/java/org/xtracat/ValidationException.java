package org.xtracat;

public class ValidationException extends Throwable {
    private String message;
    public ValidationException(String string) {
        message = string;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
