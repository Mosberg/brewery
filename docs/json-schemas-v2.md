# 1. Schemas for machine recipes

## 1.1 Generic machine recipe schema

`schemas/machine_recipe.schema.json`

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "Brewery Machine Recipe",
  "type": "object",
  "required": ["type", "machine", "inputs", "output"],
  "properties": {
    "type": {
      "type": "string",
      "const": "brewery:machine_recipe"
    },
    "machine": {
      "type": "string",
      "description": "Machine ID, e.g. brewery:fermenter"
    },
    "inputs": {
      "type": "array",
      "items": {
        "type": "object",
        "required": ["ingredient"],
        "properties": {
          "ingredient": {
            "type": "string",
            "description": "Item ID or tag, e.g. minecraft:wheat_seeds or #brewery:fermentables"
          },
          "amount": {
            "type": "integer",
            "minimum": 1,
            "default": 1
          },
          "role": {
            "type": "string",
            "enum": ["base", "botanical", "modifier", "catalyst", "container"],
            "default": "base"
          }
        }
      }
    },
    "output": {
      "type": "object",
      "required": ["liquid"],
      "properties": {
        "liquid": {
          "type": "string",
          "description": "Intermediate liquid ID, e.g. brewery:wort"
        },
        "amount": {
          "type": "integer",
          "minimum": 1,
          "default": 1000
        },
        "metadata_delta": {
          "type": "object",
          "description": "Adjustments to liquid metadata",
          "additionalProperties": true
        }
      }
    },
    "time": {
      "type": "integer",
      "minimum": 1,
      "description": "Ticks required"
    },
    "energy": {
      "type": "integer",
      "minimum": 0,
      "description": "Optional energy cost"
    }
  }
}
```

You can specialize this per machine by constraining `machine` and `inputs.role` if you want, but this generic schema is enough for data‑driven behavior.

---

## 2. Schemas for intermediate liquids

## 2.1 Intermediate liquid schema

`schemas/liquid.schema.json`

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "Brewery Liquid Definition",
  "type": "object",
  "required": ["id", "phase", "base_type"],
  "properties": {
    "id": {
      "type": "string",
      "description": "Liquid ID, e.g. brewery:wort"
    },
    "display_name": {
      "type": "string"
    },
    "phase": {
      "type": "string",
      "enum": ["mash", "must", "wort", "wash", "young_alcohol", "spirit", "aged", "flavored"]
    },
    "base_type": {
      "type": "string",
      "description": "Alcohol type or generic, e.g. brewery:beer or brewery:generic"
    },
    "default_metadata": {
      "type": "object",
      "description": "Base metadata for this liquid",
      "properties": {
        "sugar": { "type": "number" },
        "strength": { "type": "number" },
        "clarity": { "type": "number" },
        "color": { "type": "string" },
        "flavor_profile": {
          "type": "object",
          "additionalProperties": { "type": "number" }
        }
      },
      "additionalProperties": true
    }
  }
}
```

---

## 3. Unified registry format

Single file that ties together alcohol types, liquids, machines, and tags.

`data/brewery/registry.json`

```json
{
  "alcohol_types": [
    "brewery:absinthe",
    "brewery:ale",
    "brewery:beer",
    "brewery:brandy",
    "brewery:cider",
    "brewery:gin",
    "brewery:lager",
    "brewery:mead",
    "brewery:rum",
    "brewery:stout",
    "brewery:vodka",
    "brewery:whiskey",
    "brewery:wine"
  ],
  "liquids": [
    "brewery:mash",
    "brewery:must",
    "brewery:wort",
    "brewery:wash",
    "brewery:young_alcohol",
    "brewery:spirit",
    "brewery:aged_spirit",
    "brewery:flavored_spirit"
  ],
  "machines": [
    "brewery:crusher",
    "brewery:press",
    "brewery:mash_tun",
    "brewery:kettle",
    "brewery:fermenter",
    "brewery:distiller",
    "brewery:barrel",
    "brewery:infuser",
    "brewery:bottler"
  ],
  "tags": {
    "fermentables": "#brewery:fermentables",
    "seeds": "#brewery:seeds",
    "catalysts": "#brewery:catalysts",
    "effect_ingredients": "#brewery:effect_ingredients",
    "botanicals": "#brewery:botanicals"
  }
}
```

