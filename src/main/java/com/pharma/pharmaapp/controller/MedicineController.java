package com.pharma.pharmaapp.controller;

import com.pharma.pharmaapp.entity.Medicine;
import com.pharma.pharmaapp.service.MedicineService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    private final MedicineService medicineService;
    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }
    @PostMapping("/add")
    public Medicine addMedicine(@RequestBody Medicine medicine) {
        return medicineService.save(medicine);
    }
    @GetMapping("/search")
    public List<Medicine> searchMedicine(@RequestParam String name) {
        return medicineService.searchByName(name);
    }
}