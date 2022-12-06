package bci.challenge.exception;

import lombok.Getter;

public class BciException extends Exception {

    @Getter
    private final int httpStatus;

    public BciException(int httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

}
