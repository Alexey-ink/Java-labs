package ru.spbstu.telematics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JsonInheritanceTest {

    public static class Animal {
        public String species;
        public int age;
    }

    public static class Cat extends Animal {
        public String name;
        public boolean indoor;
    }

    @Test
    void testCatDeserialization() {
        String json = "{\"species\":\"cat\",\"age\":3,\"name\":\"Murka\",\"indoor\":true}";
        Cat cat = JsonMapper.fromJson(json, Cat.class);

        assertEquals("cat", cat.species);
        assertEquals(3, cat.age);
        assertEquals("Murka", cat.name);
        assertTrue(cat.indoor);
    }

    @Test
    void testCatSerialization() {
        Cat cat = new Cat();
        cat.species = "cat";
        cat.age = 3;
        cat.name = "Murka";
        cat.indoor = true;

        String json = JsonMapper.toJson(cat);
        // JSON порядок может быть разным — проверим по содержимому
        assertTrue(json.contains("\"species\":\"cat\""));
        assertTrue(json.contains("\"age\":3"));
        assertTrue(json.contains("\"name\":\"Murka\""));
        assertTrue(json.contains("\"indoor\":true"));
    }

    @Test
    void testCatPartialJson() {
        String json = "{\"name\":\"Barsik\"}";
        Cat cat = JsonMapper.fromJson(json, Cat.class);

        assertEquals("Barsik", cat.name);
        assertNull(cat.species);     // унаследованное поле
        assertEquals(0, cat.age);    // int -> default 0
        assertFalse(cat.indoor);     // boolean -> default false
    }
}
