package com.demonstration.toolrental.model.request;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToolRentalRequest {

    @JsonProperty("ToolCode")
    @NotBlank(message = "Please provide a value for Tool Code")
    String toolCode;

    @JsonProperty("CheckoutDate")
    @NotNull(message = "Please provide a Checkout Date")
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    LocalDate checkoutDate;

    @JsonProperty("RentalDays")
    @NotNull(message = "Please provide the number of rental days")
    int rentalDays;

    @JsonProperty("Discount")
    @NotNull(message = "Please provide a value for Discount")
    int discount;

    public boolean isEmpty() {
        if ((toolCode == null || toolCode.isBlank()) && checkoutDate == null 
                && rentalDays == 0 && discount == 0) {
            return true;
        }
        return false;
    }
}