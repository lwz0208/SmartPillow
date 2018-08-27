package com.example;


public class MyClass {

    public static void main(String[] args) {

        int n = 1234;
        int count = 0;
        while(n != 0){
            count = count * 10 + n % 10;
            n = n / 10;
        }
        System.out.println(String.valueOf(count));
    }
}


