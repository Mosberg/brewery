package dk.mosberg.data;

import java.util.HashMap;
import java.util.Map;

public final class Metadata {
    public double sugar;
    public double strength;
    public double clarity;
    public String color;
    public Map<String, Integer> flavorProfile = new HashMap<>();

    public Metadata copy() {
        Metadata m = new Metadata();
        m.sugar = this.sugar;
        m.strength = this.strength;
        m.clarity = this.clarity;
        m.color = this.color;
        m.flavorProfile.putAll(this.flavorProfile);
        return m;
    }
}
