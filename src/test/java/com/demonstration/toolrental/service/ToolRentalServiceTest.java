package com.demonstration.toolrental.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;

import com.demonstration.toolrental.controller.exceptions.InvalidRequestException;
import com.demonstration.toolrental.model.entity.Tool;
import com.demonstration.toolrental.model.request.ToolRentalRequest;
import com.demonstration.toolrental.model.response.RentalAgreement;
import com.demonstration.toolrental.repository.ToolRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ToolRentalServiceTest {

    @Mock
    ToolRepository mockRepository;

    private ToolRentalService subject;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        subject = new ToolRentalService(mockRepository);
    }

    @Test
    @DisplayName("Valid input for non holiday ladder request success")
    void checkoutLadderSuccess() throws InvalidRequestException {
        LocalDate checkoutDate = LocalDate.of(2015, 3, 9);
        ToolRentalRequest request = new ToolRentalRequest("LADW", checkoutDate, 5, 0);
        Tool expectedTool = generateLadder();
        when(mockRepository.getToolData(anyString())).thenReturn(expectedTool);

        RentalAgreement actual = subject.checkout(request);

        verify(mockRepository).getToolData(anyString());
        assertToolInfoEquals(expectedTool,actual);
        assertRequestInfoEquals(request,actual);
        
        assertEquals(request.getRentalDays(), actual.getChargeDays());
        assertEquals(expectedTool.getDailyCharge().multiply(new BigDecimal(request.getRentalDays())), actual.getFinalCharge());
    }

    @Test
    @DisplayName("Check discount calculation")
    void discountCalculationSuccess() throws InvalidRequestException {
        LocalDate checkoutDate = LocalDate.of(2015, 3, 9);
        ToolRentalRequest request = new ToolRentalRequest("LADW", checkoutDate, 5, 71);
        Tool expectedTool = generateLadder();
        when(mockRepository.getToolData(anyString())).thenReturn(expectedTool);
        double expectedPreDiscountTotal = expectedTool.getDailyCharge().doubleValue() * 5.0;
        BigDecimal expectedDiscountAmount = BigDecimal.valueOf(expectedPreDiscountTotal * ((double)request.getDiscount() / 100.0))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal expectedFinalAmount = BigDecimal.valueOf(expectedPreDiscountTotal).subtract(expectedDiscountAmount);
        RentalAgreement actual = subject.checkout(request);

        verify(mockRepository).getToolData(anyString());
        
        assertEquals(request.getRentalDays(), actual.getChargeDays());
        assertEquals(expectedDiscountAmount, actual.getDiscountAmount());
        assertEquals(expectedFinalAmount, actual.getFinalCharge());
    }
    
    @Test
    @DisplayName("Check Agreement console format")
    void checkoutJackhammerSuccess() throws InvalidRequestException {
        final PrintStream standardOut = System.out;
        final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
        LocalDate checkoutDate = LocalDate.of(2015, 3, 9);
        ToolRentalRequest request = new ToolRentalRequest("JAKR", checkoutDate, 1000, 0);
        Tool expectedTool = generateJackhammer("Ridgid", request.getToolCode());
        when(mockRepository.getToolData(anyString())).thenReturn(expectedTool);

        RentalAgreement actual = subject.checkout(request);
        String systemOutput = outputStreamCaptor.toString();

        assertToolInfoEquals(expectedTool,actual);
        assertRequestInfoEquals(request,actual);
        //Currency Format Check
        assertTrue(systemOutput.contains(NumberFormat.getCurrencyInstance().format(expectedTool.getDailyCharge())));
        assertTrue(systemOutput.contains("$2,116.92"));
        //Date Format Check
        assertTrue(systemOutput.contains("03/09/15"));

        //Percent Format Check
        assertTrue(systemOutput.contains("0%"));

        System.setOut(standardOut);
    }

    @Test
    @DisplayName("Test independence day holiday check on a SUNDAY")
    void ignoreIndependenceDayOnSunday() throws InvalidRequestException {
        LocalDate checkoutDate = LocalDate.of(2021, 7, 4);
        ToolRentalRequest request = new ToolRentalRequest("JAKR", checkoutDate, 3, 0);
        Tool expectedTool = generateJackhammer("Ridgid", request.getToolCode());
        when(mockRepository.getToolData(anyString())).thenReturn(expectedTool);
        
        RentalAgreement actual = subject.checkout(request);
        
        //Of the three billable days Monday 7/5/21 qualifies as a holiday
        assertEquals(request.getRentalDays() - 1, actual.getChargeDays());
    }
    
    @Test
    @DisplayName("Test independence day holiday check on a SATURDAY")
    void ignoreIndependenceDayOnSaturday() throws InvalidRequestException {
        LocalDate checkoutDate = LocalDate.of(2020, 7, 2);
        ToolRentalRequest request = new ToolRentalRequest("JAKR", checkoutDate, 1, 0);
        Tool expectedTool = generateJackhammer("Ridgid", request.getToolCode());
        when(mockRepository.getToolData(anyString())).thenReturn(expectedTool);
        
        RentalAgreement actual = subject.checkout(request);
        
        //billable Friday 7/3/20 qualifies as a holiday
        //TODO Confirm a rental can have 0 billable days
        assertEquals(request.getRentalDays() - 1, actual.getChargeDays());
    }

    @Test
    @DisplayName("Test Labor Day holiday check when Holiday Charge is No")
    void ignoreLaborDay() throws InvalidRequestException {
        LocalDate checkoutDate = LocalDate.of(2020, 9, 6);
        ToolRentalRequest request = new ToolRentalRequest("JAKR", checkoutDate, 1, 0);
        Tool expectedTool = generateJackhammer("Ridgid", request.getToolCode());
        when(mockRepository.getToolData(anyString())).thenReturn(expectedTool);

        RentalAgreement actual = subject.checkout(request);
        
        assertEquals(request.getRentalDays() - 1, actual.getChargeDays());
    }
    
    @Test
    @DisplayName("Test weekend check when Weekend Charge is No")
    void ignoreWeekends() throws InvalidRequestException {
        LocalDate checkoutDate = LocalDate.of(2021, 4, 9);
        ToolRentalRequest request = new ToolRentalRequest("Stihl", checkoutDate, 3, 0);
        Tool expectedTool = generateChainsaw();
        when(mockRepository.getToolData(anyString())).thenReturn(expectedTool);

        RentalAgreement actual = subject.checkout(request);
        
        assertEquals(request.getRentalDays() - 2, actual.getChargeDays());
    }
    //Compare the request to the rental agreement
    private void assertRequestInfoEquals(ToolRentalRequest request, RentalAgreement actual) {
        assertEquals(request.getCheckoutDate(), actual.getCheckoutDate());
        assertEquals(request.getToolCode(), actual.getToolCode());
        assertEquals(request.getDiscount(), actual.getDiscountPercent());
    }

    //Compare the mocked tool information to the rental agreement
    private void assertToolInfoEquals(Tool expectedTool, RentalAgreement actual) {
        assertEquals(expectedTool.getBrand(), actual.getToolBrand());
        assertEquals(expectedTool.getToolCode(), actual.getToolCode());
        assertEquals(expectedTool.getToolType(), actual.getToolType());
        assertEquals(expectedTool.getDailyCharge(), actual.getDailyRentalCharge());
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
