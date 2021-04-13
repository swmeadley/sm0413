package com.demonstration.toolrental.util;

import com.demonstration.toolrental.controller.exceptions.InvalidRequestException;
import com.demonstration.toolrental.model.request.ToolRentalRequest;
import lombok.extern.slf4j.Slf4j;

import static com.demonstration.toolrental.util.ApplicationConstants.*;

@Slf4j
public final class Validator {

    private Validator () {}

    public static void validateRequest(ToolRentalRequest request) throws InvalidRequestException {
        if (request == null || request.isEmpty()) {
            throw new InvalidRequestException(REQUEST_EMPTY_OR_NULL);
        }
        if (request.getRentalDays() < 1) {
            throw new InvalidRequestException(INVALID_RENTAL_DAY_COUNT);
        }
        if (request.getDiscount() < 0 || request.getDiscount() > 100) {
            throw new InvalidRequestException(INVALID_DISCOUNT_PERCENT);
        }
        if (request.getToolCode() == null || request.getToolCode().isEmpty()) {
            throw new InvalidRequestException(TOOL_CODE_EMPTY_OR_NULL);
        }
        if (request.getCheckoutDate() == null) {
            throw new InvalidRequestException(CHECKOUT_DATE_NULL);
        }
    }
    
}
