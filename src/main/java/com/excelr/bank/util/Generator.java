package com.excelr.bank.util;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

public class Generator {
    public String generateTransactionId() {
        UUID randomUUID = UUID.randomUUID();
        return randomUUID.toString();
    }

    public String generateAcc(){
        SecureRandom random=new SecureRandom();
        Long root = random.nextLong(900000000L) + 10000000000L;

        // Generate Account Number by appending a fixed prefix and a cryptographical random number
       return  String.format("%10d", root );
    }

    public Long generateID(){
        Random random=new Random();
        return random.nextLong(9000000) + 1000000;
    }

}
