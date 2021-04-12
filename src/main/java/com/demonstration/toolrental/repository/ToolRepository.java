package com.demonstration.toolrental.repository;

import com.demonstration.toolrental.model.entity.Tool;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolRepository extends JpaRepository<Tool, Integer>{

    @Query ( value = 
        "SELECT TOOLS.TOOL_CODE, TOOLS.TOOL_TYPE, TOOLS.BRAND, " + 
        "TOOL_CHARGES.DAILY_CHARGE, TOOL_CHARGES.WEEKDAY_CHARGE, " + 
        "TOOL_CHARGES.WEEKEND_CHARGE, TOOL_CHARGES.HOLIDAY_CHARGE \n" +
        "FROM TOOLS INNER JOIN TOOL_CHARGES \n" + 
        "ON TOOLS.TOOL_TYPE = TOOL_CHARGES.TOOL_TYPE \n" + 
        "WHERE TOOLS.TOOL_CODE = ?1", nativeQuery = true
    )
    public Tool getToolData(String toolCode);
    
}
