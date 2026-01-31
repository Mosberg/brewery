# Brewery

A Minecraft mod that introduces a **machine‑driven brewery system** for crafting custom alcoholic beverages using **vanilla ingredients**, **generic processing stages**, and **metadata‑based drink definitions**.
Brewery focuses on **infrastructure**, **automation**, and **extensibility**, not on adding new crops or items.

```properties
# --------------------------------------------------------------
# Gradle Properties for Brewery Mod
# --------------------------------------------------------------

# --------------------------------------------------------------
# Gradle JVM Configuration - Optimized for Fabric Development
# --------------------------------------------------------------

org.gradle.jvmargs=-Xmx4G -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configuration-cache=true

# --------------------------------------------------------------
# Mod Metadata and Version Configuration
# --------------------------------------------------------------

maven_group=dk.mosberg
archives_base_name=brewery

mod_id=brewery
mod_version=1.0.0
mod_name=Brewery
mod_description=Brewery
mod_author=Mosberg
mod_homepage=https://mosberg.github.io/Brewery
mod_sources=https://github.com/mosberg/Brewery
mod_issues=https://github.com/mosberg/Brewery/issues
mod_license=MIT

### Entrypoints (fully-qualified class names)
mod_entrypoint_main=dk.mosberg.Brewery
mod_entrypoint_client=dk.mosberg.client.BreweryClient

# --------------------------------------------------------------
# Minecraft & Fabric Versions
# --------------------------------------------------------------

minecraft_version=1.21.11
yarn_mappings=1.21.11+build.4
loader_version=0.18.4
loom_version=1.15-SNAPSHOT
fabric_version=0.141.2+1.21.11
java_version=21

# --------------------------------------------------------------
# Library Versions
# --------------------------------------------------------------

gson_version=2.13.2
slf4j_version=2.0.17
annotations_version=26.0.2

# --------------------------------------------------------------
# Testing Framework Versions
# --------------------------------------------------------------

junit_version=6.0.2

# gradle_version=9.3.0

```

---

## Design goals

- **Vanilla‑only ingredients**
  All fermentables, botanicals, and modifiers come from existing Minecraft items. Brewery adds **machines**, **processing rules**, and **data‑driven drink definitions**, not new crops.

- **Machine‑chain production**
  Alcohol is created through a modular chain:
  **Processing → Brewing → Fermentation → Distillation → Aging → Infusion → Bottling**
  Each machine produces **generic intermediate liquids** (mash, wort, must, wash, young alcohol) to avoid combinatorial explosion.

- **Scalable, data‑driven content**
  Alcohol types, ingredient mappings, and flavor rules are defined through **data assets** and **schema‑driven templates**, not code.

- **Future‑ready architecture**
  Clear separation of common vs client logic, resource scanning for ingredients, and metadata‑based drink identity ensure long‑term extensibility.

---

## Ingredient system

Brewery organizes vanilla items into **functional categories** used by machines and drink definitions.
These categories are **tags**, not hardcoded lists.

### Fermentable bases

Items that can be converted into **mash / must / wort / wash**.

- Apple, Sweet Berries, Glow Berries, Melon Slice
- Honey Bottle
- Potato, Beetroot, Carrot
- Chorus Fruit
- Dried Kelp

### Seeds / starters

Used as **grain proxies** or **yeast/starters** in early brewing stages.

- Wheat Seeds, Beetroot Seeds
- Pumpkin Seeds, Melon Seeds
- Torchflower Seeds, Pitcher Pod

### Brewing catalysts / modifiers

Affect **fermentation speed**, **strength**, **clarity**, or **aging**.

- Nether Wart
- Redstone Dust
- Glowstone Dust
- Fermented Spider Eye
- Gunpowder
- Dragon’s Breath

### Effect ingredients (optional)

Add **potion‑like effects** or influence **quality**.

- Sugar, Golden Carrot, Glistering Melon Slice
- Spider Eye, Blaze Powder, Ghast Tear
- Magma Cream, Rabbit’s Foot, Phantom Membrane
- Turtle Shell, Pufferfish

### Botanicals / flavor layer

Used during **infusion** or **post‑distillation flavoring**.

- Leaves (all types)
- Small flowers
- Tall flowers
- Curated plant‑like ingredients

---

## Brewery ingredient tags

These tags drive the machine chain and drink definitions:

- `brewery:alcohol_types/*`
  absinthe, ale, beer, brandy, cider, gin, lager, mead, rum, stout, vodka, whiskey, wine

- `brewery:fermentables`
  All fruit/vegetable/honey/sugar bases

- `brewery:seeds`
  Grain/starters

- `brewery:catalysts`
  Brewing modifiers

- `brewery:effect_ingredients`
  Optional effect‑layer items

