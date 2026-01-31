# 🍺 Brewery Runtime Architecture

### **Code‑Side Registry + Loader + Machine Wiring**

Below is the conceptual structure of the runtime system that consumes your JSON assets and wires everything together.

---

# 1. High‑Level Overview

Your mod needs **four cooperating registries**:

1. **AlcoholTypeRegistry**
   Loads `alcohol_types/*.json` and stores metadata + processing rules.

2. **LiquidRegistry**
   Loads `liquids/*.json` and defines intermediate liquids (mash, wort, wash…).

3. **MachineRegistry**
   Registers machine types (Fermenter, Distiller, etc.) and their capabilities.

4. **MachineRecipeRegistry**
   Loads `machine_recipes/*.json` and maps them to machines.

These registries are **pure data**.
Machines operate by querying them at runtime.

---

# 2. Registry Interfaces (Conceptual)

## 2.1 AlcoholTypeRegistry

```java
public interface AlcoholTypeRegistry {
    AlcoholType get(Identifier id);
    Collection<AlcoholType> all();
}
```

### AlcoholType object

```java
public record AlcoholType(
    Identifier id,
    String displayName,
    FlavorIntent flavorIntent,
    IngredientRules ingredients,
    ProcessingChain processing,
    MetadataRules metadataRules
) {}
```

---

## 2.2 LiquidRegistry

```java
public interface LiquidRegistry {
    LiquidDefinition get(Identifier id);
    Collection<LiquidDefinition> all();
}
```

### LiquidDefinition object

```java
public record LiquidDefinition(
    Identifier id,
    Phase phase,
    Identifier baseType,
    Metadata defaultMetadata
) {}
```

---

## 2.3 MachineRegistry

```java
public interface MachineRegistry {
    MachineDefinition get(Identifier id);
    Collection<MachineDefinition> all();
}
```

### MachineDefinition object

```java
public record MachineDefinition(
    Identifier id,
    Set<MachineStage> supportedStages
) {}
```

This is where you declare:

- crusher supports `mash`, `must`
- kettle supports `boil`
- fermenter supports `ferment`
- distiller supports `distill`
- barrel supports `age`
- infuser supports `infuse`
- bottler supports `bottle`

---

## 2.4 MachineRecipeRegistry

```java
public interface MachineRecipeRegistry {
    List<MachineRecipe> getRecipesForMachine(Identifier machineId);
}
```

### MachineRecipe object

```java
public record MachineRecipe(
    Identifier machine,
    List<RecipeInput> inputs,
    RecipeOutput output,
    int time,
    int energy
) {}
```

---

# 3. Loader Architecture

Your loader runs in three passes:

---

## **Pass 1 — Load Alcohol Types**

```java
public void loadAlcoholTypes(ResourceManager manager) {
    for (JsonFile file : manager.find("alcohol_types")) {
        AlcoholType type = AlcoholTypeParser.parse(file.json());
        AlcoholTypeRegistry.register(type.id(), type);
    }
}
```

This gives you:

- allowed fermentables
- allowed botanicals
- processing stages
- metadata rules

Machines don’t care about any of this yet.

---

## **Pass 2 — Load Liquids**

```java
public void loadLiquids(ResourceManager manager) {
    for (JsonFile file : manager.find("liquids")) {
        LiquidDefinition liquid = LiquidParser.parse(file.json());
        LiquidRegistry.register(liquid.id(), liquid);
    }
}
```

Liquids define:

- phase (mash, wort, wash…)
- default metadata
- base alcohol type (or generic)

---

## **Pass 3 — Load Machine Recipes**

```java
public void loadMachineRecipes(ResourceManager manager) {
    for (JsonFile file : manager.find("machine_recipes")) {
        MachineRecipe recipe = MachineRecipeParser.parse(file.json());
        MachineRecipeRegistry.register(recipe.machine(), recipe);
    }
}
```

Recipes define:

- what inputs a machine accepts
- what liquid it outputs
- metadata deltas

---

# 4. Machine Runtime Behavior

Each machine has a **generic runtime loop**:

```java
public void tick() {
    if (!hasValidInputs()) return;

    MachineRecipe recipe = findMatchingRecipe();
    if (recipe == null) return;

    LiquidStack output = applyRecipe(recipe);
    pushToOutputTank(output);
}
```

### `findMatchingRecipe()`

```java
private MachineRecipe findMatchingRecipe() {
    List<MachineRecipe> recipes = MachineRecipeRegistry.getRecipesForMachine(this.id);

    for (MachineRecipe recipe : recipes) {
        if (recipe.matches(inventory, tanks)) {
            return recipe;
        }
    }
    return null;
}
```

### `applyRecipe()`

```java
private LiquidStack applyRecipe(MachineRecipe recipe) {
    LiquidDefinition liquid = LiquidRegistry.get(recipe.output().liquid());
    Metadata metadata = liquid.defaultMetadata().copy();

    metadata.applyDelta(recipe.output().metadataDelta());

    return new LiquidStack(liquid.id(), recipe.output().amount(), metadata);
}
```

Machines don’t know:

- what beer is
- what gin is
- what botanicals do
- what strength means

They only apply metadata deltas.

---

# 5. Wiring Machines to Alcohol Types Automatically

This is the elegant part.

### When a machine receives a liquid:

```java
LiquidDefinition liquid = LiquidRegistry.get(inputLiquid.id());
AlcoholType type = AlcoholTypeRegistry.get(liquid.baseType());
```

Now the machine can:

- check if its stage is allowed
- apply metadata rules from the alcohol type

### Example: Fermenter

```java
if (type.processing().containsStage("ferment")) {
    metadata.strength += metadata.sugar * type.metadataRules().fermentFactor();
    metadata.clarity -= 0.1;
}
```

### Example: Distiller

```java
if (type.processing().containsStage("distill")) {
    metadata.strength += type.metadataRules().strengthPerDistill();
    metadata.clarity += 0.2;
}
```

### Example: Barrel

```java
if (type.processing().containsStage("age")) {
    metadata.strength += type.metadataRules().agingEffect();
    metadata.flavorProfile.merge(woodProfile);
}
```

This is how the system becomes **fully data‑driven**.

---

# 6. Automatic Machine Wiring

Machines register themselves like this:

```java
MachineRegistry.register(new MachineDefinition(
    id("fermenter"),
    Set.of(MachineStage.FERMENT)
));
```

Then the loader automatically knows:

- which recipes apply
- which alcohol types allow fermentation
- how metadata should change

No hardcoding.
No switch statements.
No per‑drink logic.

---

# 7. Putting It All Together

When the mod loads:

1. **Alcohol types** define flavor rules
2. **Liquids** define intermediate states
3. **Machine recipes** define transformations
4. **Machines** declare what stages they support
5. **Runtime** applies metadata rules dynamically

This gives you:

- A fully modular brewery
- Zero hardcoded drink logic
- Infinite extensibility
- Perfect alignment with your asset pipeline

---

If you want, I can now generate:

- **Actual Java/Kotlin/Fabric code stubs**
- **A full registry bootstrap class**
- **A runtime metadata engine**
- **A machine capability system**
- **A full example mod folder structure**

Just tell me how deep you want to go.
