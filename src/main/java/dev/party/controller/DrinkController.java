package dev.party.controller;

import dev.party.repository.DrinkRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DrinkController {

    private final DrinkRepository drinkRepository;

    public DrinkController(DrinkRepository drinkRepository) {
        this.drinkRepository = drinkRepository;
    }

    @GetMapping("/drinks")
    public String showDrinks(Model model) {
        model.addAttribute("drinks", drinkRepository.findAll());
        return "drinks";
    }
}