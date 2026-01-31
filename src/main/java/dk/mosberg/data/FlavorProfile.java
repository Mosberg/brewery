package dk.mosberg.data;

import java.util.HashMap;
import java.util.Map;

public class FlavorProfile {
    private final Map<String, Integer> profile = new HashMap<>();

    public void set(String flavor, int value) {
        profile.put(flavor, value);
    }

    public int get(String flavor) {
        return profile.getOrDefault(flavor, 0);
    }

    public Map<String, Integer> asMap() {
        return profile;
    }

    public void putAll(Map<String, Integer> map) {
        profile.putAll(map);
    }

    public FlavorProfile copy() {
        FlavorProfile copy = new FlavorProfile();
        copy.putAll(this.profile);
        return copy;
    }
}
