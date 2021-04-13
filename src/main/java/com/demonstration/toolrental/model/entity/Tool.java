package com.demonstration.toolrental.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "TOOLS")
public class Tool {
    @Column(name = "TOOL_TYPE")
    private String toolType;
    @Column(name = "BRAND")
    private String brand;
    @Id
    @Column(name = "TOOL_CODE")
    private String toolCode;
    @Column(name = "DAILY_CHARGE")
    private BigDecimal dailyCharge;
    @Column(name = "WEEKDAY_CHARGE")
    private String weekdayCharge;
    @Column(name = "WEEKEND_CHARGE")
    private String weekendCharge;
    @Column(name = "HOLIDAY_CHARGE")
    private String holidayCharge;
}
