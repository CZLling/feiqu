package com.feiqu.system;

import java.math.BigDecimal;

public class Demo {

    public static void main(String[] args){
        BigDecimal one = new BigDecimal("12345678912345679894.98");
        BigDecimal two = new BigDecimal("123456789123456794.02");
        BigDecimal newNumber = one.multiply(two);
        System.out.println(newNumber);

        double a = 12345678912345679894.98;
        double b = 123456789123456794.02;
        System.out.println(a*b);
    }
}
