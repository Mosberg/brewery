package dk.mosberg.runtime;

import java.util.Map;
import dk.mosberg.data.AlcoholType;
import dk.mosberg.data.LiquidDefinition;
import dk.mosberg.data.Metadata;
import dk.mosberg.registry.AlcoholTypeRegistry;
import dk.mosberg.registry.LiquidRegistry;
import net.minecraft.util.Identifier;

public final class MetadataEngine {

    public static Metadata applyStage(Identifier liquidId, Metadata metadata, String stage,
            Map<String, Object> recipeDelta) {
        LiquidDefinition liquid = LiquidRegistry.get(liquidId);
        if (liquid == null)
            return metadata;

        AlcoholType type = AlcoholTypeRegistry.get(liquid.baseType());
        if (type == null)
            return metadata;

        // Apply recipe delta first
        applyDelta(metadata, recipeDelta);

        // Then apply alcohol-type-specific rules
        switch (stage) {
            case "ferment" -> applyFerment(type, metadata);
            case "distill" -> applyDistill(type, metadata);
            case "age" -> applyAge(type, metadata);
            case "condition" -> applyCondition(type, metadata);
            case "infuse" -> applyInfuse(type, metadata);
        }

        return metadata;
    }

    @SuppressWarnings("null")
    private static void applyDelta(Metadata metadata, Map<String, Object> delta) {
        if (delta == null)
            return;
        if (delta.containsKey("sugar"))
            metadata.sugar += ((Number) delta.get("sugar")).doubleValue();
        if (delta.containsKey("strength"))
            metadata.strength += ((Number) delta.get("strength")).doubleValue();
        if (delta.containsKey("clarity"))
            metadata.clarity += ((Number) delta.get("clarity")).doubleValue();
        if (delta.containsKey("color"))
            metadata.color = (String) delta.get("color");
        if (delta.containsKey("flavorProfile")) {
            Object fpObj = delta.get("flavorProfile");
            if (fpObj instanceof Map<?, ?> fpMap) {
                for (var entry : fpMap.entrySet()) {
                    String key = entry.getKey().toString();
                    int value = ((Number) entry.getValue()).intValue();
                    metadata.flavorProfile.merge(key, value, Integer::sum);
                }
            }
        }
    }

    private static void applyFerment(AlcoholType type, Metadata metadata) {
        double factor = type.metadataRules().baseStrength();
        metadata.strength += metadata.sugar * factor;
        metadata.sugar = 0;
        metadata.clarity -= 0.1;
    }

    private static void applyDistill(AlcoholType type, Metadata metadata) {
        metadata.strength += type.metadataRules().strengthPerDistill();
        metadata.clarity += 0.2;
    }

    private static void applyAge(AlcoholType type, Metadata metadata) {
        metadata.strength += type.metadataRules().agingEffect();
    }

    private static void applyCondition(AlcoholType type, Metadata metadata) {
        metadata.clarity += 0.1;
    }

    @SuppressWarnings("null")
    private static void applyInfuse(AlcoholType type, Metadata metadata) {
        // Merge flavor profile from AlcoholType's metadataRules into the metadata's flavorProfile
        var flavorProfile = type.metadataRules().flavorProfile();
        if (flavorProfile != null) {
            for (var entry : flavorProfile.entrySet()) {
                metadata.flavorProfile.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }
    }
}
