package com.excelr.bank.security.services.impl;

import com.excelr.bank.exception.AccountDetailsException;
import com.excelr.bank.models.AdminBankDetails;
import com.excelr.bank.repository.AdminBankRepository;
import com.excelr.bank.security.services.interfaces.AdminBankService;
import com.excelr.bank.util.Generator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminBankService {

    @Autowired
    private AdminBankRepository adminRepo;

    @Override
    public AdminBankDetails insertRecord(AdminBankDetails admin) throws AccountDetailsException{
       if(admin==null || admin.getBankName()==null){
           throw new RuntimeException("Admin Bank Details or Name cannot be Null");
       }else {
           admin.setId(new Generator().generateID());
           admin.setAccountNumber(new Generator().generateAcc());
           return adminRepo.save(admin);
       }
    }
}
