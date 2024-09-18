package com.excelr.bank.controllers;

import com.excelr.bank.models.Bank;
import com.excelr.bank.security.services.impl.BankDataServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/RegisteredBanks")
public class BankController {


    @Autowired
    private BankDataServiceImpl bankDataService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllRecords(){
//        bankDataService.getAllBanks();
        return ResponseEntity.status(HttpStatus.OK).body("Retrieval Successful");
    }

    @GetMapping("/getById")
    public ResponseEntity<?> getRecordById(@RequestParam Long id){
        try {
            bankDataService.getBankById(id);
            return ResponseEntity.status(HttpStatus.OK).body("Retrieval Successful");
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insertBank(@RequestBody Bank bank){
        try {
            return bankDataService.insertRecord(bank);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateBank(@RequestBody Bank bank){
        return bankDataService.updateBank(bank);
    }
}
