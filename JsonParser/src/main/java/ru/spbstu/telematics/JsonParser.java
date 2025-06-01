package ru.spbstu.telematics;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonParser {
    private String json;
    private int pos;

    public JsonParser() { this.json = null; this.pos=0; }

    public Object parse(String input) {
        this.pos = 0;
        this.json = input;
        skipWhitespace();
        Object value = parseValue();
        skipWhitespace();
        if (pos != json.length()) throw new JsonException("Extra data at end");
        return value;
    }

    private Object parseValue() {
        skipWhitespace();
        if (match('n','u','l','l')) return null;
        if (match('t','r','u','e')) return true;
        if (match('f','a','l','s','e')) return false;
        char c = current();
        if (c=='"') return parseString();
        if (c=='{') return parseObject();
        if (c=='[') return parseArray();
        if (c=='-' || Character.isDigit(c)) return parseNumber();
        throw new JsonException("Unexpected char at " + pos);
    }

    private Map<String,Object> parseObject() {
        expect('{');
        Map<String,Object> map = new LinkedHashMap<>();
        skipWhitespace();
        if (peek()=='}') { pos++; return map; }
        do {
            skipWhitespace();
            String key = parseString();
            skipWhitespace(); expect(':'); skipWhitespace();
            Object val = parseValue();
            map.put(key, val);
            skipWhitespace();
        } while (consumeIf(','));
        skipWhitespace(); expect('}');
        return map;
    }

    private List<Object> parseArray() {
        expect('[');
        List<Object> list = new ArrayList<>();
        skipWhitespace();
        if (peek()==']') { pos++; return list; }
        do {
            skipWhitespace(); list.add(parseValue()); skipWhitespace();
        } while (consumeIf(','));
        expect(']');
        return list;
    }

    private String parseString() {
        expect('"'); StringBuilder sb=new StringBuilder();
        while (true) {
            char c = next();
            if (c=='"') break;
            if (c=='\\') {
                char esc = next();
                if (esc=='"'||esc=='\\'||esc=='/') sb.append(esc);
                else if (esc=='b') sb.append('\b');
                else if (esc=='f') sb.append('\f');
                else if (esc=='n') sb.append('\n');
                else if (esc=='r') sb.append('\r');
                else if (esc=='t') sb.append('\t');
                else throw new JsonException("Invalid escape \\"+esc);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private Number parseNumber() {
        int start=pos;
        if (peek()=='-') pos++;
        while (Character.isDigit(peek())) pos++;
        if (peek()=='.') { pos++; while (Character.isDigit(peek())) pos++; }
        if (peek()=='e'||peek()=='E') {
            pos++; if (peek()=='+'||peek()=='-') pos++; while (Character.isDigit(peek())) pos++;
        }
        String num = json.substring(start, pos);
        if (num.contains(".")||num.contains("e")||num.contains("E")) return Double.parseDouble(num);
        return Long.parseLong(num);
    }

    private boolean match(char... cs) {
        int i=0;
        while (i<cs.length && peek()==cs[i]) { pos++; i++; }
        return i==cs.length;
    }
    private boolean consumeIf(char c) { if (peek()==c) { pos++; return true;} return false; }
    private void expect(char c) { if (peek()!=c) throw new JsonException("Expected '"+c+"' at "+pos); pos++; }
    private char peek() { return pos<json.length()?json.charAt(pos):'\0'; }
    private char current() { return peek(); }
    private char next() { return json.charAt(pos++); }
    private void skipWhitespace() { while (Character.isWhitespace(peek())) pos++; }
}

