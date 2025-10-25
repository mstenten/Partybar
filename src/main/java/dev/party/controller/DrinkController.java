package dev.party.controller;

import dev.party.model.Drink;
import dev.party.repository.DrinkRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DrinkController {

    private final DrinkRepository drinkRepository;

    public DrinkController(DrinkRepository drinkRepository) {
        this.drinkRepository = drinkRepository;
    }

    @GetMapping("/drinks")
    public String showDrinks(@RequestParam(required = false) String category, Model model) {

        // Debug: Prüfen, ob Route aufgerufen wird
        System.out.println("🎯 /drinks wurde aufgerufen (Kategorie: " + category + ")");

        List<Drink> allDrinks = drinkRepository.findAll();

        // 🧩 Falls keine Drinks vorhanden sind
        if (allDrinks == null || allDrinks.isEmpty()) {
            model.addAttribute("groupedDrinks", Collections.emptyMap());
            model.addAttribute("selectedCategory", category);
            model.addAttribute("noDrinksMessage", "Keine Getränke gefunden 🍸");
            return "drinks";
        }

        // 🧃 Gruppieren nach Kategorie
        Map<String, List<Drink>> grouped = allDrinks.stream()
                .collect(Collectors.groupingBy(
                        Drink::getCategory,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        // 🗂️ Alphabetisch sortieren
        List<String> sortedCategories = new ArrayList<>(grouped.keySet());
        sortedCategories.sort(String::compareToIgnoreCase);

        // 🎯 Ausgewählte Kategorie nach oben
        if (category != null && grouped.containsKey(category)) {
            sortedCategories.remove(category);
            sortedCategories.add(0, category);
        }

        // 🧱 Neue LinkedHashMap mit sortierter Reihenfolge
        LinkedHashMap<String, List<Drink>> sortedGrouped = new LinkedHashMap<>();
        for (String cat : sortedCategories) {
            sortedGrouped.put(cat, grouped.get(cat));
        }

        // 🔗 Daten an Thymeleaf
        model.addAttribute("groupedDrinks", sortedGrouped);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("noDrinksMessage", null);

        return "drinks";
    }
}
