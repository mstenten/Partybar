package dev.party.config;

import dev.party.model.User;
import dev.party.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin") == null) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setRole("ADMIN"); // ⚠️ nur ADMIN, kein ROLE_ !
                userRepository.save(admin);
                System.out.println("👑 Admin-Account erstellt (admin/admin)");
            } else {
                System.out.println("✅ Admin-Account existiert bereits");
            }
        };
    }
}