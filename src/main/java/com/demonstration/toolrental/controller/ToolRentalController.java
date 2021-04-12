package com.demonstration.toolrental.controller;

import javax.validation.Valid;

import com.demonstration.toolrental.controller.exceptions.InvalidRequestException;
import com.demonstration.toolrental.model.request.ToolRentalRequest;
import com.demonstration.toolrental.model.response.RentalAgreement;
import com.demonstration.toolrental.service.ToolRentalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Validated
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
        @Valid ToolRentalRequest request) throws InvalidRequestException {
        log.info("Starting API for {}", request.getToolCode());
        RentalAgreement result = toolRentalService.checkout(request);
        log.info("Completed Rental Agreement for {}", request.getToolCode());
        return ResponseEntity.ok(result);
    }
}
