package com.ayushmaanbhav.instakilo.exception;

public class RecoverableException extends RuntimeException {

    private static final long serialVersionUID = 2152723295111450988L;

    public RecoverableException(String message, Throwable e) {
        super(message, e);
    }

}
