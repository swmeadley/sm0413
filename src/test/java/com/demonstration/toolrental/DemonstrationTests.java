package com.demonstration.toolrental;

import com.demonstration.toolrental.controller.exceptions.EmptyResultSetException;
import com.demonstration.toolrental.controller.exceptions.InvalidRequestException;
import com.demonstration.toolrental.model.entity.Tool;
import com.demonstration.toolrental.model.request.ToolRentalRequest;
import com.demonstration.toolrental.model.response.RentalAgreement;
import com.demonstration.toolrental.repository.ToolRepository;
import com.demonstration.toolrental.service.ToolRentalService;
import com.demonstration.toolrental.testutils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static com.demonstration.toolrental.util.ApplicationConstants.INVALID_DISCOUNT_PERCENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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
        Tool expectedTool = TestUtils.generateJackhammer("Ridgid", "JAKR");
        when(mockRepository.getToolData(anyString())).thenReturn(expectedTool);

        try {
            RentalAgreement actual = subject.checkout(request);
            fail();
        } catch (InvalidRequestException | EmptyResultSetException e) {
            assertEquals(INVALID_DISCOUNT_PERCENT, e.getLocalizedMessage());
        }
    }

    @Test
    @DisplayName("TEST 2")
    void testTwo() throws InvalidRequestException, EmptyResultSetException {
        LocalDate checkoutDate = LocalDate.of(2020, 7, 2);
        ToolRentalRequest request = new ToolRentalRequest("LADW", checkoutDate, 3, 10);
        Tool expectedTool = TestUtils.generateLadder();
        when(mockRepository.getToolData(anyString())).thenReturn(expectedTool);

        RentalAgreement actual = subject.checkout(request);

        TestUtils.assertRequestInfoEquals(request,actual);
        TestUtils.assertToolInfoEquals(expectedTool,actual);
        //Independence Day on Saturday 3 - 1 = 2
        assertEquals(2, actual.getChargeDays());
        //2 * 1.99 = 3.98 Pre-Discount Charge
        assertEquals(BigDecimal.valueOf(3.98),actual.getPrediscountCharge());
        //3.98 * .1 = .398 rounds up to .40 Discount Amount
        assertEquals(BigDecimal.valueOf(.40).setScale(2,RoundingMode.UNNECESSARY),actual.getDiscountAmount());
        //3.98 - .4 = 3.58 Final Charge
        assertEquals(BigDecimal.valueOf(3.58), actual.getFinalCharge());
    }

    @Test
    @DisplayName("TEST 3")
    void testThree() throws InvalidRequestException, EmptyResultSetException {
        LocalDate checkoutDate = LocalDate.of(2015, 7, 2);
        ToolRentalRequest request = new ToolRentalRequest("CHNS", checkoutDate, 5, 25);
        Tool expectedTool = TestUtils.generateChainsaw();
        when(mockRepository.getToolData(anyString())).thenReturn(expectedTool);

        RentalAgreement actual = subject.checkout(request);

        TestUtils.assertRequestInfoEquals(request,actual);
        TestUtils.assertToolInfoEquals(expectedTool,actual);
        //Does not charge Weekends 5 - 2 = 3
        assertEquals(3, actual.getChargeDays());
        //3 * 1.49 = 4.47 Pre-Discount Charge
        assertEquals(BigDecimal.valueOf(4.47),actual.getPrediscountCharge());
        //4.47 * .25 = 1.1175 rounds up to 1.12 Discount Amount
        assertEquals(BigDecimal.valueOf(1.12),actual.getDiscountAmount());
        //4.47 - 1.12 = 3.35 Final Charge
        assertEquals(BigDecimal.valueOf(3.35), actual.getFinalCharge());
    }

    @Test
    @DisplayName("TEST 4")
    void testFour() throws InvalidRequestException, EmptyResultSetException {
        LocalDate checkoutDate = LocalDate.of(2015, 9, 3);
        ToolRentalRequest request = new ToolRentalRequest("JAKD", checkoutDate, 6, 0);
        Tool expectedTool = TestUtils.generateJackhammer("DeWalt", "JAKD");
        when(mockRepository.getToolData(anyString())).thenReturn(expectedTool);

        RentalAgreement actual = subject.checkout(request);

        TestUtils.assertRequestInfoEquals(request,actual);
        TestUtils.assertToolInfoEquals(expectedTool,actual);
        //Does not charge Weekends or Labor Day 6 - 3 = 3
        assertEquals(3, actual.getChargeDays());
        //3 * 2.99 = 8.97 Pre-Discount Charge
        assertEquals(BigDecimal.valueOf(8.97),actual.getPrediscountCharge());
        //8.97 * 0 = 0.00 Discount Amount
        assertEquals(BigDecimal.valueOf(0.00).setScale(2,RoundingMode.UNNECESSARY),actual.getDiscountAmount());
        //8.97 - 0 = 8.97 Final Charge
        assertEquals(BigDecimal.valueOf(8.97), actual.getFinalCharge());
    }

    @Test
    @DisplayName("TEST 5")
    void testFive() throws InvalidRequestException, EmptyResultSetException {
        LocalDate checkoutDate = LocalDate.of(2015, 7, 2);
        ToolRentalRequest request = new ToolRentalRequest("JAKR", checkoutDate, 9, 0);
        Tool expectedTool = TestUtils.generateJackhammer("Ridgid", "JAKR");
        when(mockRepository.getToolData(anyString())).thenReturn(expectedTool);

        RentalAgreement actual = subject.checkout(request);

        TestUtils.assertRequestInfoEquals(request,actual);
        TestUtils.assertToolInfoEquals(expectedTool,actual);
        //Does not charge Weekends(3) or Independence Day(1) 9 - 4 = 5
        assertEquals(5, actual.getChargeDays());
        //5 * 2.99 = 14.95 Pre-Discount Charge
        assertEquals(BigDecimal.valueOf(14.95),actual.getPrediscountCharge());
        //14.95 * 0 = 0.00 Discount Amount
        assertEquals(BigDecimal.valueOf(0.00).setScale(2,RoundingMode.UNNECESSARY),actual.getDiscountAmount());
        //14.95 - 0 = 14.95 Final Charge
        assertEquals(BigDecimal.valueOf(14.95), actual.getFinalCharge());
    }

    @Test
    @DisplayName("TEST 6")
    void testSix() throws InvalidRequestException, EmptyResultSetException {
        LocalDate checkoutDate = LocalDate.of(2020, 7, 2);
        ToolRentalRequest request = new ToolRentalRequest("JAKR", checkoutDate, 4, 50);
        Tool expectedTool = TestUtils.generateJackhammer("Ridgid", "JAKR");
        when(mockRepository.getToolData(anyString())).thenReturn(expectedTool);

        RentalAgreement actual = subject.checkout(request);

        TestUtils.assertRequestInfoEquals(request,actual);
        TestUtils.assertToolInfoEquals(expectedTool,actual);
        //Does not charge Weekends or Labor Day 4 - 3 = 1
        assertEquals(1, actual.getChargeDays());
        //1 * 2.99 = 2.99 Pre-Discount Charge
        assertEquals(BigDecimal.valueOf(2.99),actual.getPrediscountCharge());
        //2.99 * .50 = 1.495 Round Half Up is 1.50 Discount Amount
        assertEquals(BigDecimal.valueOf(1.50).setScale(2,RoundingMode.UNNECESSARY),actual.getDiscountAmount());
        //2.99 - 1.50 = 1.49 Final Charge
        assertEquals(BigDecimal.valueOf(1.49), actual.getFinalCharge());
    }
}
