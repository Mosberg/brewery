# 1. Example mod folder structure

```text
src/main/java/
  com/example/brewery/
    Brewery.java

    registry/
      AlcoholTypeRegistry.java
      LiquidRegistry.java
      MachineRegistry.java
      MachineRecipeRegistry.java

    data/
      AlcoholType.java
      LiquidDefinition.java
      MachineDefinition.java
      MachineRecipe.java
      Metadata.java
      FlavorProfile.java
      MachineStage.java
      Phase.java

    loader/
      BreweryDataLoader.java
      AlcoholTypeParser.java
      LiquidParser.java
      MachineRecipeParser.java

    machine/
      base/
        AbstractMachineBlockEntity.java
        MachineCapability.java
      impl/
        FermenterBlockEntity.java
        DistillerBlockEntity.java
        BarrelBlockEntity.java
        InfuserBlockEntity.java
        BottlerBlockEntity.java

    runtime/
      MetadataEngine.java
      LiquidStack.java

src/main/resources/
  fabric.mod.json
  data/brewery/registry.json
  data/brewery/alcohol_types/*.json
  data/brewery/liquids/*.json
  data/brewery/machine_recipes/*.json
  assets/brewery/models/...
  assets/brewery/lang/en_us.json
```

---

### 2. Core mod entrypoint

```java
package dk.mosberg;

import dk.mosberg.loader.BreweryDataLoader;
import net.fabricmc.api.ModInitializer;

public class Brewery implements ModInitializer {
    public static final String MOD_ID = "brewery";

    @Override
    public void onInitialize() {
        BreweryDataLoader.register();
    }
}
```

---

### 3. Registry classes

#### 3.1 AlcoholTypeRegistry

```java
package dk.mosberg.registry;

import dk.mosberg.data.AlcoholType;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class AlcoholTypeRegistry {
    private static final Map<Identifier, AlcoholType> TYPES = new HashMap<>();

    public static void register(AlcoholType type) {
        TYPES.put(type.id(), type);
    }

    public static AlcoholType get(Identifier id) {
        return TYPES.get(id);
    }

    public static Collection<AlcoholType> all() {
        return TYPES.values();
    }

    public static void clear() {
        TYPES.clear();
    }
}
```

#### 3.2 LiquidRegistry

```java
package dk.mosberg.registry;

import dk.mosberg.data.LiquidDefinition;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class LiquidRegistry {
    private static final Map<Identifier, LiquidDefinition> LIQUIDS = new HashMap<>();

    public static void register(LiquidDefinition liquid) {
        LIQUIDS.put(liquid.id(), liquid);
    }

    public static LiquidDefinition get(Identifier id) {
        return LIQUIDS.get(id);
    }

    public static Collection<LiquidDefinition> all() {
        return LIQUIDS.values();
    }

    public static void clear() {
        LIQUIDS.clear();
    }
}
```

#### 3.3 MachineRegistry

```java
package dk.mosberg.registry;

import dk.mosberg.data.MachineDefinition;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class MachineRegistry {
    private static final Map<Identifier, MachineDefinition> MACHINES = new HashMap<>();

    public static void register(MachineDefinition machine) {
        MACHINES.put(machine.id(), machine);
    }

    public static MachineDefinition get(Identifier id) {
        return MACHINES.get(id);
    }

    public static Collection<MachineDefinition> all() {
        return MACHINES.values();
    }

    public static void clear() {
        MACHINES.clear();
    }
}
```

#### 3.4 MachineRecipeRegistry

```java
package dk.mosberg.registry;

import dk.mosberg.data.MachineRecipe;
import net.minecraft.util.Identifier;

import java.util.*;

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
```

---

### 4. Data records

#### 4.1 AlcoholType

```java
package dk.mosberg.data;

import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

public record AlcoholType(
    Identifier id,
    String displayName,
    String flavorIntent,
    IngredientRules ingredients,
    ProcessingChain processing,
    MetadataRules metadataRules
) {
    public record IngredientRules(
        List<String> fermentables,
        List<String> botanicals,
        List<String> modifiers
    ) {}

    public record ProcessingChain(
        List<String> stages
    ) {}

    public record MetadataRules(
        double baseStrength,
        double strengthPerDistill,
        double agingEffect,
        Map<String, Double> clarityModifiers,
        Map<String, Integer> flavorProfile
    ) {}
}
```