This gives you a single discovery point for everything—perfect for validation and tooling.

---

## 4. Full example pack (13 alcohol types)

All using the `alcohol_type.schema.json` from earlier.

I’ll show a couple fully, then the rest in the same structure (you can drop them straight into `data/brewery/alcohol_types/`).

## 4.1 `brewery:beer.json`

```json
{
  "id": "brewery:beer",
  "display_name": "Beer",
  "flavor_intent": "General grain-based beer baseline",
  "ingredients": {
    "fermentables": ["minecraft:wheat_seeds"],
    "botanicals": ["minecraft:torchflower_seeds", "#minecraft:small_flowers"],
    "modifiers": ["minecraft:redstone", "minecraft:glowstone_dust"]
  },
  "processing": {
    "stages": ["mash", "boil", "ferment", "condition"],
    "metadata_rules": {
      "base_strength": 1.5,
      "strength_per_distill": 0.0,
      "aging_effect": 0.2,
      "clarity_modifiers": {
        "minecraft:redstone": 0.1,
        "minecraft:glowstone_dust": -0.1
      },
      "flavor_profile": {
        "grain": 3,
        "bitter": 2,
        "floral": 1
      }
    }
  }
}
```

## 4.2 `brewery:gin.json`

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
        "minecraft:sugar": 0.1
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

## 4.3 Remaining types (same structure)

You can paste these as‑is; they’re consistent with your design intent.

`brewery:absinthe.json`

```json
{
  "id": "brewery:absinthe",
  "display_name": "Absinthe",
  "flavor_intent": "Herbal, aromatic, bitter spirit",
  "ingredients": {
    "fermentables": ["minecraft:honey_bottle", "minecraft:sweet_berries"],
    "botanicals": ["minecraft:dandelion", "minecraft:wither_rose", "#minecraft:leaves"],
    "modifiers": ["minecraft:sugar", "minecraft:glowstone_dust"]
  },
  "processing": {
    "stages": ["ferment", "distill", "infuse", "age"],
    "metadata_rules": {
      "base_strength": 3.0,
      "strength_per_distill": 1.5,
      "aging_effect": 0.3,
      "clarity_modifiers": {
        "minecraft:glowstone_dust": 0.2
      },
      "flavor_profile": {
        "herbal": 4,
        "bitter": 3,
        "sweet": 1
      }
    }
  }
}
```

`brewery:ale.json`

```json
{
  "id": "brewery:ale",
  "display_name": "Ale",
  "flavor_intent": "Malt-forward beer with gentle hops",
  "ingredients": {
    "fermentables": ["minecraft:wheat_seeds", "minecraft:potato"],
    "botanicals": ["#minecraft:small_flowers"],
    "modifiers": ["minecraft:redstone", "minecraft:glowstone_dust"]
  },
  "processing": {
    "stages": ["mash", "boil", "ferment", "condition"],
    "metadata_rules": {
      "base_strength": 1.8,
      "strength_per_distill": 0.0,
      "aging_effect": 0.3,
      "clarity_modifiers": {
        "minecraft:redstone": 0.1
      },
      "flavor_profile": {
        "grain": 3,
        "bitter": 1,
        "floral": 2
      }
    }
  }
}
```

`brewery:brandy.json`

```json
{
  "id": "brewery:brandy",
  "display_name": "Brandy",
  "flavor_intent": "Distilled fruit spirit, often aged",
  "ingredients": {
    "fermentables": ["minecraft:apple", "minecraft:melon_slice"],
    "botanicals": ["minecraft:rose_bush", "minecraft:peony"],
    "modifiers": ["minecraft:glowstone_dust", "minecraft:redstone"]
  },
  "processing": {
    "stages": ["press", "ferment", "distill", "age"],
    "metadata_rules": {
      "base_strength": 2.5,
      "strength_per_distill": 1.5,
      "aging_effect": 0.6,
      "clarity_modifiers": {},
      "flavor_profile": {
        "fruit": 3,
        "oak": 2,
        "sweet": 1
      }
    }
  }
}
```

