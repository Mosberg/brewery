package dk.mosberg.registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import dk.mosberg.data.MachineDefinition;
import net.minecraft.util.Identifier;

public final class MachineRegistry {
    private static final Map<Identifier, MachineDefinition> MACHINES = new HashMap<>();

    public static void register(MachineDefinition machine) {
        MACHINES.put(machine.id(), machine);
    }

    public static MachineDefinition get(Identifier id) {
        return MACHINES.get(id);
    }

    public static Collection<MachineDefinition> all() {
        return MACHINES.values();
    }

    public static void clear() {
        MACHINES.clear();
    }
}
