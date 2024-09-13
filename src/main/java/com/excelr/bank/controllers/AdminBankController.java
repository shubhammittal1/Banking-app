package com.excelr.bank.controllers;

import com.excelr.bank.exception.BadRequestException;
import com.excelr.bank.exception.DatabaseException;
import com.excelr.bank.exception.DuplicateRecordException;
import com.excelr.bank.models.AdminBankDetails;
import com.excelr.bank.security.services.impl.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
public class AdminBankController {

    @Autowired
    private AdminServiceImpl adminService;

    @PostMapping("/insert")
    public ResponseEntity<?> insertRecord(@RequestBody AdminBankDetails adminBankDetails) {
        try {
            adminService.insertRecord(adminBankDetails);
            return ResponseEntity.status(HttpStatus.OK).body("Data Inserted Successfully");
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (DuplicateRecordException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (DatabaseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inserting record");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }
}