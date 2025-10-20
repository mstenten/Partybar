package dev.party.controller;

import dev.party.model.Drink;
import dev.party.repository.DrinkRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final DrinkRepository drinkRepository;

    public HomeController(DrinkRepository drinkRepository) {
        this.drinkRepository = drinkRepository;
    }

    // ===========================
    // HOMEPAGE
    // ===========================
    @GetMapping({"/", "/home"})
    public String home(Model model, Authentication authentication) {
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());

            // ✅ Richtige Rollenprüfung (Spring liefert "ROLE_ADMIN")
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            model.addAttribute("isAdmin", isAdmin);
        } else {
            model.addAttribute("username", "Gast");
            model.addAttribute("isAdmin", false);
        }

        return "home";
    }

    // ===========================
    // GETRÄNKESEITE (für alle sichtbar)
    // ===========================

}