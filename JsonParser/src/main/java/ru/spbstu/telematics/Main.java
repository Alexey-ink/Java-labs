package ru.spbstu.telematics;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Person person = new Person();
        person.name = "Alexey";
        person.age = 22;
        person.emails = new String[]{"alexey@example.com", "alexey@work.com"};
        person.scores = Arrays.asList(95, 87, 100);
        person.address = new Address("Санкт-Петербург", "Ленина 1");

        // Сериализация в JSON
        String json = JsonMapper.toJson(person);
        System.out.println("Сериализованный JSON:");
        System.out.println(json);

        // Преобразование в Map
        Map<String, Object> map = JsonMapper.toMap(json);
        System.out.println("\nКак Map:");
        System.out.println(map);

        // Десериализация обратно в объект
        Person restored = JsonMapper.fromJson(json, Person.class);
        System.out.println();
        System.out.println("\nВосстановленный объект:");
        System.out.println("Имя: " + restored.name);
        System.out.println("Возраст: " + restored.age);
        System.out.println("Emails: " + Arrays.toString(restored.emails));
        System.out.println("Оценки: " + restored.scores);
        System.out.println("Город: " + (restored.address != null ? restored.address.city : null));
    }

    public static class Address {
        public String city;
        public String street;

        public Address() {}

        public Address(String city, String street) {
            this.city = city;
            this.street = street;
        }
    }

    public static class Person {
        public String name;
        public int age;
        public String[] emails;
        public List<Integer> scores;
        public Address address;
    }
}