- `brewery:botanicals`
  Leaves + flowers + curated plants

These tags allow machines to operate generically and allow new alcohol types to be added without code changes.

---

## Alcohol types

Each alcohol type is defined by a **data asset** describing:

- **Flavor intent**
- **Valid fermentable bases**
- **Allowed botanicals**
- **Optional modifiers**
- **Processing requirements** (ferment → distill → age → infuse)
- **Metadata rules** (strength, clarity, color, flavor profile)

The following entries describe **design intent**, not hardcoded recipes.
Machines interpret these definitions to produce the final drink.

---

# Alcohol Type Definitions

_(Rewritten to match the machine‑chain vision)_

Each section below now describes:

- **Base → Fermentation → Distillation → Aging → Infusion**
- **Generic machine stages**
- **Metadata‑driven outcomes**

---

### Absinthe

**Intent:** Herbal, aromatic, bitter.
**Base:** Honey Bottle or Sweet Berries → **wash**
**Process:**

- Ferment wash → **young alcohol**
- Distill → **neutral spirit**
- Infuse botanicals (Dandelion, Wither Rose, Leaves)
- Optional short aging
  **Modifiers:** Sugar (sweetness), Glowstone Dust (potency)

---

### Ale

**Intent:** Malt‑forward, lightly hopped.
**Base:** Wheat Seeds (+ optional Potato) → **grain mash**
**Process:**

- Mash → wort
- Boil with small flowers (hop proxy)
- Ferment → young ale
- Condition briefly
  **Modifiers:** Redstone (longer conditioning), Glowstone (strength)

---

### Beer

**Intent:** General beer profile.
**Base:** Wheat Seeds → **wort**
**Process:**

- Mash → wort
- Boil with Torchflower Seeds or small flowers
- Ferment → young beer
- Condition
  **Modifiers:** Redstone, Glowstone

---

### Brandy

**Intent:** Distilled fruit spirit.
**Base:** Apple or Melon Slice → **must**
**Process:**

- Ferment → fruit wine
- Distill → brandy new‑make
- Barrel age
  **Modifiers:** Glowstone (strength), Redstone (aging)

---

### Cider

**Intent:** Fermented apple beverage.
**Base:** Apple → **must**
**Process:**

- Press/steep → must
- Ferment → cider
- Condition
  **Modifiers:** Redstone (dry/aged cider)

---

### Gin

**Intent:** Neutral spirit + botanical infusion.
**Base:** Potato (neutral) or Carrot → **wash**
**Process:**

- Ferment → neutral young alcohol
- Distill → clean spirit
- Infuse botanicals (Spruce Leaves, Blue Orchid, Allium)
  **Modifiers:** Blaze Powder (dryness), Sugar (balance)

---

### Lager

**Intent:** Clean, crisp, cold‑conditioned beer.
**Base:** Wheat Seeds → **wort**
**Process:**

- Mash → wort
- Light boil
- Ferment
- Extended conditioning (lagering)
  **Modifiers:** Redstone (longer conditioning)

---

### Mead

**Intent:** Honey wine.
**Base:** Honey Bottle → **must**
**Process:**

- Dilute → must
- Ferment → mead
- Optional aging
  **Botanicals:** Allium, Dandelion, Spore Blossom
  **Modifiers:** Glowstone, Redstone

---

### Rum

**Intent:** Sugar‑derived spirit.
**Base:** Sugar + Potato or Sweet Berries → **wash**
**Process:**

- Ferment → sugar wash
- Distill → rum
- Barrel age
  **Modifiers:** Blaze Powder, Redstone

---

### Stout

**Intent:** Dark, rich beer.
**Base:** Wheat Seeds + Beetroot → **dark wort**
**Process:**

- Mash → dark wort
- Boil with Wither Rose / Dark Oak Leaves
- Ferment
- Condition
  **Modifiers:** Glowstone (imperial), Redstone (conditioning)

---

### Vodka

**Intent:** Neutral, clean spirit.
**Base:** Potato → **wash**
**Process:**

- Ferment → young alcohol
- Distill (multiple passes allowed)
- No aging
  **Modifiers:** Glowstone (proof), Dragon’s Breath (premium batch)

---

### Whiskey

**Intent:** Grain spirit + barrel aging.
**Base:** Wheat Seeds → **grain wash**
**Process:**

- Ferment → wash
- Distill → whiskey new‑make
- Barrel age (critical)
  **Modifiers:** Redstone, Glowstone

---

### Wine

**Intent:** Fermented fruit beverage.
**Base:** Sweet Berries (red), Glow Berries (white), Melon Slice (light) → **must**
**Process:**

- Ferment → wine
- Condition/age
  **Botanicals:** Rose Bush, Lilac
  **Modifiers:** Redstone, Sugar

---
