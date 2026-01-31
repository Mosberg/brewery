package dk.mosberg.data;

import java.util.Set;
import net.minecraft.util.Identifier;

public record MachineDefinition(Identifier id, Set<MachineStage> supportedStages) {
}
