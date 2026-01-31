package dk.mosberg.loader;

import java.io.InputStreamReader;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dk.mosberg.Brewery;
import dk.mosberg.data.MachineRecipe;
import dk.mosberg.registry.MachineRecipeRegistry;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class MachineRecipeParser {
    private static final Gson GSON = new Gson();

    @SuppressWarnings("null")
    public static void loadAll(ResourceManager manager) {
        String path = "machine_recipes";
        Map<Identifier, Resource> resources =
                manager.findResources(path, id -> id.getPath().endsWith(".json"));

        for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
            try (var reader = new InputStreamReader(entry.getValue().getInputStream())) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                MachineRecipe recipe = parse(entry.getKey(), json);
                MachineRecipeRegistry.register(recipe);
            } catch (Exception e) {
                Brewery.LOGGER.error("Failed to load machine recipe {}", entry.getKey(), e);
            }
        }
    }

    private static MachineRecipe parse(Identifier fileId, JsonObject json) {
        // For brevity, stub with minimal fields
        Identifier machine = Identifier.tryParse(json.get("machine").getAsString());
        MachineRecipe.RecipeOutput output = new MachineRecipe.RecipeOutput(
                Identifier.tryParse("minecraft:water"), 1, java.util.Map.of());
        return new MachineRecipe(machine, java.util.List.of(), output, 100, 0);
    }
}
