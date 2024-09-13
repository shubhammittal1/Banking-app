package com.excelr.bank.payload.request;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class ForgotPasswordRequest {

    private String email;
//    private String phoneNumber;

}
