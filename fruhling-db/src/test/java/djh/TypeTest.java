package djh;

import java.util.HashMap;
import java.util.Map;

public class TypeTest {

    private final Map<String, Object> cache = new HashMap<String, Object>();

    public <T> T get(String key) {
        @SuppressWarnings("unchecked")
        T t = (T) cache.get(key);
        return t;
    }

    public <T> T put(String key, Object value) {
        @SuppressWarnings("unchecked")
        T t = (T) cache.put(key, value);
        return t;
    }

    public void test() {
        put("five", 5);
        Integer five = put("five", 6);
        
        put("letter", "letter");
        String letter = put("letter", "word");
    }

}
