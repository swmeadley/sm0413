package com.demonstration.toolrental.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToolRentalRequest {

    String toolCode;
    LocalDate checkoutDate;
    int rentalDays;
    int discount;

    public boolean isEmpty() {
        if ((toolCode == null || toolCode.isBlank()) && checkoutDate == null 
                && rentalDays == 0 && discount == 0) {
            return true;
        }
        return false;
    }
}