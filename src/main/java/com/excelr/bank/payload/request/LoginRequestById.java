package com.excelr.bank.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
public class LoginRequestById {

    @NotBlank
    @NotNull
    private String id;
    @NotBlank
    @NotNull
    private String password;
}
