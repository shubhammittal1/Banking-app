package com.excelr.bank.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ElectricityBillRequest {

    private String providor;

    private String customerId;

    private String accNum;

    private Long accId;

    private BigDecimal amount;
}
