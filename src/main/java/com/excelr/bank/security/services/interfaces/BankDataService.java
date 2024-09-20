package com.excelr.bank.security.services.interfaces;

import com.excelr.bank.models.Bank;
import org.springframework.http.ResponseEntity;


public interface BankDataService {



    public Bank getBankById(Long id);


    public ResponseEntity<?> insertRecord(Bank bank);

    public ResponseEntity<?> updateBank(Bank bank);
    ResponseEntity<?> deleteBankById(Long id);

    ResponseEntity<?> deleteAllBankRecords();
}