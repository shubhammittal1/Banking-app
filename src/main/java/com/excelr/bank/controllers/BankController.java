package com.excelr.bank.controllers;

import com.excelr.bank.models.Bank;
import com.excelr.bank.security.services.impl.BankDataServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/RegisteredBanks")
public class BankController {


    @Autowired
    private BankDataServiceImpl bankDataService;

    @GetMapping("/getAllRecords")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllRecords(){
        return bankDataService.getAllBankRecords();
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getRecordById(@PathVariable Long id){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(bankDataService.getBankById(id));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/insert")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> insertBank(@RequestBody Bank bank){
        try {
            return bankDataService.insertRecord(bank);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateBank(@RequestBody Bank bank){
        return bankDataService.updateBank(bank);
    }

    @DeleteMapping("/deleteRecord/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public  ResponseEntity<?> deleteBankRecords(@PathVariable Long id){
        return bankDataService.deleteBankById(id);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public  ResponseEntity<?> deleteAllBankRecords(){
        return bankDataService.deleteAllBankRecords();
    }
}


