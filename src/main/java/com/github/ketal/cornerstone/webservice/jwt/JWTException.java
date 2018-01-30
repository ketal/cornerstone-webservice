package com.github.ketal.cornerstone.webservice.jwt;

public class JWTException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public JWTException(String error) {
        super(error);
    }
    
    public JWTException(String error, Throwable e) {
        super(error, e);
    }
}
