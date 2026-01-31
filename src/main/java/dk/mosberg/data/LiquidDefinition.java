package dk.mosberg.data;

import net.minecraft.util.Identifier;

public record LiquidDefinition(Identifier id, Phase phase, Identifier baseType,
        Metadata defaultMetadata) {
}
