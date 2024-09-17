package com.excelr.bank.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class LoginRequest {

    private Long userId;

	private Long adminId;

	@NotBlank
	private String password;



}