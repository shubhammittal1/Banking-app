package com.excelr.bank.security.services.interfaces;

import com.excelr.bank.models.Bank;

import java.util.List;


public interface BanksDataService {

    List<Bank> getAllBanks();
    Bank getBankById(Long id);


    Bank insertRecord(Bank bank);

    Bank updateBank(Bank bank);
    void deleteBank(Long id);

}