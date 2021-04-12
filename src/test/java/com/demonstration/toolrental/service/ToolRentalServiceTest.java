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
import com.demonstration.toolrental.testutils.TestUtils;

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
    @DisplayName("Check weekday success")
    void checkoutLadderSuccess() throws InvalidRequestException {
        LocalDate checkoutDate = LocalDate.of(2015, 3, 9);
        ToolRentalRequest request = new ToolRentalRequest("LADW", checkoutDate, 5, 0);
        Tool expectedTool = TestUtils.generateLadder();
        when(mockRepository.getToolData(anyString())).thenReturn(expectedTool);

        RentalAgreement actual = subject.checkout(request);

        verify(mockRepository).getToolData(anyString());
        TestUtils.assertToolInfoEquals(expectedTool,actual);
        TestUtils.assertRequestInfoEquals(request,actual);
        
        assertEquals(request.getRentalDays(), actual.getChargeDays());
        assertEquals(expectedTool.getDailyCharge().multiply(new BigDecimal(request.getRentalDays())), actual.getFinalCharge());
    }

    @Test
    @DisplayName("Check discount calculation")
    void discountCalculationSuccess() throws InvalidRequestException {
        LocalDate checkoutDate = LocalDate.of(2015, 3, 9);
        ToolRentalRequest request = new ToolRentalRequest("LADW", checkoutDate, 5, 71);
        Tool expectedTool = TestUtils.generateLadder();
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
        Tool expectedTool = TestUtils.generateJackhammer("Ridgid", request.getToolCode());
        when(mockRepository.getToolData(anyString())).thenReturn(expectedTool);

        RentalAgreement actual = subject.checkout(request);
        String systemOutput = outputStreamCaptor.toString();

        TestUtils.assertToolInfoEquals(expectedTool,actual);
        TestUtils.assertRequestInfoEquals(request,actual);
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
    @DisplayName("Check Independence Day when Holiday Charges are Yest")
    void checkoutChainsawSuccess() throws InvalidRequestException {
        LocalDate checkoutDate = LocalDate.of(2020, 7, 2);
        ToolRentalRequest request = new ToolRentalRequest("CHNS", checkoutDate, 1, 0);
        Tool expectedTool = TestUtils.generateChainsaw();
        when(mockRepository.getToolData(anyString())).thenReturn(expectedTool);

        RentalAgreement actual = subject.checkout(request);

        verify(mockRepository).getToolData(anyString());
        TestUtils.assertToolInfoEquals(expectedTool,actual);
        TestUtils.assertRequestInfoEquals(request,actual);

        assertEquals(request.getRentalDays(), actual.getChargeDays());
        assertEquals(expectedTool.getDailyCharge().multiply(new BigDecimal(request.getRentalDays())), actual.getFinalCharge());
    }

    @Test
    @DisplayName("Check Independence day on Sunday when Holiday Charge is No")
    void ignoreIndependenceDayOnSunday() throws InvalidRequestException {
        LocalDate checkoutDate = LocalDate.of(2021, 7, 4);
        ToolRentalRequest request = new ToolRentalRequest("JAKR", checkoutDate, 3, 0);
        Tool expectedTool = TestUtils.generateJackhammer("Ridgid", request.getToolCode());
        when(mockRepository.getToolData(anyString())).thenReturn(expectedTool);
        
        RentalAgreement actual = subject.checkout(request);
        
        //Of the three billable days Monday 7/5/21 qualifies as a holiday
        assertEquals(request.getRentalDays() - 1, actual.getChargeDays());
    }
    
    @Test
    @DisplayName("Check Independence day on Saturday when Holiday Charge is No")
    void ignoreIndependenceDayOnSaturday() throws InvalidRequestException {
        LocalDate checkoutDate = LocalDate.of(2020, 7, 2);
        ToolRentalRequest request = new ToolRentalRequest("JAKR", checkoutDate, 1, 0);
        Tool expectedTool = TestUtils.generateJackhammer("Ridgid", request.getToolCode());
        when(mockRepository.getToolData(anyString())).thenReturn(expectedTool);
        
        RentalAgreement actual = subject.checkout(request);
        
        //billable Friday 7/3/20 qualifies as a holiday
        //TODO Confirm a rental can have 0 billable days
        assertEquals(request.getRentalDays() - 1, actual.getChargeDays());
    }

    @Test
    @DisplayName("Check Labor Day when Holiday Charge is No")
    void ignoreLaborDay() throws InvalidRequestException {
        LocalDate checkoutDate = LocalDate.of(2020, 9, 6);
        ToolRentalRequest request = new ToolRentalRequest("JAKR", checkoutDate, 1, 0);
        Tool expectedTool = TestUtils.generateJackhammer("Ridgid", request.getToolCode());
        when(mockRepository.getToolData(anyString())).thenReturn(expectedTool);

        RentalAgreement actual = subject.checkout(request);
        
        assertEquals(request.getRentalDays() - 1, actual.getChargeDays());
    }
    
    @Test
    @DisplayName("Check weekend when Weekend Charge is No")
    void ignoreWeekends() throws InvalidRequestException {
        LocalDate checkoutDate = LocalDate.of(2021, 4, 9);
        ToolRentalRequest request = new ToolRentalRequest("CHNS", checkoutDate, 3, 0);
        Tool expectedTool = TestUtils.generateChainsaw();
        when(mockRepository.getToolData(anyString())).thenReturn(expectedTool);

        RentalAgreement actual = subject.checkout(request);

        TestUtils.assertToolInfoEquals(expectedTool,actual);
        TestUtils.assertRequestInfoEquals(request,actual);
        assertEquals(request.getRentalDays() - 2, actual.getChargeDays());
    }

    @Test
    @DisplayName("Check weekend when Weekend Charge is Yes")
    void includeWeekends() throws InvalidRequestException {
        LocalDate checkoutDate = LocalDate.of(2021, 4, 9);
        ToolRentalRequest request = new ToolRentalRequest("LADW", checkoutDate, 3, 0);
        Tool expectedTool = TestUtils.generateLadder();
        when(mockRepository.getToolData(anyString())).thenReturn(expectedTool);

        RentalAgreement actual = subject.checkout(request);

        TestUtils.assertToolInfoEquals(expectedTool,actual);
        TestUtils.assertRequestInfoEquals(request,actual);
        assertEquals(request.getRentalDays(), actual.getChargeDays());
    }
}
