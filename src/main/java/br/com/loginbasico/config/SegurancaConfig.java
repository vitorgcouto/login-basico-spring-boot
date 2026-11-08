package br.com.loginbasico.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SegurancaConfig {

    /**
     * BCrypt gera um hash com "salt" aleatório a cada chamada de encode(),
     * então a mesma senha produz hashes diferentes. A verificação é feita
     * com matches(), nunca com equals().
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
