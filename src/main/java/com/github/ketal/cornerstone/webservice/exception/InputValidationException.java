package com.github.ketal.cornerstone.webservice.exception;

import javax.validation.ValidationException;

public class InputValidationException extends ValidationException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public InputValidationException(String message) {
        super(message);
    }
    
    public InputValidationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InputValidationException(Throwable cause) {
        super(cause);
    }
}
