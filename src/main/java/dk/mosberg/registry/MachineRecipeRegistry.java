package dk.mosberg.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import dk.mosberg.data.MachineRecipe;
import net.minecraft.util.Identifier;

public final class MachineRecipeRegistry {
    private static final Map<Identifier, List<MachineRecipe>> RECIPES = new HashMap<>();

    public static void register(MachineRecipe recipe) {
        RECIPES.computeIfAbsent(recipe.machine(), id -> new ArrayList<>()).add(recipe);
    }

    public static List<MachineRecipe> getRecipesForMachine(Identifier machineId) {
        return RECIPES.getOrDefault(machineId, List.of());
    }

    public static void clear() {
        RECIPES.clear();
    }
}
