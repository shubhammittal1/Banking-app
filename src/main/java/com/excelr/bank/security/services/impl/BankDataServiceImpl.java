package com.excelr.bank.security.services.impl;

import com.excelr.bank.exception.BankCreationException;
import com.excelr.bank.exception.BankNotFoundException;
import com.excelr.bank.models.Bank;
import com.excelr.bank.repository.BankDataRepository;
import com.excelr.bank.security.services.interfaces.BanksDataService;
import com.excelr.bank.util.Generator;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BankDataServiceImpl implements BanksDataService {

    @Autowired
    private BankDataRepository bankDataRepository;



    @Override
    public List<Bank> getAllBanks() {
        return bankDataRepository.findAll();
    }

    @Override
    public Bank insertRecord(Bank bank) throws BankCreationException {
        if(isBankValid(bank)) {
            Generator generator= new Generator();
            bank.setAccNumber(generator.generateAcc());
            return bankDataRepository.save(bank);
        }else{
            throw new BankCreationException("Bank details are invalid");
        }
    }

    @Override
    public Bank getBankById(Long id) {
        return bankDataRepository.findById(id).orElseThrow(() -> new BankNotFoundException(id));
    }


    @Override
    public Bank updateBank(Bank bank) {
        Bank existedBank = getBankById(bank.getId());
        // Update fields
        existedBank.setBankName(bank.getBankName());
        existedBank.setEmail(bank.getEmail());
        existedBank.setPhoneNo(bank.getPhoneNo());
        existedBank.setWebsite(existedBank.getWebsite());
        existedBank.setCurrency(existedBank.getCurrency());

        return bankDataRepository.save(existedBank);
    }

    @Override
    public void deleteBank(Long id) {
        bankDataRepository.deleteById(id);
    }


    private boolean isBankValid(Bank bank) {
        return StringUtils.isNotBlank(bank.getBankName()) && StringUtils.isNotBlank(bank.getEmail());
    }

}
