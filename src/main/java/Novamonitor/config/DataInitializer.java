package Novamonitor.config;

import Novamonitor.entity.Tutor;
import Novamonitor.repository.TutorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner criarUsuarios(
            TutorRepository tutorRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            if (tutorRepository.findByEmail("admin@supernovavet.com").isEmpty()) {

                Tutor admin = new Tutor();
                admin.setNome("Administrador");
                admin.setEmail("admin@supernovavet.com");
                admin.setTelefone("11999999999");
                admin.setSenha(passwordEncoder.encode("admin123"));
                admin.setPerfil("ADMIN");

                tutorRepository.save(admin);
            }

            if (tutorRepository.findByEmail("vet@supernovavet.com").isEmpty()) {

                Tutor veterinario = new Tutor();
                veterinario.setNome("Veterinario");
                veterinario.setEmail("vet@supernovavet.com");
                veterinario.setTelefone("11888888888");
                veterinario.setSenha(passwordEncoder.encode("vet123"));
                veterinario.setPerfil("VETERINARIO");

                tutorRepository.save(veterinario);
            }
        };
    }
}