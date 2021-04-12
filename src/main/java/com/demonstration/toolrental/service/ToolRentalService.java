package com.demonstration.toolrental.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import com.demonstration.toolrental.controller.exceptions.InvalidRequestException;
import com.demonstration.toolrental.model.entity.Tool;
import com.demonstration.toolrental.model.request.ToolRentalRequest;
import com.demonstration.toolrental.model.response.RentalAgreement;
import com.demonstration.toolrental.repository.ToolRepository;
import com.demonstration.toolrental.util.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ToolRentalService {

    private ToolRepository toolRepository;

    @Autowired
    public ToolRentalService(ToolRepository toolRepository) {
        this.toolRepository = toolRepository;
    }

    public RentalAgreement checkout(ToolRentalRequest request) throws InvalidRequestException {
        Validator.validateRequest(request);
        Tool rentalTool = toolRepository.getToolData(request.getToolCode());
        List<LocalDate> rentalDates = getRentalDates(request.getCheckoutDate(), request.getRentalDays());
        int billableDays = calculateBillableDays(rentalDates, rentalTool);
        RentalAgreement rentalAgreement = generateAgreement(rentalTool, billableDays, request);
        rentalAgreement.printAgreement();
        return rentalAgreement;
    }

    private int calculateBillableDays(List<LocalDate> rentalDates, Tool rentalTool) {
        int billableDays = 0;
        for (LocalDate localDate : rentalDates) {
            //Don't charge checkout day
            if(localDate.equals(rentalDates.get(0))) {
                continue;
            }
            //Check Holidays first
            if(rentalTool.getHolidayCharge().equalsIgnoreCase("No") 
                    && dateIsHoliday(localDate)) {
                continue;
            }
            //Check Weekends as well in case future tools have no charge on both
            if(rentalTool.getWeekendCharge().equalsIgnoreCase("No")
                    && dateIsWeekend(localDate)) {
                continue;
            }
            billableDays++;
        }
        return billableDays;
    }

    private boolean dateIsWeekend(LocalDate localDate) {
        return localDate.getDayOfWeek().equals(DayOfWeek.SATURDAY) 
        || localDate.getDayOfWeek().equals(DayOfWeek.SUNDAY);
    }
    
    private boolean dateIsHoliday(LocalDate localDate) {
        LocalDate independenceDay = LocalDate.of(localDate.getYear(),7,4);
        if ((localDate.getDayOfWeek().equals(DayOfWeek.FRIDAY) && localDate.plusDays(1).equals(independenceDay))
        || (localDate.getDayOfWeek().equals(DayOfWeek.MONDAY) && localDate.minusDays(1).equals(independenceDay))) {
            return true;
        } else if (localDate.equals(independenceDay) && !dateIsWeekend(localDate)) {
            return true;
        } else if (localDate.getMonth().equals(Month.SEPTEMBER) && localDate.getDayOfMonth() < 8 
                    && localDate.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
            return true;
        }
        return false;
    }
    
    private List<LocalDate> getRentalDates(LocalDate checkoutDate, int rentalDays) {
        List<LocalDate> rentalDates = new ArrayList<LocalDate>();
        rentalDates.add(checkoutDate);
        for (int i = 1; i <= rentalDays; i++) {
            rentalDates.add(checkoutDate.plusDays(i));
        }
        return rentalDates;
    }

    private RentalAgreement generateAgreement(Tool rentalTool, int billableDays, ToolRentalRequest request) {
        RentalAgreement rentalAgreement = new RentalAgreement();
        rentalAgreement.setToolCode(rentalTool.getToolCode());
        rentalAgreement.setToolType(rentalTool.getToolType());
        rentalAgreement.setToolBrand(rentalTool.getBrand());
        rentalAgreement.setRentalDays(request.getRentalDays());
        rentalAgreement.setCheckoutDate(request.getCheckoutDate());
        rentalAgreement.setDueDate(request.getCheckoutDate().plusDays(request.getRentalDays()));
        rentalAgreement.setDailyRentalCharge(rentalTool.getDailyCharge().setScale(2,RoundingMode.HALF_UP));
        rentalAgreement.setChargeDays(billableDays);
        BigDecimal subtotal = rentalTool.getDailyCharge().multiply(new BigDecimal(billableDays));
        rentalAgreement.setPrediscountCharge(subtotal.setScale(2, RoundingMode.HALF_UP));
        rentalAgreement.setDiscountPercent(request.getDiscount());
        BigDecimal discountAmount = calculateDiscountAmount(request.getDiscount(), subtotal);
        rentalAgreement.setDiscountAmount(discountAmount);
        rentalAgreement.setFinalCharge(subtotal.subtract(discountAmount));
        return rentalAgreement;
    }

    private BigDecimal calculateDiscountAmount(int discountPercent, BigDecimal subtotal) {
        if (discountPercent == 0) {
            return BigDecimal.ZERO;
        }
        double discountDecimal = (double) discountPercent / 100.00;
        BigDecimal discount = BigDecimal.valueOf(discountDecimal).setScale(2,RoundingMode.HALF_UP);
        return subtotal.multiply(discount).setScale(2,RoundingMode.HALF_UP);
    }
    
}
