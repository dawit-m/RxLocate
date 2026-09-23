package com.pharma.pharmaapp.controller;

import com.pharma.pharmaapp.entity.Pharmacy;
import com.pharma.pharmaapp.service.MedicineService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/medicine")
public class MedicineActionController {

    private final MedicineService medicineService;

    public MedicineActionController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @PostMapping("/update-quantity/{id}/{action}")
    public String updateQuantity(@PathVariable Long id,
                                 @PathVariable String action,
                                 HttpSession session) {
        Pharmacy pharmacy = (Pharmacy) session.getAttribute("loggedInPharmacy");
        if (pharmacy == null) {
            return "redirect:/pharmacy/login";
        }

        medicineService.adjustQuantityForPharmacy(id, pharmacy, action);
        return "redirect:/pharmacy/dashboard";
    }
}
