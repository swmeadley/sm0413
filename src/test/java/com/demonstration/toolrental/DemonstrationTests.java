package com.demonstration.toolrental;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static com.demonstration.toolrental.util.ApplicationConstants.INVALID_DISCOUNT_PERCENT;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import com.demonstration.toolrental.controller.exceptions.InvalidRequestException;
import com.demonstration.toolrental.model.entity.Tool;
import com.demonstration.toolrental.model.request.ToolRentalRequest;
import com.demonstration.toolrental.model.response.RentalAgreement;
import com.demonstration.toolrental.repository.ToolRepository;
import com.demonstration.toolrental.service.ToolRentalService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class DemonstrationTests {

    @Mock
    ToolRepository mockRepository;

    private ToolRentalService subject;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        subject = new ToolRentalService(mockRepository);
    }

    @Test
    @DisplayName("TEST 1")
    void testOne() throws InvalidRequestException {
        LocalDate checkoutDate = LocalDate.of(2015, 9, 3);
        ToolRentalRequest request = new ToolRentalRequest("JAKR", checkoutDate, 5, 101);
        Tool expectedTool = generateJackhammer("Ridgid", "JAKR");
        when(mockRepository.getToolData(anyString())).thenReturn(expectedTool);

        try {
            RentalAgreement actual = subject.checkout(request);
        } catch (InvalidRequestException e) {
            assertEquals(INVALID_DISCOUNT_PERCENT, e.getLocalizedMessage());
        }
    }

    @Test
    @DisplayName("TEST 2")
    void testTwo() throws InvalidRequestException {
        LocalDate checkoutDate = LocalDate.of(2020, 7, 2);
        ToolRentalRequest request = new ToolRentalRequest("LADW", checkoutDate, 3, 10);
        Tool expectedTool = generateLadder();
        when(mockRepository.getToolData(anyString())).thenReturn(expectedTool);

        RentalAgreement actual = subject.checkout(request);
    }

    @Test
    @DisplayName("TEST 3")
    void testThree() throws InvalidRequestException {
        LocalDate checkoutDate = LocalDate.of(2015, 7, 2);
        ToolRentalRequest request = new ToolRentalRequest("CHNS", checkoutDate, 5, 25);
        Tool expectedTool = generateChainsaw();
        when(mockRepository.getToolData(anyString())).thenReturn(expectedTool);

        RentalAgreement actual = subject.checkout(request);
    }

    @Test
    @DisplayName("TEST 4")
    void testFour() throws InvalidRequestException {
        LocalDate checkoutDate = LocalDate.of(2015, 9, 3);
        ToolRentalRequest request = new ToolRentalRequest("JAKD", checkoutDate, 6, 0);
        Tool expectedTool = generateJackhammer("DeWalt", "JAKD");
        when(mockRepository.getToolData(anyString())).thenReturn(expectedTool);

        RentalAgreement actual = subject.checkout(request);
    }

    @Test
    @DisplayName("TEST 5")
    void testFive() throws InvalidRequestException {
        LocalDate checkoutDate = LocalDate.of(2015, 7, 2);
        ToolRentalRequest request = new ToolRentalRequest("JAKR", checkoutDate, 9, 0);
        Tool expectedTool = generateJackhammer("Ridgid", "JAKR");
        when(mockRepository.getToolData(anyString())).thenReturn(expectedTool);

        RentalAgreement actual = subject.checkout(request);
    }

    @Test
    @DisplayName("TEST 6")
    void testSix() throws InvalidRequestException {
        LocalDate checkoutDate = LocalDate.of(2020, 7, 2);
        ToolRentalRequest request = new ToolRentalRequest("JAKR", checkoutDate, 4, 50);
        Tool expectedTool = generateJackhammer("Ridgid", "JAKR");
        when(mockRepository.getToolData(anyString())).thenReturn(expectedTool);

        RentalAgreement actual = subject.checkout(request);
    }

    private Tool generateLadder() {
        return Tool.builder()
            .toolType("Ladder")
            .brand("Werner")
            .toolCode("LADW")
            .dailyCharge(new BigDecimal(1.99).setScale(2,RoundingMode.HALF_UP))
            .weekdayCharge("Yes")
            .weekendCharge("Yes")
            .holidayCharge("No")
            .build();
    }

    private Tool generateChainsaw() {
        return Tool.builder()
            .toolType("Chainsaw")
            .brand("Stihl")
            .toolCode("CHNS")
            .dailyCharge(new BigDecimal(1.49).setScale(2,RoundingMode.HALF_UP))
            .weekdayCharge("Yes")
            .weekendCharge("No")
            .holidayCharge("Yes")
            .build();
    }

    private Tool generateJackhammer(String brand, String toolCode) {
        return Tool.builder()
            .toolType("Jackhammer")
            .brand(brand)
            .toolCode(toolCode)
            .dailyCharge(new BigDecimal(2.99).setScale(2,RoundingMode.HALF_UP))
            .weekdayCharge("Yes")
            .weekendCharge("No")
            .holidayCharge("No")
            .build();
    }
    
}
