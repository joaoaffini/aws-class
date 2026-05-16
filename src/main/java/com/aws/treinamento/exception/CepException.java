package com.aws.treinamento.exception;

public class CepException extends RuntimeException {

    public CepException(String message) {
        super(message);
    }

    public CepException(String message, Throwable cause) {
        super(message, cause);
    }

}
