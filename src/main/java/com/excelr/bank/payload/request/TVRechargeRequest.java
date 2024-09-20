package com.excelr.bank.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TVRechargeRequest {

    private String provider;
    private BigDecimal rechgPlan;
    private BigDecimal amount;
    private Long custId;
    private String accNum;
}
