package dev.party.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Drink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category; // Sekt, Weine, Biere, Cocktails, Longdrinks, Spirituosen, Alkoholfrei

    private String imagePath; // Pfad zum Bild (z. B. /images/mojito.jpg)

    @OneToMany(mappedBy = "drink", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ingredient> ingredients; // Liste der Zutaten

    @Lob
    private String preparation; // Wie das Getränk gemacht wird

    // === Getter & Setter ===
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public String getPreparation() {
        return preparation;
    }

    public void setPreparation(String preparation) {
        this.preparation = preparation;
    }
    public String getIngredientsAsString() {
        if (ingredients == null || ingredients.isEmpty()) {
            return "";
        }
        return ingredients.stream()
                .map(Ingredient::getName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
    }

}