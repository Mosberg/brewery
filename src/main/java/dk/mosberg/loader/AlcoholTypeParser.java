package dk.mosberg.loader;

import java.io.InputStreamReader;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dk.mosberg.Brewery;
import dk.mosberg.data.AlcoholType;
import dk.mosberg.registry.AlcoholTypeRegistry;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public final class AlcoholTypeParser {
    private static final Gson GSON = new Gson();

    @SuppressWarnings("null")
    public static void loadAll(ResourceManager manager) {
        String path = "alcohol_types";
        Map<Identifier, Resource> resources =
                manager.findResources(path, id -> id.getPath().endsWith(".json"));

        for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
            try (var reader = new InputStreamReader(entry.getValue().getInputStream())) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                AlcoholType type = parse(entry.getKey(), json);
                AlcoholTypeRegistry.register(type);
            } catch (Exception e) {
                Brewery.LOGGER.error("Failed to load alcohol type {}", entry.getKey(), e);
            }
        }
    }

    private static AlcoholType parse(Identifier fileId, JsonObject json) {
        Identifier id = json.has("id") ? Identifier.tryParse(json.get("id").getAsString()) : fileId;
        String displayName = json.get("display_name").getAsString();
        String flavorIntent = json.get("flavor_intent").getAsString();

        // Parse IngredientRules
        JsonObject ingredientsObj = json.getAsJsonObject("ingredients");
        var fermentables =
                GSON.fromJson(ingredientsObj.getAsJsonArray("fermentables"), java.util.List.class);
        var botanicals =
                GSON.fromJson(ingredientsObj.getAsJsonArray("botanicals"), java.util.List.class);
        var modifiers =
                GSON.fromJson(ingredientsObj.getAsJsonArray("modifiers"), java.util.List.class);
        AlcoholType.IngredientRules ingredients =
                new AlcoholType.IngredientRules(fermentables, botanicals, modifiers);

        // Parse ProcessingChain
        JsonObject processingObj = json.getAsJsonObject("processing");
        var stages = GSON.fromJson(processingObj.getAsJsonArray("stages"), java.util.List.class);
        AlcoholType.ProcessingChain processing = new AlcoholType.ProcessingChain(stages);

        // Parse MetadataRules
        JsonObject metaObj = json.getAsJsonObject("metadata_rules");
        double baseStrength = metaObj.get("base_strength").getAsDouble();
        double strengthPerDistill = metaObj.get("strength_per_distill").getAsDouble();
        double agingEffect = metaObj.get("aging_effect").getAsDouble();
        Map<String, Double> clarityModifiers =
                GSON.fromJson(metaObj.getAsJsonObject("clarity_modifiers"), java.util.Map.class);
        Map<String, Integer> flavorProfile =
                GSON.fromJson(metaObj.getAsJsonObject("flavor_profile"), java.util.Map.class);
        AlcoholType.MetadataRules metadataRules = new AlcoholType.MetadataRules(baseStrength,
                strengthPerDistill, agingEffect, clarityModifiers, flavorProfile);

        return new AlcoholType(id, displayName, flavorIntent, ingredients, processing,
                metadataRules);
    }
}
