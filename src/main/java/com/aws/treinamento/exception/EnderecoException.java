package com.aws.treinamento.exception;

public class EnderecoException extends RuntimeException {

    public EnderecoException(String message) {
        super(message);
    }

    public EnderecoException(String message, Throwable cause) {
        super(message, cause);
    }

}
