package br.com.loginbasico.service;

import br.com.loginbasico.dto.CredenciaisRequest;
import br.com.loginbasico.exception.SenhaIncorretaException;
import br.com.loginbasico.exception.UsuarioJaExisteException;
import br.com.loginbasico.exception.UsuarioNaoEncontradoException;
import br.com.loginbasico.model.Usuario;
import br.com.loginbasico.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutenticacaoService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AutenticacaoService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Cadastra o usuário já guardando o HASH BCrypt da senha. */
    @Transactional
    public Usuario cadastrar(CredenciaisRequest credenciais) {
        if (repository.existsByNomeUsuario(credenciais.usuario())) {
            throw new UsuarioJaExisteException();
        }
        String hash = passwordEncoder.encode(credenciais.senha());
        return repository.save(new Usuario(credenciais.usuario(), hash));
    }

    /**
     * 1) busca o usuário no banco  -> se não existir: UsuarioNaoEncontradoException
     * 2) compara senha x hash      -> se não bater:   SenhaIncorretaException
     * 3) devolve o usuário autenticado
     */
    @Transactional(readOnly = true)
    public Usuario autenticar(CredenciaisRequest credenciais) {
        Usuario usuario = repository.findByNomeUsuario(credenciais.usuario())
                .orElseThrow(UsuarioNaoEncontradoException::new);

        if (!passwordEncoder.matches(credenciais.senha(), usuario.getSenha())) {
            throw new SenhaIncorretaException();
        }
        return usuario;
    }
}
