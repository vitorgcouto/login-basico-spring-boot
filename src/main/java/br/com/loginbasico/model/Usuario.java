package br.com.loginbasico.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_usuario", nullable = false, unique = true, length = 50)
    private String nomeUsuario;

    // Guarda o HASH BCrypt (sempre 60 caracteres), nunca a senha em texto puro.
    @Column(nullable = false, length = 60)
    private String senha;

    protected Usuario() {
        // exigido pelo JPA
    }

    public Usuario(String nomeUsuario, String senhaHash) {
        this.nomeUsuario = nomeUsuario;
        this.senha = senhaHash;
    }

    public Long getId() {
        return id;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public String getSenha() {
        return senha;
    }
}
