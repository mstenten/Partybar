package dev.party.controller;

import dev.party.model.Drink;
import dev.party.model.Ingredient;
import dev.party.repository.DrinkRepository;
import dev.party.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminDrinkController {

    private final DrinkRepository drinkRepository;
    private final IngredientRepository ingredientRepository;

    @Value("${upload.dir:uploads}")
    private String uploadDir;

    public AdminDrinkController(DrinkRepository drinkRepository, IngredientRepository ingredientRepository) {
        this.drinkRepository = drinkRepository;
        this.ingredientRepository = ingredientRepository;
    }

    // ===========================
    // FORMULAR: GETRÄNK HINZUFÜGEN
    // ===========================
    @GetMapping("/add-drink")
    public String showAddDrinkForm() {
        return "add-drink";
    }

    // ===========================
    // SPEICHERN: NEUES GETRÄNK
    // ===========================
    @PostMapping("/add-drink")
    public String addDrink(
            @RequestParam("name") String name,
            @RequestParam("category") String category,
            @RequestParam("ingredientsList") String ingredientsList,
            @RequestParam("preparation") String preparation,
            @RequestParam("imageFile") MultipartFile imageFile
    ) throws IOException {

        String fileName = "default-drink.jpg";

        // 📸 Bild speichern
        if (imageFile != null && !imageFile.isEmpty()) {
            fileName = imageFile.getOriginalFilename();
            Path uploadPath = Paths.get(System.getProperty("user.dir"), uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path targetPath = uploadPath.resolve(fileName);
            imageFile.transferTo(targetPath.toFile());
        }

        // 🍸 Drink anlegen
        Drink drink = new Drink();
        drink.setName(name);
        drink.setCategory(category);
        drink.setPreparation(preparation);
        drink.setImagePath("/" + uploadDir + "/" + fileName);

        // 🍋 Zutaten verarbeiten
        List<Ingredient> ingredients = new ArrayList<>();
        for (String ing : ingredientsList.split(",")) {
            if (!ing.trim().isEmpty()) {
                Ingredient i = new Ingredient();
                i.setName(ing.trim());
                i.setDrink(drink);
                ingredients.add(i);
            }
        }

        drink.setIngredients(ingredients);
        drinkRepository.save(drink);
        ingredientRepository.saveAll(ingredients);

        return "redirect:/drinks";
    }

    // ===========================
    // FORMULAR: GETRÄNK BEARBEITEN
    // ===========================
    @GetMapping("/edit-drink/{id}")
    public String editDrinkForm(@PathVariable Long id, Model model) {
        Optional<Drink> optionalDrink = drinkRepository.findById(id);
        if (optionalDrink.isEmpty()) {
            return "redirect:/drinks";
        }

        Drink drink = optionalDrink.get();
        String ingredientsString = String.join(", ",
                drink.getIngredients().stream().map(Ingredient::getName).toList());

        model.addAttribute("drink", drink);
        model.addAttribute("ingredientsList", ingredientsString);
        return "edit-drink";
    }

    // ===========================
    // SPEICHERN: BEARBEITUNG (robust)
    // ===========================
    @PostMapping("/edit-drink/{id}")
    public String updateDrink(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("category") String category,
            @RequestParam("ingredientsList") String ingredientsList,
            @RequestParam("preparation") String preparation,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        try {
            Optional<Drink> optionalDrink = drinkRepository.findById(id);
            if (optionalDrink.isEmpty()) {
                System.err.println("❌ Kein Drink mit ID " + id + " gefunden!");
                return "redirect:/drinks";
            }

            Drink drink = optionalDrink.get();
            drink.setName(name != null ? name.trim() : "Unbekannt");
            drink.setCategory(category != null ? category.trim() : "Sonstiges");
            drink.setPreparation(preparation != null ? preparation.trim() : "");

            // 📸 Bild speichern (optional)
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    String fileName = imageFile.getOriginalFilename();
                    if (fileName == null || fileName.isBlank()) {
                        fileName = "default-drink.jpg";
                    }

                    Path uploadPath = Paths.get(System.getProperty("user.dir"), uploadDir);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }

                    Path target = uploadPath.resolve(fileName);
                    imageFile.transferTo(target.toFile());
                    drink.setImagePath("/" + uploadDir + "/" + fileName);

                } catch (Exception e) {
                    System.err.println("⚠️ Fehler beim Bild-Upload: " + e.getMessage());
                }
            }

            // 🍋 Zutaten aktualisieren
            try {
                ingredientRepository.deleteAll(drink.getIngredients());
                List<Ingredient> newIngredients = new ArrayList<>();

                if (ingredientsList != null && !ingredientsList.isBlank()) {
                    for (String ing : ingredientsList.split(",")) {
                        String trimmed = ing.trim();
                        if (!trimmed.isEmpty()) {
                            Ingredient i = new Ingredient();
                            i.setName(trimmed);
                            i.setDrink(drink);
                            newIngredients.add(i);
                        }
                    }
                }

                drink.setIngredients(newIngredients);
                drinkRepository.save(drink);
                ingredientRepository.saveAll(newIngredients);

            } catch (Exception e) {
                System.err.println("⚠️ Fehler beim Aktualisieren der Zutaten: " + e.getMessage());
            }

            System.out.println("✅ Drink erfolgreich bearbeitet (ID " + id + ")");
            return "redirect:/drinks";

        } catch (Exception e) {
            System.err.println("💥 Unerwarteter Fehler beim Update von Drink ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return "redirect:/drinks";
        }
    }

    // ===========================
    // GETRÄNK LÖSCHEN
    // ===========================
    @GetMapping("/delete-drink/{id}")
    public String deleteDrink(@PathVariable Long id) {
        try {
            ingredientRepository.deleteAll(ingredientRepository.findAll()
                    .stream()
                    .filter(i -> i.getDrink() != null && Objects.equals(i.getDrink().getId(), id))
                    .toList());

            drinkRepository.deleteById(id);
            System.out.println("🗑️ Drink gelöscht (ID " + id + ")");
        } catch (Exception e) {
            System.err.println("⚠️ Fehler beim Löschen des Drinks ID " + id + ": " + e.getMessage());
        }

        return "redirect:/drinks";
    }
}