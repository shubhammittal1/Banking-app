package com.excelr.bank.repository;

import com.excelr.bank.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {

    Account findByUserId(Long customerId);

    Optional<Account> findByAccountNumber(String acc);
}