#### 4.2 LiquidDefinition

```java
package dk.mosberg.data;

import net.minecraft.util.Identifier;

public record LiquidDefinition(
    Identifier id,
    Phase phase,
    Identifier baseType,
    Metadata defaultMetadata
) {}
```

#### 4.3 MachineDefinition

```java
package dk.mosberg.data;

import dk.mosberg.data.MachineStage;
import net.minecraft.util.Identifier;

import java.util.Set;

public record MachineDefinition(
    Identifier id,
    Set<MachineStage> supportedStages
) {}
```

#### 4.4 MachineRecipe

```java
package dk.mosberg.data;

import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

public record MachineRecipe(
    Identifier machine,
    List<RecipeInput> inputs,
    RecipeOutput output,
    int time,
    int energy
) {
    public record RecipeInput(
        String ingredient,
        int amount,
        String role
    ) {}

    public record RecipeOutput(
        Identifier liquid,
        int amount,
        Map<String, Object> metadataDelta
    ) {}
}
```

#### 4.5 Metadata & enums

```java
package dk.mosberg.data;

import java.util.HashMap;
import java.util.Map;

public final class Metadata {
    public double sugar;
    public double strength;
    public double clarity;
    public String color;
    public Map<String, Integer> flavorProfile = new HashMap<>();

    public Metadata copy() {
        Metadata m = new Metadata();
        m.sugar = this.sugar;
        m.strength = this.strength;
        m.clarity = this.clarity;
        m.color = this.color;
        m.flavorProfile.putAll(this.flavorProfile);
        return m;
    }
}
```

```java
package dk.mosberg.data;

public enum Phase {
    MASH, MUST, WORT, WASH, YOUNG_ALCOHOL, SPIRIT, AGED, FLAVORED
}
```

```java
package dk.mosberg.data;

public enum MachineStage {
    MASH, PRESS, BOIL, FERMENT, DISTILL, AGE, INFUSE, CONDITION
}
```

---

### 5. Data loader & parsers

#### 5.1 Data loader bootstrap

```java
package dk.mosberg.loader;

import dk.mosberg.Brewery;
import dk.mosberg.registry.*;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public final class BreweryDataLoader implements SimpleSynchronousResourceReloadListener {

    public static void register() {
        net.fabricmc.fabric.api.resource.ResourceManagerHelper
            .get(net.minecraft.resource.ResourceType.SERVER_DATA)
            .registerReloadListener(new BreweryDataLoader());
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(Brewery.MOD_ID, "data_loader");
    }

    @Override
    public void reload(ResourceManager manager) {
        AlcoholTypeRegistry.clear();
        LiquidRegistry.clear();
        MachineRecipeRegistry.clear();

        AlcoholTypeParser.loadAll(manager);
        LiquidParser.loadAll(manager);
        MachineRecipeParser.loadAll(manager);
    }
}
```

#### 5.2 Example parser skeleton

```java
package dk.mosberg.loader;

import dk.mosberg.Brewery;
import dk.mosberg.data.AlcoholType;
import dk.mosberg.registry.AlcoholTypeRegistry;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.util.Map;

public final class AlcoholTypeParser {
    private static final Gson GSON = new Gson();

    public static void loadAll(ResourceManager manager) {
        String path = "alcohol_types";
        Map<Identifier, Resource> resources = manager.findResources(path, id -> id.getPath().endsWith(".json"));

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
        Identifier id = json.has("id") ? new Identifier(json.get("id").getAsString()) : fileId;

        String displayName = json.get("display_name").getAsString();
        String flavorIntent = json.get("flavor_intent").getAsString();

        // Parse nested objects (ingredients, processing, metadata_rules) similarly.
        // Stubbed here:
        AlcoholType.IngredientRules ingredients = new AlcoholType.IngredientRules(
            java.util.List.of(), java.util.List.of(), java.util.List.of()
        );
        AlcoholType.ProcessingChain processing = new AlcoholType.ProcessingChain(
            java.util.List.of()
        );
        AlcoholType.MetadataRules metadataRules = new AlcoholType.MetadataRules(
            0.0, 0.0, 0.0, java.util.Map.of(), java.util.Map.of()
        );

        return new AlcoholType(id, displayName, flavorIntent, ingredients, processing, metadataRules);
    }
}
```

You’d mirror this for `LiquidParser` and `MachineRecipeParser`.