`brewery:cider.json`

```json
{
  "id": "brewery:cider",
  "display_name": "Cider",
  "flavor_intent": "Fermented apple beverage",
  "ingredients": {
    "fermentables": ["minecraft:apple", "minecraft:sweet_berries"],
    "botanicals": ["minecraft:dandelion"],
    "modifiers": ["minecraft:redstone"]
  },
  "processing": {
    "stages": ["press", "ferment", "condition"],
    "metadata_rules": {
      "base_strength": 1.2,
      "strength_per_distill": 0.0,
      "aging_effect": 0.2,
      "clarity_modifiers": {},
      "flavor_profile": {
        "fruit": 3,
        "herbal": 1,
        "dry": 1
      }
    }
  }
}
```

`brewery:lager.json`

```json
{
  "id": "brewery:lager",
  "display_name": "Lager",
  "flavor_intent": "Clean, crisp beer with extended conditioning",
  "ingredients": {
    "fermentables": ["minecraft:wheat_seeds"],
    "botanicals": ["minecraft:azure_bluet"],
    "modifiers": ["minecraft:redstone"]
  },
  "processing": {
    "stages": ["mash", "boil", "ferment", "condition"],
    "metadata_rules": {
      "base_strength": 1.5,
      "strength_per_distill": 0.0,
      "aging_effect": 0.4,
      "clarity_modifiers": {
        "minecraft:redstone": 0.2
      },
      "flavor_profile": {
        "grain": 2,
        "crisp": 3,
        "bitter": 1
      }
    }
  }
}
```

`brewery:mead.json`

```json
{
  "id": "brewery:mead",
  "display_name": "Mead",
  "flavor_intent": "Honey wine with optional floral notes",
  "ingredients": {
    "fermentables": ["minecraft:honey_bottle"],
    "botanicals": ["minecraft:allium", "minecraft:dandelion", "minecraft:spore_blossom"],
    "modifiers": ["minecraft:glowstone_dust", "minecraft:redstone"]
  },
  "processing": {
    "stages": ["ferment", "age"],
    "metadata_rules": {
      "base_strength": 2.0,
      "strength_per_distill": 0.0,
      "aging_effect": 0.5,
      "clarity_modifiers": {},
      "flavor_profile": {
        "honey": 4,
        "floral": 2,
        "sweet": 2
      }
    }
  }
}
```

`brewery:rum.json`

```json
{
  "id": "brewery:rum",
  "display_name": "Rum",
  "flavor_intent": "Sugar-derived spirit, often aged",
  "ingredients": {
    "fermentables": ["minecraft:sugar", "minecraft:potato", "minecraft:sweet_berries"],
    "botanicals": ["minecraft:mangrove_leaves"],
    "modifiers": ["minecraft:blaze_powder", "minecraft:redstone"]
  },
  "processing": {
    "stages": ["ferment", "distill", "age"],
    "metadata_rules": {
      "base_strength": 2.5,
      "strength_per_distill": 1.5,
      "aging_effect": 0.7,
      "clarity_modifiers": {},
      "flavor_profile": {
        "sugar": 3,
        "oak": 2,
        "spice": 2
      }
    }
  }
}
```

`brewery:stout.json`

```json
{
  "id": "brewery:stout",
  "display_name": "Stout",
  "flavor_intent": "Dark, rich beer",
  "ingredients": {
    "fermentables": ["minecraft:wheat_seeds", "minecraft:beetroot"],
    "botanicals": ["minecraft:wither_rose", "minecraft:dark_oak_leaves"],
    "modifiers": ["minecraft:glowstone_dust", "minecraft:redstone"]
  },
  "processing": {
    "stages": ["mash", "boil", "ferment", "condition"],
    "metadata_rules": {
      "base_strength": 2.0,
      "strength_per_distill": 0.0,
      "aging_effect": 0.4,
      "clarity_modifiers": {},
      "flavor_profile": {
        "roast": 3,
        "bitter": 3,
        "sweet": 1
      }
    }
  }
}
```

