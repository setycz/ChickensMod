# Chickens — Fabric 26.3

A port of setycz's MIT-licensed Chickens mod for **Minecraft 26.3**, **Java 25**, **Fabric Loader 0.19.5+**, and **Fabric API 0.160.7+26.3**. Install the mod and Fabric API on both client and server.

## Survival start

- Find White, Flint, Log, and Sand chickens in the Overworld. Quartz chickens spawn in the Nether.
- Craft a colored egg with one regular egg and its matching dye. Throw it (or dispense it): each egg has a 1/8 hatch chance; a successful hatch has a 1/32 chance of four chicks.
- Right-click a regular vanilla chicken with a book to make a Smart Chicken. The book is reusable, matching the original.
- Feed resource chickens the usual chicken breeding food. Different breeds can produce either parent or a breed unlocked by that pair. Same-breed parents improve stats, capped at 10.
- Craft the analyzer from one egg and one compass; right-click a resource chicken to read its stats and remaining laying time.

All **42 original breeds** are included. See [BREEDING.md](BREEDING.md) for the full breeding tree.

## Stats and production

**Growth** accelerates growing, breeding recovery, and laying: stat 10 is ten times as fast as stat 1. Base laying intervals are 6,000–12,000 ticks per breed tier before Growth and configuration adjustments.

**Gain** produces 1 item at levels 1–4, 2 at 5–9, and 3 at 10. **Strength** weights inheritance. Both parents contribute to each inherited stat. Unlocking a new breed starts its stats at 1/1/1; offspring matching an existing parent retain that parent's stats.

## Henhouses

Craft a henhouse with eight matching wood planks surrounding a hay bale. Oak, acacia, birch, dark oak, jungle, and spruce variants retain the original textures.

Resource chickens within five blocks on each axis send production to the closest loaded henhouse with fuel and space. No terrain is loaded to perform collection. One hay bale collects 100 items and then produces one dirt.

The henhouse has its original dedicated screen: hay input at the upper left, dirt output below it, nine resource slots on the right, and a live fuel bar between them. Hover over the fuel bar to see how many items remain before another hay bale is needed, the stored hay count, and whether more hay or dirt-output space is needed. Output slots reject manual insertion. Shift-clicking hay inserts it into the fuel slot; shift-clicking resources returns them to the player inventory. Other items stay in the player inventory. Hoppers feed hay from the top and extract dirt/resources from other sides. If storage is full or unavailable, the exact uncollected remainder drops beside the chicken.

Breaking a henhouse drops its inventory. Partially used hay energy belongs to the placed henhouse and is lost when it is broken.

## Liquid eggs

Water and Lava chickens lay stackable liquid eggs. Use one to place a source fluid, or use a dispenser. Each egg exposes one bucket through Fabric's fluid transfer API and is consumed when a full-bucket transfer commits. No empty bucket is created. Aborted or partial transfers consume nothing.

## Configuration

`config/chickens.json` is created at startup. It supports spawn weight and per-breed enablement, laying multiplier, production item, and death-drop item. Item names are validated; invalid settings report their location. Disabling an ancestor also disables descendant breeds. Restart after changing configuration.

This port uses modern item IDs: White, Blue, Black, and Brown chickens retain their original resource outputs: bone meal, lapis lazuli, ink sacs, and cocoa beans. Colored-egg crafting uses modern dye items. Existing Forge 1.10.2 entities and inventories are not migrated. JEI/WAILA integrations from Forge 1.10.2 are not included; the analyzer and breeding reference provide the information without those dependencies.

## Fixes carried by this port

- Partial henhouse collection returns only the remainder, removing the old duplication path.
- Henhouses collect the full Gain-adjusted output rather than silently collecting just one item.
- Gain inheritance uses both parents; the old implementation used parent two twice.
- Laying timers, fuel energy, inventories, and stats persist across saves.
- Saved stats are clamped to 1–10, and Growth does not repeatedly accelerate an already-saved baby age.
- Vanilla egg production is suppressed on resource chickens.
- GUI and hopper input/output restrictions prevent storage misuse.
- Legacy chicken textures use their matching adult model UV layout for all breeds and scaled chicks.

## Build and validation

With Java 25 selected, run `./gradlew build` (Windows: `gradlew.bat build`). The playable jar is generated under `build/libs`; the `-sources` jar is for developers.

Run `./gradlew -PregressionTests=true runRegressionServer` for the headless regression mod. It creates an isolated flat world, executes checks, and stops the server. Success prints `CHICKENS REGRESSION OK`; failure prints `CHICKENS REGRESSION FAILED`. The regression mod is excluded from the release jar.

The initial port was compiled for both client and server and exercised on a dedicated Minecraft 26.3 server with Fabric Loader 0.19.5 and Fabric API 0.160.7+26.3. Regression coverage includes all breed registrations, breeding and stats, persistence, collection/fuel boundaries, live production, death loot, liquid transfer transactions, dispenser behavior, menu restrictions, live fuel synchronization, shift-clicking, full player inventories, and breaking a stocked henhouse. Client visuals still need an in-game check.

Original mod and textures © 2016 setycz, distributed under the MIT license in [LICENSE.txt](LICENSE.txt).

## 0.1.3 baby chicken fix

Resource chickens now render with chick proportions while young: a smaller body,
legs and wings with a larger head. Breed textures and adult appearances are
preserved. The renderer switches automatically as the chicken grows up. Replace
the Chickens jar on client and server; existing chickens and worlds are supported.

## 0.1.2 texture fix

Item and block textures now use the modern `textures/item/` and `textures/block/` atlas directories. This fixes missing checkerboard icons and henhouse block textures. `python3 scripts/check_assets.py` checks custom model texture paths before every CI build. Entity and screen textures remain in their direct texture locations.

## Roost companion

[Roost for Fabric 26.3](companions/roost/README.md) adds the Chicken Catcher, captured chicken items, Roost, Chicken Breeder, and Roost Collector as a separate mod jar compatible with all 42 breeds.
