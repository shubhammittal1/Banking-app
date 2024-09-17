package com.excelr.bank.security.services.interfaces;

import com.excelr.bank.models.Bank;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface BankDataService {



    public Bank getBankById(Long id);


    public ResponseEntity<?> insertRecord(Bank bank);

    public ResponseEntity<?> updateBank(Bank bank);
    void deleteBank(Long id);

}