package com.excelr.bank.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StatementRequest {
    private Long accountId;
    private Long userId;
    private String startDate;
    private String endDate;
}
