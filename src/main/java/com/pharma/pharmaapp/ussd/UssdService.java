package com.pharma.pharmaapp.ussd;

import com.pharma.pharmaapp.entity.Medicine;
import com.pharma.pharmaapp.service.MedicineService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UssdService {

    private final MedicineService medicineService;

    public UssdService(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    public String process(String sessionId, String text) {

        if (text == null || text.isEmpty()) {
            return "CON Welcome to RxLocate Ethiopia\n1. Search Medicine Info\n2. About Service";
        }

        if (text.equals("1")) {
            return "CON Enter medicine name:";
        }

        if (text.startsWith("1*")) {
            String medicineName = text.substring(2);
            List<Medicine> results = medicineService.searchByName(medicineName);

            if (results == null || results.isEmpty()) {
                return "END Sorry, " + medicineName + " is not available in our database.";
            }

            Medicine med = results.get(0);

            String pharmacyName = med.getPharmacy().getPharmacyName();
            String pharmacyPhone = med.getPharmacy().getPhoneNumber();

            return "END " + med.getName() + " (" + med.getDosage() + ")\n" +
                    "Price: " + med.getPrice() + " ETB\n" +
                    "Origin: " + med.getBrandCountry() + "\n" +
                    "Pharmacy: " + pharmacyName + "\n" +
                    "Call: " + pharmacyPhone;
        }

        if (text.equals("2")) {
            return "END RxLocate Ethiopia: Connecting patients to pharmacies offline. Developed for Biomedical Engineering Final Project.";
        }

        return "END Invalid input. Please try again by dialing the code.";
    }
}