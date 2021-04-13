package com.demonstration.toolrental.controller;

import com.demonstration.toolrental.controller.exceptions.EmptyResultSetException;
import com.demonstration.toolrental.controller.exceptions.InvalidRequestException;
import com.demonstration.toolrental.model.request.ToolRentalRequest;
import com.demonstration.toolrental.service.ToolRentalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.mockito.Mockito.verify;

class ToolRentalControllerTest {

    @Mock
    ToolRentalService mockService;

    ToolRentalController subject;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        subject = new ToolRentalController(mockService);
    }

    @Test
    @DisplayName("Check controller passes appropriate request to service")
    void successfulRequest() throws InvalidRequestException, EmptyResultSetException {
        ToolRentalRequest request = new ToolRentalRequest("JAKD",
                LocalDate.of(2021,4,13),5,50);

        subject.getToolRentalAgreement(request.getToolCode(),
                request.getCheckoutDate(),
                request.getRentalDays(),
                request.getDiscount());

        verify(mockService).checkout(request);
    }
}