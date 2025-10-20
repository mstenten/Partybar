package dev.party;

import dev.party.model.User;
import dev.party.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class PartybarApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(PartybarApplication.class, args);
    }

    @Override
    public void run(String... args) {
        // Admin automatisch anlegen, falls nicht vorhanden
        if (userRepository.findByUsernameIgnoreCase("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(new BCryptPasswordEncoder().encode("admin123"));
            admin.setRole("ROLE_ADMIN"); // ✅ wichtiges Prefix für Spring Security
            userRepository.save(admin);
            System.out.println("✅ Admin erstellt: Benutzername=admin, Passwort=admin123");
        } else {
            System.out.println("⚠️ Admin existiert bereits — kein neuer Eintrag erstellt.");
        }
    }
}