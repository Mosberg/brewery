package dk.mosberg.data;

import java.util.List;
import java.util.Map;
import net.minecraft.util.Identifier;

public record MachineRecipe(Identifier machine, List<RecipeInput> inputs, RecipeOutput output,
        int time, int energy) {
    public record RecipeInput(String ingredient, int amount, String role) {
    }

    public record RecipeOutput(Identifier liquid, int amount, Map<String, Object> metadataDelta) {
    }
}
