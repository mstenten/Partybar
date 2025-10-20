package dev.party.repository;

import dev.party.model.DrinkOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DrinkOrderRepository extends JpaRepository<DrinkOrder, Long> {

    // Für Admin-Seite (neueste zuerst)
    List<DrinkOrder> findAllByOrderByCreatedAtDesc();

    // ✅ Für Badge: schnelle DB-Zählung offener Bestellungen
    long countByCompletedFalse();
}
