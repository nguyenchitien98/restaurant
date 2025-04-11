package com.tien.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyRevenueDTO {
    private Integer dayOfWeek;
    private String weekday;
    private Double totalRevenue;
}
