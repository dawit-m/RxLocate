package com.pharma.pharmaapp.controller;

import com.pharma.pharmaapp.entity.Medicine;
import com.pharma.pharmaapp.service.MedicineService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ClientController {

    private final MedicineService medicineService;

    public ClientController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @GetMapping("/client")
    public String showClientPage(Model model) {
        model.addAttribute("medicines", List.of());
        return "client-search";
    }

    @GetMapping("/client/search")
    public String search(@RequestParam(required = false) String name, Model model) {
        String query = name == null ? "" : name.trim();
        List<Medicine> results = query.isBlank() ? List.of() : medicineService.searchByName(query);
        model.addAttribute("medicines", results);
        model.addAttribute("searchQuery", query);
        model.addAttribute("resultCount", results.size());
        return "client-search";
    }
}
