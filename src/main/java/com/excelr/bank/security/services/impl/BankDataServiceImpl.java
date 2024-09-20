package com.excelr.bank.security.services.impl;

import com.excelr.bank.exception.BankCreationException;
import com.excelr.bank.exception.BankNotFoundException;
import com.excelr.bank.models.Bank;
import com.excelr.bank.payload.response.MessageResponse;
import com.excelr.bank.repository.BankDataRepository;
import com.excelr.bank.security.services.interfaces.BankDataService;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;


@Service
public class BankDataServiceImpl implements BankDataService {

    @Autowired
    private BankDataRepository bankDataRepository;


    public ResponseEntity<?> getAllBankRecords() {
        try {
            List<Bank> bankData = bankDataRepository.findAll();
            return ResponseEntity.ok(bankData.isEmpty() ? Collections.singletonList("No Records Found") : bankData);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error Fetching Data");
        }

    }

    @Override
    public ResponseEntity<?> insertRecord(Bank bank) throws BankCreationException {
        List<Bank> chkBank=bankDataRepository.findByBankName(bank.getBankName());
        if(chkBank!=null && (chkBank.stream()
                .anyMatch(rtvBank -> rtvBank.getIfscCode().equals(bank.getIfscCode()))) && (chkBank.stream().anyMatch(rtvBank -> rtvBank.getAddress().equals(bank.getAddress())))) {
            throw new BankCreationException(bank.getBankName()+ " is already registered with IFSC "+bank.getIfscCode());
        }else{
            if (isBankValid(bank)) {
                bankDataRepository.save(bank);
                return ResponseEntity.status(HttpStatus.OK).body("Congratulations!!! You are Successfully Registered with ExcelR Banking");
            } else {
                throw new BankCreationException("Bank details are invalid");
            }
        }
    }
    @Override
    public Bank getBankById(Long id) {
        return bankDataRepository.findById(id).orElseThrow(() -> new BankNotFoundException(id));
    }

    @Override
    public ResponseEntity<?> updateBank(Bank bank) {
        Bank existedBank = getBankById(bank.getId());
        // Update fields
        existedBank.setBankName(bank.getBankName());
        existedBank.setEmail(bank.getEmail());
        existedBank.setPhoneNo(bank.getPhoneNo());
        existedBank.setWebsite(existedBank.getWebsite());
        existedBank.setCurrency(existedBank.getCurrency());

        bankDataRepository.save(existedBank);
        return ResponseEntity.ok(new MessageResponse("Account Details Updated Success in : "+existedBank));
    }

    @Override
    public ResponseEntity<?> deleteBankById(Long id) {
        Bank bank = bankDataRepository.findById(id).orElseThrow(()-> new RuntimeException("Record Not Available"));
        if(bank==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No Record Available with Id: "+id);
        }else{
            bankDataRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body("Record Delete with Id: "+id);
        }
    }

    @Override
    public ResponseEntity<?> deleteAllBankRecords() {
        List<Bank> bank = bankDataRepository.findAll();
        if(bank.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No Record Available");
        }else{
            bankDataRepository.deleteAll();
            return ResponseEntity.status(HttpStatus.OK).body("All Data Deleted Successfully");
        }
    }


    private boolean isBankValid(Bank bank) {
        return StringUtils.isNotBlank(bank.getBankName());
    }
}
