package dk.mosberg.data;

import java.util.List;
import java.util.Map;
import net.minecraft.util.Identifier;

public record AlcoholType(Identifier id, String displayName, String flavorIntent,
        IngredientRules ingredients, ProcessingChain processing, MetadataRules metadataRules) {
    public record IngredientRules(List<String> fermentables, List<String> botanicals,
            List<String> modifiers) {
    }

    public record ProcessingChain(List<String> stages) {
    }

    public record MetadataRules(double baseStrength, double strengthPerDistill, double agingEffect,
            Map<String, Double> clarityModifiers, Map<String, Integer> flavorProfile) {
    }
}
