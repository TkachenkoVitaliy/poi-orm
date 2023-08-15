package com.poiorm;

import java.lang.reflect.Field;

public class Main {
    public static void main(String[] args) throws NoSuchFieldException {
        Person person = new Person();
        Class<? extends Person> type = person.getClass();
        Field field = type.getDeclaredField("name");
        System.out.println(field.getType() == String.class);

        Field secondField = type.getDeclaredField("age");
        System.out.println(secondField.getType() == double.class);

    }


}

class Person {

    String name = "test";
    double age = 10;
}
