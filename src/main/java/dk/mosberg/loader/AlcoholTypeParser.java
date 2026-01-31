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
        // You can wire this to your JSON schema or map fields manually.
        // For brevity, assume fields exist and are valid.
        // id: use json "id" if present, else derive from fileId.
        Identifier id = json.has("id") ? Identifier.tryParse(json.get("id").getAsString()) : fileId;

        String displayName = json.get("display_name").getAsString();
        String flavorIntent = json.get("flavor_intent").getAsString();

        // Parse nested objects (ingredients, processing, metadata_rules) similarly.
        // Stubbed here:
        AlcoholType.IngredientRules ingredients = new AlcoholType.IngredientRules(
                java.util.List.of(), java.util.List.of(), java.util.List.of());
        AlcoholType.ProcessingChain processing =
                new AlcoholType.ProcessingChain(java.util.List.of());
        AlcoholType.MetadataRules metadataRules = new AlcoholType.MetadataRules(0.0, 0.0, 0.0,
                java.util.Map.of(), java.util.Map.of());

        return new AlcoholType(id, displayName, flavorIntent, ingredients, processing,
                metadataRules);
    }
}
