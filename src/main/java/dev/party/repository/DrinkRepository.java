package dev.party.repository;

import dev.party.model.Drink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DrinkRepository extends JpaRepository<Drink, Long> {
    // Optional: Filter nach Kategorie
    List<Drink> findByCategory(String category);
}