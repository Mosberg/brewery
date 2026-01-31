package dk.mosberg.registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import dk.mosberg.data.LiquidDefinition;
import net.minecraft.util.Identifier;

public final class LiquidRegistry {
    private static final Map<Identifier, LiquidDefinition> LIQUIDS = new HashMap<>();

    public static void register(LiquidDefinition liquid) {
        LIQUIDS.put(liquid.id(), liquid);
    }

    public static LiquidDefinition get(Identifier id) {
        return LIQUIDS.get(id);
    }

    public static Collection<LiquidDefinition> all() {
        return LIQUIDS.values();
    }

    public static void clear() {
        LIQUIDS.clear();
    }
}
