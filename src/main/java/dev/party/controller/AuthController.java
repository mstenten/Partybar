package dev.party.controller;

import dev.party.model.User;
import dev.party.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Login-Seite
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Registrierungsseite
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // Registrierung absenden
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        if (userRepository.findByUsernameIgnoreCase(user.getUsername()).isPresent()) {
            model.addAttribute("error", "Dieser Benutzername ist bereits vergeben.");
            return "register";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER"); // ✅ Normale Benutzer bekommen ROLE_USER
        userRepository.save(user);

        model.addAttribute("success", "Registrierung erfolgreich! Du kannst dich jetzt einloggen.");
        return "login";
    }

    // Nach Login
    @GetMapping("/")
    public String home() {
        return "home";
    }
}