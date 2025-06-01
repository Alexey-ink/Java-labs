package ru.spbstu.telematics;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    static public class Person {
        public String name;
        public int age;
        public String[] emails;
    }

    static public class Tags {
        public List<String> tags;
    }

    static public class Book {
        public String title;
        public Person author;
    }

    static public class Wrapper {
        public Map<String, Object> data;
    }


    @Test
    void testPrimitives() {
        String json = "{\"name\":\"Alexey\",\"age\":22}";
        Person p = JsonMapper.fromJson(json, Person.class);
        assertEquals("Alexey", p.name);
        assertEquals(22, p.age);
    }

    @Test
    void testArray() {
        String json = "{\"name\":\"Alex\",\"age\":25,\"emails\":[\"alex@gmail.com\",\"alex@work.com\"]}";
        Person p = JsonMapper.fromJson(json, Person.class);
        assertEquals("Alex", p.name);
        assertEquals(25, p.age);
        assertArrayEquals(new String[]{"alex@gmail.com", "alex@work.com"}, p.emails);
    }

    @Test
    void testList() {
        String json = "{\"tags\":[\"json\",\"parser\",\"java\"]}";
        Tags t = JsonMapper.fromJson(json, Tags.class);
        assertEquals(List.of("json", "parser", "java"), t.tags);
    }

    @Test
    void testNestedObject() {
        String json = "{\"title\":\"Book\",\"author\":{\"name\":\"Leo\",\"age\":51}}";
        Book b = JsonMapper.fromJson(json, Book.class);
        assertEquals("Book", b.title);
        assertNotNull(b.author);
        assertEquals("Leo", b.author.name);
        assertEquals(51, b.author.age);
    }

    @Test
    void testNulls() {
        String json = "{\"name\":null,\"age\":null,\"emails\":null}";
        Person p = JsonMapper.fromJson(json, Person.class);
        assertNull(p.name);
        assertEquals(0, p.age); // Примитив int — default = 0
        assertNull(p.emails);
    }

    @Test
    void testComplexNested() {
        String json = """
    {
      "data": {
        "data": [
          [
            [1, 2, 3],
            [{"val":1}, {"val":2}],
            [true, true, false]
          ],
          ["a", "b", "c"]
        ]
      }
    }
    """;
        Wrapper wrapper = JsonMapper.fromJson(json, Wrapper.class);

        assertNotNull(wrapper.data);
        assertInstanceOf(Map.class, wrapper.data);

        Map<?, ?> outerMap = (Map<?, ?>) wrapper.data;
        Object nestedData = outerMap.get("data");
        assertInstanceOf(List.class, nestedData);

        List<?> outerList = (List<?>) nestedData;
        assertEquals(2, outerList.size());

        Object first = outerList.get(0);
        assertInstanceOf(List.class, first);
    }

}
