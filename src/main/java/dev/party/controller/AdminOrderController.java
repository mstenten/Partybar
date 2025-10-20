package dev.party.controller;

import dev.party.model.DrinkOrder;
import dev.party.repository.DrinkOrderRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final DrinkOrderRepository drinkOrderRepository;

    public AdminOrderController(DrinkOrderRepository drinkOrderRepository) {
        this.drinkOrderRepository = drinkOrderRepository;
    }

    // ✅ Übersicht
    @GetMapping
    public String showOrders(Model model) {
        List<DrinkOrder> orders = drinkOrderRepository.findAllByOrderByCreatedAtDesc();
        model.addAttribute("orders", orders);
        return "admin-orders";
    }

    // ✅ Erledigt markieren
    @PostMapping("/complete/{id}")
    public String completeOrder(@PathVariable Long id) {
        drinkOrderRepository.findById(id).ifPresent(order -> {
            order.setCompleted(true);
            drinkOrderRepository.save(order);
        });
        return "redirect:/admin/orders";
    }

    // ✅ Badge-Zähler (nur offene)
    @GetMapping("/count")
    @ResponseBody
    public long getOpenOrderCount() {
        // robust & effizient (DB-seitig gezählt)
        return drinkOrderRepository.countByCompletedFalse();
    }
}
