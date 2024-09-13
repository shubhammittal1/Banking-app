package com.excelr.bank.payload.request;

import lombok.Data;

@Data
public class GenerateOtpRequest {
    private String email;
    private String phoneNumber;
}