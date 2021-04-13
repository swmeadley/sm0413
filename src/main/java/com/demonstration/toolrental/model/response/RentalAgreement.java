package com.demonstration.toolrental.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RentalAgreement {
    private String toolCode;
    private String toolType;
    private String toolBrand;
    private int rentalDays;
    private LocalDate checkoutDate;
    private LocalDate dueDate;
    private BigDecimal dailyRentalCharge;
    private int chargeDays;
    private BigDecimal prediscountCharge;
    private int discountPercent;
    private BigDecimal discountAmount;
    private BigDecimal finalCharge;

    @JsonIgnore
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy");

    public void printAgreement() {
        System.out.println("Tool Code: " + toolCode);
        System.out.println("Tool Type: " + toolType);
        System.out.println("Tool Brand: " + toolBrand);
        System.out.println("Rental Days: " + rentalDays);
        System.out.println("Checkout Date: " + checkoutDate.format(formatter));
        System.out.println("Due Date: " + dueDate.format(formatter));
        System.out.println("Daily Rental Charge: " + NumberFormat.getCurrencyInstance().format(dailyRentalCharge));
        System.out.println("Charge Days: " + chargeDays);
        System.out.println("Pre-Discount Charge: " + NumberFormat.getCurrencyInstance().format(prediscountCharge));
        System.out.println("Discount Percent: " + discountPercent + "%");
        System.out.println("Discount Amount: " + NumberFormat.getCurrencyInstance().format(discountAmount));
        System.out.println("Final Charge: " + NumberFormat.getCurrencyInstance().format(finalCharge) + "\n");
    }
}
