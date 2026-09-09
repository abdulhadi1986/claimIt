package com.foundIt.claimIt.exception;

import java.io.Serial;

public class InvalidUserInputException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;
    public InvalidUserInputException(String message) {
        super(message);
    }
}
