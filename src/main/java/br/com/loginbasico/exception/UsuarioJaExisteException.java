package br.com.loginbasico.exception;

public class UsuarioJaExisteException extends RuntimeException {

    public UsuarioJaExisteException() {
        super("Já existe um usuário cadastrado com esse nome.");
    }
}
