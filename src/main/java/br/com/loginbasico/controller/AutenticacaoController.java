package br.com.loginbasico.controller;

import br.com.loginbasico.dto.CredenciaisRequest;
import br.com.loginbasico.dto.MensagemResponse;
import br.com.loginbasico.model.Usuario;
import br.com.loginbasico.service.AutenticacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AutenticacaoController {

    private final AutenticacaoService service;

    public AutenticacaoController(AutenticacaoService service) {
        this.service = service;
    }

    /** Cria um usuário para podermos testar o acesso (senha salva como hash BCrypt). */
    @PostMapping("/usuarios")
    public ResponseEntity<MensagemResponse> cadastrar(@Valid @RequestBody CredenciaisRequest credenciais) {
        Usuario usuario = service.cadastrar(credenciais);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MensagemResponse("Usuário '" + usuario.getNomeUsuario() + "' cadastrado com sucesso."));
    }

    /**
     * Endpoint protegido: só responde com sucesso depois de validar
     * usuário e senha enviados no corpo da requisição.
     */
    @PostMapping("/protegido")
    public ResponseEntity<MensagemResponse> acessoProtegido(@Valid @RequestBody CredenciaisRequest credenciais) {
        Usuario usuario = service.autenticar(credenciais);
        return ResponseEntity.ok(
                new MensagemResponse("Acesso liberado! Bem-vindo, " + usuario.getNomeUsuario() + "."));
    }
}
