package com.excelr.bank.util;

import java.security.SecureRandom;
import java.util.Random;

public class Generator {
//    public String generateId() {
//        return "BMGR" + String.format("%05d", (int) (Math.random() * 100000));
//    }

    public String generateAcc(){
        SecureRandom random=new SecureRandom();
        Long root = random.nextLong(900000000L) + 10000000000L;

        // Generate Account Number by appending a fixed prefix and a cryptographical random number
        String accountNumber =  String.format("%10d", root );

        return "ACC"+accountNumber;

    }

    public Long generateID(){
        Random random=new Random();
        return random.nextLong(9000000) + 1000000;
    }

}