---

### 6. Runtime metadata engine

```java
package dk.mosberg.runtime;

import dk.mosberg.data.AlcoholType;
import dk.mosberg.data.LiquidDefinition;
import dk.mosberg.data.Metadata;
import dk.mosberg.registry.AlcoholTypeRegistry;
import dk.mosberg.registry.LiquidRegistry;
import net.minecraft.util.Identifier;

import java.util.Map;

public final class MetadataEngine {

    public static Metadata applyStage(
        Identifier liquidId,
        Metadata metadata,
        String stage,
        Map<String, Object> recipeDelta
    ) {
        LiquidDefinition liquid = LiquidRegistry.get(liquidId);
        if (liquid == null) return metadata;

        AlcoholType type = AlcoholTypeRegistry.get(liquid.baseType());
        if (type == null) return metadata;

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

    private static void applyDelta(Metadata metadata, Map<String, Object> delta) {
        if (delta == null) return;
        // You can make this more robust; this is a simple sketch.
        if (delta.containsKey("sugar")) metadata.sugar += ((Number) delta.get("sugar")).doubleValue();
        if (delta.containsKey("strength")) metadata.strength += ((Number) delta.get("strength")).doubleValue();
        if (delta.containsKey("clarity")) metadata.clarity += ((Number) delta.get("clarity")).doubleValue();
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

    private static void applyInfuse(AlcoholType type, Metadata metadata) {
        // Flavor profile merging would go here.
    }
}
```

---

### 7. Machine capability system

#### 7.1 Capability interface

```java
package dk.mosberg.machine.base;

import dk.mosberg.data.MachineStage;
import dk.mosberg.runtime.LiquidStack;

public interface MachineCapability {
    MachineStage stage();
    boolean canProcess(LiquidStack input);
    LiquidStack process(LiquidStack input);
}
```

#### 7.2 Abstract machine block entity

```java
package dk.mosberg.machine.base;

import dk.mosberg.data.MachineStage;
import dk.mosberg.data.MachineRecipe;
import dk.mosberg.registry.MachineRecipeRegistry;
import dk.mosberg.runtime.LiquidStack;
import dk.mosberg.runtime.MetadataEngine;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;

import java.util.List;
import java.util.Map;

public abstract class AbstractMachineBlockEntity extends BlockEntity {

    protected LiquidStack inputTank;
    protected LiquidStack outputTank;

    protected AbstractMachineBlockEntity(Identifier typeId, BlockPos pos, BlockState state) {
        super(null, pos, state); // replace null with your BlockEntityType
    }

    protected abstract Identifier machineId();
    protected abstract MachineStage stage();

    public void tickServer() {
        if (inputTank == null || inputTank.isEmpty()) return;

        List<MachineRecipe> recipes = MachineRecipeRegistry.getRecipesForMachine(machineId());
        for (MachineRecipe recipe : recipes) {
            if (matches(recipe)) {
                process(recipe);
                break;
            }
        }
    }

    protected boolean matches(MachineRecipe recipe) {
        // For now, assume any recipe for this machine matches if we have a liquid.
        return true;
    }

    protected void process(MachineRecipe recipe) {
        var outputDef = recipe.output();
        LiquidStack out = new LiquidStack(
            outputDef.liquid(),
            outputDef.amount(),
            inputTank.metadata().copy()
        );

        MetadataEngine.applyStage(
            out.liquidId(),
            out.metadata(),
            stage().name().toLowerCase(),
            outputDef.metadataDelta()
        );

        this.outputTank = out;
        this.inputTank = null;
    }
}
```

#### 7.3 Example: Fermenter

```java
package dk.mosberg.machine.impl;

import dk.mosberg.Brewery;
import dk.mosberg.data.MachineStage;
import dk.mosberg.machine.base.AbstractMachineBlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;

public class FermenterBlockEntity extends AbstractMachineBlockEntity {

    public FermenterBlockEntity(BlockPos pos, BlockState state) {
        super(new Identifier(Brewery.MOD_ID, "fermenter"), pos, state);
    }

    @Override
    protected Identifier machineId() {
        return new Identifier(Brewery.MOD_ID, "fermenter");
    }

    @Override
    protected MachineStage stage() {
        return MachineStage.FERMENT;
    }
}
```

You’d mirror this for Distiller, Barrel, Infuser, Bottler, etc.

---
