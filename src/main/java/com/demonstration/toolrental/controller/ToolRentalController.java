package com.demonstration.toolrental.controller;

import com.demonstration.toolrental.controller.exceptions.EmptyResultSetException;
import com.demonstration.toolrental.controller.exceptions.InvalidRequestException;
import com.demonstration.toolrental.model.request.ToolRentalRequest;
import com.demonstration.toolrental.model.response.RentalAgreement;
import com.demonstration.toolrental.service.ToolRentalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;


@RestController
@Slf4j
@RequestMapping(value = "/rental")
public class ToolRentalController {
    private ToolRentalService toolRentalService;

    @Autowired
    public ToolRentalController(ToolRentalService toolRentalService) {
        this.toolRentalService = toolRentalService;
    }

    @GetMapping(value = "/tool")
    public ResponseEntity<RentalAgreement> getToolRentalAgreement(
            @RequestParam("ToolCode") @NotNull String toolCode,
            @RequestParam("CheckoutDate") @NotNull @DateTimeFormat(pattern = "MM/dd/yyyy") LocalDate checkoutDate,
            @RequestParam("RentalDays") @NotNull int rentalDays,
            @RequestParam("Discount") @NotNull int discount)
            throws InvalidRequestException, EmptyResultSetException {

        log.info("Checkout for {}", toolCode);
        ToolRentalRequest request = new ToolRentalRequest(toolCode,checkoutDate,rentalDays,discount);
        RentalAgreement result = toolRentalService.checkout(request);
        log.info("Completed Rental Agreement for {}", request.getToolCode());
        return ResponseEntity.ok(result);
    }
}
