# 1. JSON Schema: `alcohol_type.schema.json`

This schema defines **one alcohol family** (beer, wine, gin, etc.).
It does **not** define recipes — it defines **rules** that machines interpret.

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "Brewery Alcohol Type Definition",
  "type": "object",

  "required": ["id", "flavor_intent", "processing", "ingredients"],

  "properties": {
    "id": {
      "type": "string",
      "description": "Unique identifier, e.g. brewery:beer"
    },

    "display_name": {
      "type": "string",
      "description": "Human-readable name"
    },

    "flavor_intent": {
      "type": "string",
      "description": "High-level description of the drink's character"
    },

    "ingredients": {
      "type": "object",
      "required": ["fermentables"],
      "properties": {
        "fermentables": {
          "type": "array",
          "items": { "type": "string" },
          "description": "Vanilla items or tags usable as base sugars"
        },
        "botanicals": {
          "type": "array",
          "items": { "type": "string" },
          "description": "Optional flavoring ingredients"
        },
        "modifiers": {
          "type": "array",
          "items": { "type": "string" },
          "description": "Optional catalysts affecting strength, clarity, duration"
        }
      }
    },

    "processing": {
      "type": "object",
      "required": ["stages"],
      "properties": {
        "stages": {
          "type": "array",
          "description": "Ordered list of required machine stages",
          "items": {
            "type": "string",
            "enum": ["mash", "press", "boil", "ferment", "distill", "age", "infuse", "condition"]
          }
        },

        "metadata_rules": {
          "type": "object",
          "description": "How machines should adjust metadata",
          "properties": {
            "base_strength": { "type": "number" },
            "strength_per_distill": { "type": "number" },
            "aging_effect": { "type": "number" },
            "clarity_modifiers": {
              "type": "object",
              "additionalProperties": { "type": "number" }
            },
            "flavor_profile": {
              "type": "object",
              "additionalProperties": { "type": "number" }
            }
          }
        }
      }
    }
  }
}
```

This schema ensures:

- Every alcohol type is **data‑driven**
- Machines interpret the **processing stages**
- Metadata (strength, clarity, flavor) is computed generically
- Adding new alcohol types requires **zero code**

---

# 2. Machine Behavior Specifications

_(Generic, metadata‑driven, no hardcoded drink logic)_

Each machine consumes **generic inputs** and produces **generic outputs**.
Alcohol identity is stored in **metadata**, not item IDs.

---

## **2.1 Mill / Crusher**

**Input:** fermentable solid
**Output:** `mash` (grain) or `must` (fruit)
**Rules:**

- If input ∈ `brewery:fermentables/grain` → produce `mash`
- If input ∈ `brewery:fermentables/fruit` → produce `must`
- Metadata: `{ sugar: base_value }`

---

## **2.2 Press**

**Input:** fruit fermentables
**Output:** `must`
**Rules:**

- Higher sugar yield than Crusher
- Metadata: `{ sugar: base_value * 1.2 }`

---

## **2.3 Mash Tun**

**Input:** mash + water
**Output:** `wort`
**Rules:**

- Converts grain mash → wort
- Metadata: `{ sugar: mash.sugar * 0.9 }`

---

## **2.4 Boiling Kettle**

**Input:** wort + botanicals
**Output:** `hopped_wort`
**Rules:**

- Adds bitterness/aroma from botanicals
- Metadata: `{ bitterness: +botanical_value }`

---

## **2.5 Fermenter**

**Input:** any sugary liquid (must, wort, wash)
**Output:** `young_alcohol`
**Rules:**

- Converts sugar → alcohol
- Metadata:
  - `strength = sugar * ferment_factor`
  - `clarity = base - turbidity`
  - `flavor_profile += botanicals`

---

## **2.6 Distiller**

**Input:** young_alcohol
**Output:** `spirit`
**Rules:**

- `strength += alcohol_type.metadata_rules.strength_per_distill`
- `clarity += 1`
- `flavor_profile *= 0.5` (cleaner)

---

## **2.7 Barrel / Aging Machine**

**Input:** spirit or wine
**Output:** aged variant
**Rules:**

- `strength += aging_effect`
- `flavor_profile += wood_profile`
- `color += wood_color`

---

## **2.8 Infuser**

**Input:** alcohol + botanicals
**Output:** flavored alcohol
**Rules:**

- `flavor_profile += botanical_profile`
- `clarity -= infusion_haze`

---

## **2.9 Bottler**

**Input:** any alcohol liquid
**Output:** bottled drink
**Rules:**

- Finalizes metadata
- Applies alcohol type ID
- Generates tooltip from metadata

---

# 3. Single‑Source‑of‑Truth Drink Definition Format

_(This is the file modders create for each alcohol type)_

Example: `brewery:gin.json`

```json
{
  "id": "brewery:gin",
  "display_name": "Gin",

  "flavor_intent": "Neutral spirit infused with juniper-like botanicals",

  "ingredients": {
    "fermentables": ["minecraft:potato", "minecraft:carrot"],
    "botanicals": ["minecraft:spruce_leaves", "minecraft:blue_orchid", "minecraft:allium"],
    "modifiers": ["minecraft:blaze_powder", "minecraft:sugar"]
  },

  "processing": {
    "stages": ["mash", "ferment", "distill", "infuse"],

    "metadata_rules": {
      "base_strength": 2.0,
      "strength_per_distill": 1.5,
      "aging_effect": 0.0,

      "clarity_modifiers": {
        "minecraft:blaze_powder": -0.2,
        "minecraft:sugar": +0.1
      },

      "flavor_profile": {
        "juniper": 3,
        "floral": 1,
        "citrus": 0
      }
    }
  }
}
```

This file alone defines:

- What ingredients gin can use
- What machines it must pass through
- How each stage modifies metadata
- How botanicals influence flavor
- How strong, clear, or aged the final drink is

No machine needs to know what “gin” is — it just follows the schema.

---
