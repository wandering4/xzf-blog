package com.xzf.blog.framework.commons.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonUtilsTest {

    @Test
    void toJsonAndParseObjectShouldRoundTrip() {
        DemoPayload payload = new DemoPayload();
        payload.name = "alice";
        payload.age = 18;

        String json = JsonUtils.toJsonString(payload);
        DemoPayload parsed = JsonUtils.parseObject(json, DemoPayload.class);

        assertEquals("alice", parsed.name);
        assertEquals(18, parsed.age);
    }

    @Test
    void parseObjectShouldReturnNullForBlankJson() {
        assertNull(JsonUtils.parseObject(" ", DemoPayload.class));
    }

    @Test
    void parseMapShouldParseTypedMap() throws Exception {
        Map<String, Integer> parsed = JsonUtils.parseMap("{\"a\":1,\"b\":2}", String.class, Integer.class);

        assertEquals(2, parsed.size());
        assertEquals(1, parsed.get("a"));
        assertEquals(2, parsed.get("b"));
    }

    @Test
    void parseListShouldParseTypedList() throws Exception {
        List<Integer> parsed = JsonUtils.parseList("[1,2,3]", Integer.class);

        assertEquals(List.of(1, 2, 3), parsed);
    }

    @Test
    void parseSetShouldParseTypedSet() throws Exception {
        Set<String> parsed = JsonUtils.parseSet("[\"x\",\"y\"]", String.class);

        assertEquals(2, parsed.size());
        assertTrue(parsed.contains("x"));
        assertTrue(parsed.contains("y"));
    }

    static class DemoPayload {
        public String name;
        public int age;
    }
}
