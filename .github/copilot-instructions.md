# Copilot Instructions for Brewery Mod

## Project Overview

- **Brewery** is a Minecraft mod for machine-driven brewing of custom alcoholic beverages using vanilla ingredients, generic processing stages, and metadata-based drink definitions.
- Focus: **Infrastructure, automation, and extensibility**—not adding new crops/items.
- All content is **data-driven** via JSON assets in `src/main/resources/data/brewery/` and schemas in `src/main/resources/data/schemas/`.

## Architecture & Key Patterns

- **Registries**: Four main registries manage all runtime data:
  - `AlcoholTypeRegistry`, `LiquidRegistry`, `MachineRegistry`, `MachineRecipeRegistry` (see `dk.mosberg.registry.*`).
  - Registries are pure data, loaded at runtime from JSON.
- **Data Loaders**: `BreweryDataLoader` and related parsers (`AlcoholTypeParser`, etc.) scan and load JSON assets on resource reload.
- **Machines**: Each machine is a block entity (see `dk.mosberg.machine.impl.*`) implementing a processing stage (e.g., `FermenterBlockEntity` for `FERMENT`).
  - All machines use `AbstractMachineBlockEntity` as a base for input/output and ticking logic.
- **Processing**: Machine logic is generic and metadata-driven, using `MetadataEngine` to apply stage effects.
- **Data Models**: All core data (alcohol types, liquids, recipes) are Java records in `dk.mosberg.data.*`.
- **JSON Schemas**: All data formats are strictly defined in `src/main/resources/data/schemas/`.

## Developer Workflows

- **Build**: Use Gradle (`./gradlew build`). JVM args and caching are optimized for Fabric modding (see `gradle.properties`).
- **Run/Debug**: Use Fabric Loom run configs (see `build.gradle` and generated run configs in `build/loom-cache/argFiles/`).
- **Add Content**: Add new alcohol types, liquids, or recipes by creating JSON files in the appropriate subfolder of `src/main/resources/data/brewery/`.
- **Schema Validation**: Reference JSON schemas in `src/main/resources/data/schemas/` for correct structure.

## Project Conventions

- **No hardcoded recipes or item logic**—all behavior is data-driven.
- **Vanilla-only ingredients**: All recipes use existing Minecraft items/tags.
- **Processing stages** are generic and extensible; machines are not tied to specific alcohol types.
- **Registries are cleared and reloaded** on resource reload (see `BreweryDataLoader`).
- **Naming**: Use `brewery:<name>` for all IDs in JSON and code.

## Key Files & Directories

- `src/main/java/dk/mosberg/registry/` — Registry classes
- `src/main/java/dk/mosberg/loader/` — Data loaders/parsers
- `src/main/java/dk/mosberg/machine/` — Machine block entities
- `src/main/resources/data/brewery/` — JSON data assets
- `src/main/resources/data/schemas/` — JSON schemas
- `README.md` — Project summary and JVM/Gradle config

## Example: Adding a New Alcohol Type

1. Create a new JSON file in `src/main/resources/data/brewery/alcohol_types/` (see `ale.json`, `beer.json` for examples).
2. Follow the schema in `schemas/alcohol_type.schema.json`.
3. No code changes required—data is auto-loaded at runtime.

---

For more, see the conceptual docs in `docs/` and the `README.md`.
See remote index in `.github/remote-index.md` for useful Fabric 1.21.11 resources.
