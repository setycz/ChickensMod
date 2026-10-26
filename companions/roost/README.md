# Roost — Fabric 26.3 companion

A port of [Tim Wood and David Vierra's Roost](https://github.com/timrwood/roost), licensed under MIT. Includes the Chicken Catcher, captured chicken items, Roost, Chicken Breeder, and Roost Collector. Original sprites, block geometry, chicken models, and machine GUI textures are reused.

Requires **Minecraft 26.3**, **Java 25**, **Fabric Loader 0.19.5+**, **Fabric API 0.160.7+26.3**, and this repository's **Chickens 0.1.2+**. Install both mods and Fabric API on client and server. This companion is a separate jar; it does not replace Chickens.

## Using it

1. Craft a **Chicken Catcher** with an egg above a stick above a feather. Right-click an adult vanilla or resource chicken. Catchers have 64 uses. Babies and chickens carrying or riding another entity cannot be caught.
2. Captured chickens are items. Matching portable data stacks to **16**. Breed, Growth/Gain/Strength, name, health, and other portable saved data are retained. Use the item on clear ground to release one. Capture/release never copies the original entity UUID.
3. Craft a **Roost** with planks across the top, planks at the middle left/right, and three hay bales across the bottom. Right-click it with captured chickens, or open the GUI and put them into its chicken slot. Production stays in its four output slots. Roosts require **no hay fuel and no seeds**. More chickens and higher Growth increase speed; Gain controls the amount produced. Vanilla chickens produce eggs or feathers.
4. Craft a **Chicken Breeder** using the same recipe with wheat seeds in the middle. Insert two stacks of captured chickens and seeds. Each successful breeding consumes **two seeds** and keeps the parents. Offspring follow the Chickens breeding tree and stat inheritance. The Breeder has three output slots. Vanilla chickens breed with vanilla chickens; resource chickens breed with resource chickens.
5. Craft a **Roost Collector** with planks down both sides, a captured vanilla chicken at top center, a hopper in the center, and a chest at bottom center. It gathers Roost output into 27 slots, scanning a 9×9 area from its own height through two blocks above. It never loads absent chunks or takes housed chickens.

Hoppers insert machine inputs from above and extract production from the other sides. Collectors allow item transfer on any side. Output slots reject manual insertion. Shift-clicking routes chickens/seeds to their own input slots. Breaking a machine drops its inventory.

Machine progress and status are synchronized to the screen. Hover over the progress arrow for percentage and status. Full output pauses production safely; pending output is saved and is not rerolled or deleted. Breeder seeds are charged only when the whole child item fits. Parents and progress are saved with the block. Changing parent data resets that machine's current cycle.

`config/roost.json` supports `roostSpeed` and `breederSpeed` (0.01–100, default 1). Restart to apply changes. Base production time is 6,000–12,000 ticks multiplied by breed tier and Chickens laying coefficient; Growth and matching stack count advance the cycle faster.

## Build

Build the Chickens jar at the repository root first, then run:

```sh
./gradlew -p companions/roost build
./gradlew -p companions/roost -PregressionTests=true runRegressionServer
```

The companion resolves the parent build at `../../build/libs/chickens-fabric-26.3-0.1.3.jar`. The headless regression harness is a separate development mod, excluded from the playable jar. Success prints `ROOST REGRESSION OK`.

## Scope and validation

This version supports vanilla chickens and all 42 breeds in our Chickens port. Other legacy More Chickens addons require their own ports. The original Forge JEI/WAILA hooks are not included. Client code is compiled and model/texture paths are checked; in-game appearance still needs a client playtest.

Dedicated-server regression coverage includes capture/release metadata, UUID isolation, catcher duplication guards, full inventory fallback, Roost production and stack capacity, pending output persistence, breeding seed costs and inheritance, full-output pauses, Collector transfers, hopper protection, and GUI data synchronization.

Original source and assets © 2019 Tim Wood, David Vierra. See LICENSE.txt.

## 0.1.1 fixes

Matching captured chickens now stack despite incidental entity timers, breeding cooldowns, combat memories, random follow-range bonuses, and random cluck voices. Capturing normalizes the cluck voice to the default. Breed, Growth/Gain/Strength, custom names, health, baby age, custom attributes, and other persistent traits remain intact. Previously captured items migrate automatically in player inventories and when Roost machines load or receive them; items stored in ordinary chests migrate when moved into your inventory. Stacks retain a maximum of 16. Chicken display models now define their particle texture. Companion registration also initializes Chickens first regardless of Fabric entrypoint order.
