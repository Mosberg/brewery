package dk.mosberg.registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import dk.mosberg.data.AlcoholType;
import net.minecraft.util.Identifier;

public final class AlcoholTypeRegistry {
    private static final Map<Identifier, AlcoholType> TYPES = new HashMap<>();

    public static void register(AlcoholType type) {
        TYPES.put(type.id(), type);
    }

    public static AlcoholType get(Identifier id) {
        return TYPES.get(id);
    }

    public static Collection<AlcoholType> all() {
        return TYPES.values();
    }

    public static void clear() {
        TYPES.clear();
    }
}
