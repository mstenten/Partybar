package dev.party.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "drink_order")
public class DrinkOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String drinkName;

    @Column(length = 500)
    private String ingredients;

    @Column(length = 1000)
    private String preparation;

    private LocalDateTime createdAt = LocalDateTime.now();

    // ✅ robustes Feld für Admin-Status (Standardwert = false)
    @Column(nullable = false)
    private boolean completed = false;

    // === Konstruktoren ===
    public DrinkOrder() {
        // Standard-Konstruktor wird von JPA benötigt
    }

    public DrinkOrder(String username, String drinkName, String ingredients, String preparation) {
        this.username = username;
        this.drinkName = drinkName;
        this.ingredients = ingredients;
        this.preparation = preparation;
        this.createdAt = LocalDateTime.now();
        this.completed = false;
    }

    // === Getter und Setter ===

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDrinkName() {
        return drinkName;
    }

    public void setDrinkName(String drinkName) {
        this.drinkName = drinkName;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getPreparation() {
        return preparation;
    }

    public void setPreparation(String preparation) {
        this.preparation = preparation;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    // === Praktisch für Debugging / Admin ===
    @Override
    public String toString() {
        return "DrinkOrder{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", drinkName='" + drinkName + '\'' +
                ", completed=" + completed +
                ", createdAt=" + createdAt +
                '}';
    }
}
