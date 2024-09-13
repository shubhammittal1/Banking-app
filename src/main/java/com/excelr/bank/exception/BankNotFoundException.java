package com.excelr.bank.exception;

public class BankNotFoundException extends RuntimeException{

    private Long bankId;

    public BankNotFoundException(Long bankId) {
        super(String.format("Bank with ID %d not found", bankId));
        this.bankId = bankId;
    }

    public Long getBankId() {
        return bankId;
    }

}