`brewery:vodka.json`

```json
{
  "id": "brewery:vodka",
  "display_name": "Vodka",
  "flavor_intent": "Very neutral distilled spirit",
  "ingredients": {
    "fermentables": ["minecraft:potato"],
    "botanicals": [],
    "modifiers": ["minecraft:glowstone_dust", "minecraft:dragon_breath"]
  },
  "processing": {
    "stages": ["mash", "ferment", "distill", "distill"],
    "metadata_rules": {
      "base_strength": 2.5,
      "strength_per_distill": 2.0,
      "aging_effect": 0.0,
      "clarity_modifiers": {
        "minecraft:dragon_breath": 0.3
      },
      "flavor_profile": {
        "neutral": 4
      }
    }
  }
}
```

`brewery:whiskey.json`

```json
{
  "id": "brewery:whiskey",
  "display_name": "Whiskey",
  "flavor_intent": "Grain spirit with barrel aging",
  "ingredients": {
    "fermentables": ["minecraft:wheat_seeds"],
    "botanicals": [],
    "modifiers": ["minecraft:redstone", "minecraft:glowstone_dust"]
  },
  "processing": {
    "stages": ["mash", "ferment", "distill", "age"],
    "metadata_rules": {
      "base_strength": 2.5,
      "strength_per_distill": 1.5,
      "aging_effect": 0.8,
      "clarity_modifiers": {},
      "flavor_profile": {
        "grain": 3,
        "oak": 3,
        "smoke": 1
      }
    }
  }
}
```

`brewery:wine.json`

```json
{
  "id": "brewery:wine",
  "display_name": "Wine",
  "flavor_intent": "Fermented fruit beverage",
  "ingredients": {
    "fermentables": ["minecraft:sweet_berries", "minecraft:glow_berries", "minecraft:melon_slice"],
    "botanicals": ["minecraft:rose_bush", "minecraft:lilac"],
    "modifiers": ["minecraft:redstone", "minecraft:sugar"]
  },
  "processing": {
    "stages": ["press", "ferment", "age"],
    "metadata_rules": {
      "base_strength": 1.8,
      "strength_per_distill": 0.0,
      "aging_effect": 0.5,
      "clarity_modifiers": {},
      "flavor_profile": {
        "fruit": 4,
        "tannin": 2,
        "sweet": 1
      }
    }
  }
}
```

---

## 5. Automatic asset generation templates

These are the glue between your data and your assets.

## 5.1 Bottled drink item model template

`assets/brewery/models/item/bottle_drink_template.json`

```json
{
  "parent": "item/generated",
  "textures": {
    "layer0": "brewery:item/bottle_base",
    "layer1": "brewery:item/drink_overlays/${alcohol_type}"
  }
}
```

Your pipeline replaces `${alcohol_type}` with `beer`, `gin`, etc., and emits one model per type.

## 5.2 Block model template for machines

`assets/brewery/models/block/machine_template.json`

```json
{
  "parent": "block/cube",
  "textures": {
    "particle": "brewery:block/machines/${machine}",
    "down": "brewery:block/machines/${machine}_bottom",
    "up": "brewery:block/machines/${machine}_top",
    "north": "brewery:block/machines/${machine}_front",
    "south": "brewery:block/machines/${machine}_side",
    "west": "brewery:block/machines/${machine}_side",
    "east": "brewery:block/machines/${machine}_side"
  }
}
```

## 5.3 Lang entry template

```json
{
  "item.brewery.bottle_${id}": "${Display Name}",
  "block.brewery.${machine}": "${Machine Name}"
}
```

Your generator walks `alcohol_types` and `machines` from `registry.json` and emits localized names.

## 5.4 Tag generation template

From `alcohol_types` and `ingredients`:

```json
{
  "replace": false,
  "values": ["minecraft:wheat_seeds", "minecraft:potato"]
}
```

Generated into `data/brewery/tags/items/fermentables/beer.json`, etc., if you want per‑type tags.

---
