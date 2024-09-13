package com.excelr.bank.repository;

import com.excelr.bank.models.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankDataRepository extends JpaRepository<Bank,Long> {
   Bank findBankByBankName(String bankName);
}
