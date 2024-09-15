package com.excelr.bank.util;

import com.excelr.bank.models.Account;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ROIGenerator {

    private BigDecimal savingAccount(BigDecimal Balance){
        Account account=new Account();
        BigDecimal interest = BigDecimal.valueOf(.025);
        LocalDateTime dateTime= account.getCreatedAt().plusYears(1);
        if(LocalDate.now().equals(dateTime.toLocalDate()) ) {
           if(account.getBalance().compareTo(BigDecimal.valueOf(10000.00))>0) {
               return account.getBalance().add(account.getBalance().multiply(interest));
           }
        }
        return account.getBalance();
    }
}
