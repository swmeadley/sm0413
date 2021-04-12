package com.demonstration.toolrental.testutils;

import com.demonstration.toolrental.model.entity.Tool;
import com.demonstration.toolrental.model.request.ToolRentalRequest;
import com.demonstration.toolrental.model.response.RentalAgreement;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUtils {
    private TestUtils(){
    }

    //Compare the request to the rental agreement
    public static void assertRequestInfoEquals(ToolRentalRequest request, RentalAgreement actual) {
        assertEquals(request.getCheckoutDate(), actual.getCheckoutDate());
        assertEquals(request.getToolCode(), actual.getToolCode());
        assertEquals(request.getDiscount(), actual.getDiscountPercent());
    }

    //Compare the mocked tool information to the rental agreement
    public static void assertToolInfoEquals(Tool expectedTool, RentalAgreement actual) {
        assertEquals(expectedTool.getBrand(), actual.getToolBrand());
        assertEquals(expectedTool.getToolCode(), actual.getToolCode());
        assertEquals(expectedTool.getToolType(), actual.getToolType());
        assertEquals(expectedTool.getDailyCharge(), actual.getDailyRentalCharge());
    }

    public static Tool generateLadder() {
        return Tool.builder()
                .toolType("Ladder")
                .brand("Werner")
                .toolCode("LADW")
                .dailyCharge(new BigDecimal(1.99).setScale(2, RoundingMode.HALF_UP))
                .weekdayCharge("Yes")
                .weekendCharge("Yes")
                .holidayCharge("No")
                .build();
    }

    public static Tool generateChainsaw() {
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

    public static Tool generateJackhammer(String brand, String toolCode) {
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
