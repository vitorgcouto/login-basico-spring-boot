package br.com.loginbasico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Representa usuário e senha recebidos no corpo da requisição:
 * { "usuario": "vitor", "senha": "minhaSenha123" }
 */
public record CredenciaisRequest(

        @NotBlank(message = "O campo 'usuario' é obrigatório.")
        @Size(max = 50, message = "O campo 'usuario' deve ter no máximo 50 caracteres.")
        String usuario,

        // BCrypt só considera os 72 primeiros bytes da senha.
        @NotBlank(message = "O campo 'senha' é obrigatório.")
        @Size(max = 72, message = "O campo 'senha' deve ter no máximo 72 caracteres.")
        String senha
) {

    // Evita que a senha apareça em logs caso o objeto seja impresso.
    @Override
    public String toString() {
        return "CredenciaisRequest[usuario=" + usuario + ", senha=****]";
    }
}
