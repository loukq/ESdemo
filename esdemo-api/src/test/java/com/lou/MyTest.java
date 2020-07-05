package com.lou;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;

public class MyTest {
    public static void main(String[] args) {

        Person person = new Person("lou", 23, "背景");

        System.out.println(JSONUtil.toJsonStr(person));
        System.out.println(JSONUtil.parse(person).toJSONString(0));
        System.out.println(JSONUtil.parse(person).toJSONString(2));
        System.out.println(JSON.toJSONString(person));


    }
}

class Person{

    private String name;

    private int age;

    private String address;

    public Person(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Person(String name, int age, String address) {
        this.name = name;
        this.age = age;
        this.address = address;
    }
}
