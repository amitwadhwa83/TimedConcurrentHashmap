import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class TimedConcurrentHashMapTest {

    private TimedConcurrentHashMap<String, String> map;

    @BeforeEach
    void setUp() {
        map = new TimedConcurrentHashMap<>();
    }

    @Test
    void testPutAndGet() {
        map.put("key1", "value1");
        assertEquals("value1", map.get("key1"));
    }

    @Test
    void testGetElapsedTime() throws InterruptedException {
        map.put("key1", "value1");
        TimeUnit.MILLISECONDS.sleep(100);
        long elapsedTime = map.getElapsedTime("key1");
        assertTrue(elapsedTime >= 100);
    }

    @Test
    void testContainsKey() {
        map.put("key1", "value1");
        assertTrue(map.containsKey("key1"));
        assertFalse(map.containsKey("key2"));
    }

    @Test
    void testContainsValue() {
        map.put("key1", "value1");
        assertTrue(map.containsValue("value1"));
        assertFalse(map.containsValue("value2"));
    }

    @Test
    void testRemove() {
        map.put("key1", "value1");
        assertEquals("value1", map.remove("key1"));
        assertNull(map.get("key1"));
    }

    @Test
    void testReplace() {
        map.put("key1", "value1");
        assertTrue(map.replace("key1", "value1", "value2"));
        assertEquals("value2", map.get("key1"));
    }

    @Test
    void testPutIfAbsent() {
        map.putIfAbsent("key1", "value1");
        assertEquals("value1", map.get("key1"));
        map.putIfAbsent("key1", "value2");
        assertEquals("value1", map.get("key1"));
    }

    @Test
    void testClear() {
        map.put("key1", "value1");
        map.clear();
        assertTrue(map.isEmpty());
    }

    @Test
    void testSize() {
        map.put("key1", "value1");
        map.put("key2", "value2");
        assertEquals(2, map.size());
    }

    @Test
    void testIsEmpty() {
        assertTrue(map.isEmpty());
        map.put("key1", "value1");
        assertFalse(map.isEmpty());
    }
}