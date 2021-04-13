package com.demonstration.toolrental.util;

import com.demonstration.toolrental.model.request.ToolRentalRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.demonstration.toolrental.util.ApplicationConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class ValidatorTest {

    @Test
    @DisplayName("Successful Validation")
    void validationSuccess() {
        LocalDate checkoutDate = LocalDate.of(2015, 3, 9);
        ToolRentalRequest request = new ToolRentalRequest("LADW", checkoutDate, 1, 50);

        try {
            Validator.validateRequest(request);
        } catch (Exception e) {
            fail();
        }
        
    }

    @Test
    @DisplayName("Catch exception when request has invalid rental day count")
    void validationFailure_InvalidRentalDayCount() {
        LocalDate checkoutDate = LocalDate.of(2015, 3, 9);
        ToolRentalRequest request = new ToolRentalRequest("LADW", checkoutDate, 0, 0);

        try {
            Validator.validateRequest(request);
        } catch (Exception e) {
            assertEquals(INVALID_RENTAL_DAY_COUNT, e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Catch exception when request has invalid discount")
    void validationFailure_InvalidDiscount() {
        LocalDate checkoutDate = LocalDate.of(2015, 3, 9);
        ToolRentalRequest request = new ToolRentalRequest("LADW", checkoutDate, 5, 101);

        try {
            Validator.validateRequest(request);
        } catch (Exception e) {
            assertEquals(INVALID_DISCOUNT_PERCENT, e.getMessage());
        }
    }

    @Test
    @DisplayName("Catch exception when request has null checkout date")
    void validationFailure_CheckoutDate() {
        ToolRentalRequest request = new ToolRentalRequest("LADW", null, 5, 10);

        try {
            Validator.validateRequest(request);
        } catch (Exception e) {
            assertEquals(CHECKOUT_DATE_NULL, e.getMessage());
        }
    }

    @Test
    @DisplayName("Catch exception when request has empty Tool Code")
    void validationFailure_ToolCode() {
        LocalDate checkoutDate = LocalDate.of(2015, 3, 9);
        ToolRentalRequest request = new ToolRentalRequest("", checkoutDate, 5, 10);

        try {
            Validator.validateRequest(request);
        } catch (Exception e) {
            assertEquals(TOOL_CODE_EMPTY_OR_NULL, e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Catch exception when request is empty")
    void validationFailure_EmptyRequest() {
        ToolRentalRequest request = new ToolRentalRequest();
        try {
            Validator.validateRequest(request);
        } catch (Exception e) {
            assertEquals(REQUEST_EMPTY_OR_NULL, e.getMessage());
        }
    }
}
