package br.com.loginbasico.exception;

public class SenhaIncorretaException extends RuntimeException {

    public SenhaIncorretaException() {
        super("Senha incorreta.");
    }
}
