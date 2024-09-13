package com.excelr.bank.exception;


import org.springframework.security.core.AuthenticationException;

public class AccountDetailsException extends AuthenticationException {
    public AccountDetailsException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public AccountDetailsException(String msg) {
        super(msg);
    }
}
