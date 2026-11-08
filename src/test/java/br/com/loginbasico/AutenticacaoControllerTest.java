package br.com.loginbasico;

import br.com.loginbasico.model.Usuario;
import br.com.loginbasico.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AutenticacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void limparBanco() {
        repository.deleteAll();
    }

    private static String json(String usuario, String senha) {
        return """
                {"usuario": "%s", "senha": "%s"}
                """.formatted(usuario, senha);
    }

    private void cadastrar(String usuario, String senha) throws Exception {
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(usuario, senha)))
                .andExpect(status().isCreated());
    }

    @Test
    void cadastroGuardaSenhaComoHashBCryptENaoEmTextoPuro() throws Exception {
        cadastrar("vitor", "senha123");

        Usuario salvo = repository.findByNomeUsuario("vitor").orElseThrow();

        assertNotEquals("senha123", salvo.getSenha());
        assertTrue(salvo.getSenha().startsWith("$2"), "deveria ser um hash BCrypt");
        assertTrue(passwordEncoder.matches("senha123", salvo.getSenha()));
    }

    @Test
    void credenciaisCorretasLiberamOAcesso() throws Exception {
        cadastrar("vitor", "senha123");

        mockMvc.perform(post("/protegido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("vitor", "senha123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Acesso liberado! Bem-vindo, vitor."));
    }

    @Test
    void usuarioInexistenteRetorna404ComMensagemEspecifica() throws Exception {
        mockMvc.perform(post("/protegido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("fantasma", "qualquer")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Usuário não encontrado."));
    }

    @Test
    void senhaIncorretaRetorna401ComMensagemEspecifica() throws Exception {
        cadastrar("vitor", "senha123");

        mockMvc.perform(post("/protegido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("vitor", "senhaErrada")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.mensagem").value("Senha incorreta."));
    }

    @Test
    void cadastroDeUsuarioDuplicadoRetorna409() throws Exception {
        cadastrar("vitor", "senha123");

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("vitor", "outraSenha")))
                .andExpect(status().isConflict());
    }

    @Test
    void corpoSemSenhaRetorna400() throws Exception {
        mockMvc.perform(post("/protegido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuario\": \"vitor\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("O campo 'senha' é obrigatório."));
    }

    @Test
    void semCorpoRetorna400() throws Exception {
        mockMvc.perform(post("/protegido").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
