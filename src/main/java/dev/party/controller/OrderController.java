package dev.party.controller;

import dev.party.model.Drink;
import dev.party.model.DrinkOrder;
import dev.party.repository.DrinkOrderRepository;
import dev.party.repository.DrinkRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private static final String TRAY_IDS_KEY = "trayIds";

    private final DrinkRepository drinkRepository;
    private final DrinkOrderRepository drinkOrderRepository;

    public OrderController(DrinkRepository drinkRepository, DrinkOrderRepository drinkOrderRepository) {
        this.drinkRepository = drinkRepository;
        this.drinkOrderRepository = drinkOrderRepository;
    }

    // === Tablett anzeigen ===
    @GetMapping("/tray")
    public String showTray(HttpSession session, Model model, Authentication auth) {
        List<Long> trayIds = getTrayIds(session);
        List<Drink> drinks = loadDrinks(trayIds);

        model.addAttribute("drinks", drinks);
        model.addAttribute("traySize", trayIds.size());
        model.addAttribute("username", (auth != null && auth.isAuthenticated()) ? auth.getName() : "Gast");

        return "tray";
    }

    // === Drink zum Tablett hinzufügen (AJAX) ===
    @PostMapping("/add/{id}")
    @ResponseBody
    public String addToTray(@PathVariable Long id, HttpSession session) {
        List<Long> trayIds = getTrayIds(session);
        if (!trayIds.contains(id)) {
            // nur hinzufügen, wenn es den Drink auch wirklich gibt
            if (drinkRepository.existsById(id)) {
                trayIds.add(id);
                session.setAttribute(TRAY_IDS_KEY, trayIds);
            }
        }
        return String.valueOf(trayIds.size()); // plain text für fetch()
    }

    // === Drink aus Tablett entfernen ===
    @GetMapping("/remove/{id}")
    public String removeFromTray(@PathVariable Long id, HttpSession session) {
        List<Long> trayIds = getTrayIds(session);
        trayIds.removeIf(x -> Objects.equals(x, id));
        session.setAttribute(TRAY_IDS_KEY, trayIds);
        return "redirect:/orders/tray";
    }

    // === Anzahl im Tablett (Badge) ===
    @GetMapping("/count")
    @ResponseBody
    public String getTrayCount(HttpSession session) {
        return String.valueOf(getTrayIds(session).size());
    }

    // === Bestellung absenden ===
    @PostMapping("/submit")
    public String submitOrder(HttpSession session, Authentication auth, Model model) {
        List<Long> trayIds = getTrayIds(session);
        if (trayIds.isEmpty()) {
            model.addAttribute("message", "Dein Tablett ist leer – such dir etwas aus!");
            model.addAttribute("drinks", Collections.emptyList());
            return "tray";
        }

        List<Drink> drinks = loadDrinks(trayIds);
        if (drinks.isEmpty()) {
            model.addAttribute("message", "Ups – die ausgewählten Drinks konnten nicht geladen werden.");
            model.addAttribute("drinks", Collections.emptyList());
            return "tray";
        }

        String username = (auth != null && auth.isAuthenticated()) ? auth.getName() : "Gast";

        for (Drink drink : drinks) {
            DrinkOrder order = new DrinkOrder();
            order.setUsername(username);
            order.setDrinkName(drink.getName());
            order.setIngredients(drink.getIngredientsAsString());
            order.setPreparation(drink.getPreparation());
            order.setCreatedAt(LocalDateTime.now());
            order.setCompleted(false);
            drinkOrderRepository.save(order);
        }

        // Tray leeren
        session.removeAttribute(TRAY_IDS_KEY);
        return "thankyou";
    }

    // === Hilfsmethoden ===

    @SuppressWarnings("unchecked")
    private List<Long> getTrayIds(HttpSession session) {
        Object obj = session.getAttribute(TRAY_IDS_KEY);
        if (obj instanceof List<?>) {
            try {
                // defensive copy, um ConcurrentModification zu vermeiden
                return new ArrayList<>((List<Long>) obj);
            } catch (ClassCastException e) {
                // falls mal etwas anderes drin lag
                List<Long> empty = new ArrayList<>();
                session.setAttribute(TRAY_IDS_KEY, empty);
                return empty;
            }
        } else {
            List<Long> empty = new ArrayList<>();
            session.setAttribute(TRAY_IDS_KEY, empty);
            return empty;
        }
    }

    private List<Drink> loadDrinks(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        // Reihenfolge wie im Tray beibehalten:
        Map<Long, Drink> byId = drinkRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Drink::getId, d -> d));
        List<Drink> ordered = new ArrayList<>();
        for (Long id : ids) {
            Drink d = byId.get(id);
            if (d != null) ordered.add(d);
        }
        return ordered;
    }
}
