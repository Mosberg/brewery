package dk.mosberg.loader;

import java.io.InputStreamReader;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dk.mosberg.Brewery;
import dk.mosberg.data.LiquidDefinition;
import dk.mosberg.data.Metadata;
import dk.mosberg.data.Phase;
import dk.mosberg.registry.LiquidRegistry;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class LiquidParser {
    private static final Gson GSON = new Gson();

    @SuppressWarnings("null")
    public static void loadAll(ResourceManager manager) {
        String path = "liquids";
        Map<Identifier, Resource> resources =
                manager.findResources(path, id -> id.getPath().endsWith(".json"));

        for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
            try (var reader = new InputStreamReader(entry.getValue().getInputStream())) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                LiquidDefinition def = parse(entry.getKey(), json);
                LiquidRegistry.register(def);
            } catch (Exception e) {
                Brewery.LOGGER.error("Failed to load liquid {}", entry.getKey(), e);
            }
        }
    }

    private static LiquidDefinition parse(Identifier fileId, JsonObject json) {
        Identifier id = json.has("id") ? Identifier.tryParse(json.get("id").getAsString()) : fileId;
        Phase phase = Phase.valueOf(json.get("phase").getAsString().toUpperCase());
        Identifier baseType = Identifier.tryParse(json.get("base_type").getAsString());
        // For now, use empty metadata if not present
        Metadata metadata = new Metadata();
        return new LiquidDefinition(id, phase, baseType, metadata);
    }
}
