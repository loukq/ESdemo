package com.lou;

import java.util.Arrays;
import java.util.List;

public class MyTest {
    public static void main(String[] args) {
        List<String> list= Arrays.asList("lou","jkd","dkjfd");

        list.forEach(myAdd::add);
        
    }
}

class myAdd{
    
    public static String add(String s){
        System.out.println("aaa"+s);
        return "hello";
    }
}
