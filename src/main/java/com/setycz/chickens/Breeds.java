package com.setycz.chickens;
import java.util.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.*;
public final class Breeds {
 public enum SpawnType { NONE, NORMAL, SNOW, NETHER }
 public static final Map<String,Breed> ALL = new LinkedHashMap<>();
 public static Breed get(String id) { return ALL.get(id); }
 public static Breed byType(net.minecraft.world.entity.EntityType<?> type) { return ALL.values().stream().filter(b -> b.entityType == type).findFirst().orElse(ALL.get("smart")); }
 private static Breed dye(DyeColor color,String name) {
  Item item = switch(color) {
   case WHITE -> Items.BONE_MEAL;
   case BLUE -> Items.LAPIS_LAZULI;
   case BLACK -> Items.INK_SAC;
   case BROWN -> Items.COCOA_BEANS;
   default -> net.minecraft.core.registries.BuiltInRegistries.ITEM.getValue(Identifier.withDefaultNamespace(color.getName()+"_dye"));
  };
  return new Breed(color.getId(),name,Identifier.fromNamespaceAndPath("chickens","textures/entity/"+name.toLowerCase(Locale.ROOT)+".png"),item,0xffffff,0xffffff).setSpawnType(SpawnType.NONE).dye(color.getName());
 }
    public static List<Breed> defaults() {
        List<Breed> chickens = new ArrayList<Breed>();

        chickens.add(new Breed(
                50, "SmartChicken", Identifier.fromNamespaceAndPath("chickens", "textures/entity/smartchicken.png"),
                Items.EGG,
                0xffffff, 0xffff00).setSpawnType(SpawnType.NONE));

        // dye chickens
        Breed whiteChicken = dye(DyeColor.WHITE, "WhiteChicken").setDropItem(Items.BONE).setSpawnType(SpawnType.NORMAL);
        chickens.add(whiteChicken);
        Breed yellowChicken = dye(DyeColor.YELLOW, "YellowChicken");
        chickens.add(yellowChicken);
        Breed blueChicken = dye(DyeColor.BLUE, "BlueChicken");
        chickens.add(blueChicken);
        Breed greenChicken = dye(DyeColor.GREEN, "GreenChicken");
        chickens.add(greenChicken);
        Breed redChicken = dye(DyeColor.RED, "RedChicken");
        chickens.add(redChicken);
        Breed blackChicken = dye(DyeColor.BLACK, "BlackChicken");
        chickens.add(blackChicken);

        Breed pinkChicken = dye(DyeColor.PINK, "PinkChicken").setParentsNew(redChicken, whiteChicken);
        chickens.add(pinkChicken);
        Breed purpleChicken = dye(DyeColor.PURPLE, "PurpleChicken").setParentsNew(blueChicken, redChicken);
        chickens.add(purpleChicken);
        chickens.add(dye(DyeColor.ORANGE, "OrangeChicken").setParentsNew(redChicken, yellowChicken));
        chickens.add(dye(DyeColor.LIGHT_BLUE, "LightBlueChicken").setParentsNew(whiteChicken, blueChicken));
        chickens.add(dye(DyeColor.LIME, "LimeChicken").setParentsNew(greenChicken, whiteChicken));
        Breed grayChicken = dye(DyeColor.GRAY, "GrayChicken").setParentsNew(blackChicken, whiteChicken);
        chickens.add(grayChicken);
        chickens.add(dye(DyeColor.CYAN, "CyanChicken").setParentsNew(blueChicken, greenChicken));

        chickens.add(dye(DyeColor.LIGHT_GRAY, "SilverDyeChicken").setParentsNew(grayChicken, whiteChicken));
        chickens.add(dye(DyeColor.MAGENTA, "MagentaChicken").setParentsNew(purpleChicken, pinkChicken));

        // base chickens
        Breed flintChicken = new Breed(
                101, "FlintChicken", Identifier.fromNamespaceAndPath("chickens", "textures/entity/flintchicken.png"),
                Items.FLINT,
                0x6b6b47, 0xa3a375);
        chickens.add(flintChicken);

        Breed quartzChicken = new Breed(
                104, "QuartzChicken", Identifier.fromNamespaceAndPath("chickens", "textures/entity/quartzchicken.png"),
                Items.QUARTZ,
                0x4d0000, 0x1a0000).setSpawnType(SpawnType.NETHER);
        chickens.add(quartzChicken);


        Breed logChicken = new Breed(
                108, "LogChicken", Identifier.fromNamespaceAndPath("chickens", "textures/entity/logchicken.png"),
                Items.OAK_LOG,
                0x98846d, 0x528358);
        chickens.add(logChicken);

        Breed sandChicken = new Breed(
                105, "SandChicken", Identifier.fromNamespaceAndPath("chickens", "textures/entity/sandchicken.png"),
                Items.SAND,
                0xece5b1, 0xa7a06c);
        chickens.add(sandChicken);

        // Tier 2
        Breed stringChicken = new Breed(
                303, "StringChicken", Identifier.fromNamespaceAndPath("chickens", "textures/entity/stringchicken.png"),
                Items.STRING,
                0x331a00, 0x800000,
                blackChicken, logChicken
        ).setDropItem(Items.SPIDER_EYE);
        chickens.add(stringChicken);

        Breed glowstoneChicken = new Breed(
                202, "GlowstoneChicken", Identifier.fromNamespaceAndPath("chickens", "textures/entity/glowstonechicken.png"),
                Items.GLOWSTONE_DUST,
                0xffff66, 0xffff00,
                quartzChicken, yellowChicken);
        chickens.add(glowstoneChicken);

        Breed gunpowderChicken = new Breed(
                100, "GunpowderChicken", Identifier.fromNamespaceAndPath("chickens", "textures/entity/gunpowderchicken.png"),
                Items.GUNPOWDER,
                0x999999, 0x404040,
                sandChicken, flintChicken);
        chickens.add(gunpowderChicken);

        Breed redstoneChicken = new Breed(
                201, "RedstoneChicken", Identifier.fromNamespaceAndPath("chickens", "textures/entity/redstonechicken.png"),
                Items.REDSTONE,
                0xe60000, 0x800000,
                redChicken,
                sandChicken);
        chickens.add(redstoneChicken);

        Breed glassChicken = new Breed(
                106, "GlassChicken", Identifier.fromNamespaceAndPath("chickens", "textures/entity/glasschicken.png"),
                Items.GLASS,
                0xffffff, 0xeeeeff,
                quartzChicken, redstoneChicken);
        chickens.add(glassChicken);

        Breed ironChicken = new Breed(
                203, "IronChicken", Identifier.fromNamespaceAndPath("chickens", "textures/entity/ironchicken.png"),
                Items.IRON_INGOT,
                0xffffcc, 0xffcccc,
                flintChicken, whiteChicken);
        chickens.add(ironChicken);

        Breed coalChicken = new Breed(
                204, "CoalChicken", Identifier.fromNamespaceAndPath("chickens", "textures/entity/coalchicken.png"),
                Items.COAL,
                0x262626, 0x000000,
                flintChicken, logChicken);
        chickens.add(coalChicken);

        Breed brownChicken = dye(DyeColor.BROWN, "BrownChicken").setParentsNew(redChicken, greenChicken);
        brownChicken.layItem = Items.COCOA_BEANS;
        chickens.add(brownChicken);

        // tier 3
        Breed goldChicken = new Breed(
                300, "GoldChicken", Identifier.fromNamespaceAndPath("chickens", "textures/entity/goldchicken.png"),
                Items.GOLD_NUGGET,
                0xcccc00, 0xffff80,
                ironChicken, yellowChicken);
        chickens.add(goldChicken);

        Breed snowballChicken = new Breed(
                102, "SnowballChicken", Identifier.fromNamespaceAndPath("chickens", "textures/entity/snowballchicken.png"),
                Items.SNOWBALL,
                0x33bbff, 0x0088cc,
                blueChicken, logChicken).setSpawnType(SpawnType.SNOW);
        chickens.add(snowballChicken);

        Breed waterChicken = new Breed(
                206, "WaterChicken", Identifier.fromNamespaceAndPath("chickens", "textures/entity/waterchicken.png"),
                ChickensMod.WATER_EGG,
                0x000099, 0x8080ff,
                gunpowderChicken, snowballChicken);
        chickens.add(waterChicken);

        Breed lavaChicken = new Breed(
                103, "LavaChicken", Identifier.fromNamespaceAndPath("chickens", "textures/entity/lavachicken.png"),
                ChickensMod.LAVA_EGG,
                0xcc3300, 0xffff00,
                coalChicken, quartzChicken).setSpawnType(SpawnType.NETHER);
        chickens.add(lavaChicken);

        Breed clayChicken = new Breed(
                200, "ClayChicken", Identifier.fromNamespaceAndPath("chickens", "textures/entity/claychicken.png"),
                Items.CLAY_BALL,
                0xcccccc, 0xbfbfbf,
                snowballChicken, sandChicken);
        chickens.add(clayChicken);

        Breed leatherChicken = new Breed(
                107, "LeatherChicken", Identifier.fromNamespaceAndPath("chickens", "textures/entity/leatherchicken.png"),
                Items.LEATHER,
                0xA7A06C, 0x919191,
                stringChicken, brownChicken);
        chickens.add(leatherChicken);

        Breed netherwartChicken = new Breed(
                207, "NetherwartChicken", Identifier.fromNamespaceAndPath("chickens", "textures/entity/netherwartchicken.png"),
                Items.NETHER_WART,
                0x800000, 0x331a00,
                brownChicken, glowstoneChicken);
        chickens.add(netherwartChicken);

        // Tier 4
        Breed diamondChicken = new Breed(
                301, "DiamondChicken", Identifier.fromNamespaceAndPath("chickens", "textures/entity/diamondchicken.png"),
                Items.DIAMOND,
                0x99ccff, 0xe6f2ff,
                glassChicken, goldChicken);
        chickens.add(diamondChicken);

        Breed blazeChicken = new Breed(
                302, "BlazeChicken", Identifier.fromNamespaceAndPath("chickens", "textures/entity/blazechicken.png"),
                Items.BLAZE_ROD,
                0xffff66, 0xff3300,
                goldChicken, lavaChicken);
        chickens.add(blazeChicken);

        Breed slimeChicken = new Breed(
                205, "SlimeChicken", Identifier.fromNamespaceAndPath("chickens", "textures/entity/slimechicken.png"),
                Items.SLIME_BALL,
                0x009933, 0x99ffbb,
                clayChicken, greenChicken);
        chickens.add(slimeChicken);

        // Tier 5
        Breed enderChicken = new Breed(
                401, "EnderChicken", Identifier.fromNamespaceAndPath("chickens", "textures/entity/enderchicken.png"),
                Items.ENDER_PEARL,
                0x001a00, 0x001a33,
                diamondChicken, netherwartChicken);
        chickens.add(enderChicken);

        Breed ghastChicken = new Breed(
                402, "GhastChicken", Identifier.fromNamespaceAndPath("chickens", "textures/entity/ghastchicken.png"),
                Items.GHAST_TEAR,
                0xffffcc, 0xffffff,
                whiteChicken, blazeChicken);
        chickens.add(ghastChicken);

        Breed emeraldChicken = new Breed(
                400, "EmeraldChicken", Identifier.fromNamespaceAndPath("chickens", "textures/entity/emeraldchicken.png"),
                Items.EMERALD,
                0x00cc00, 0x003300,
                diamondChicken, greenChicken);
        chickens.add(emeraldChicken);

        Breed magmaChicken = new Breed(
                403, "MagmaChicken", Identifier.fromNamespaceAndPath("chickens", "textures/entity/magmachicken.png"),
                Items.MAGMA_CREAM,
                0x1a0500, 0x000000,
                slimeChicken, blazeChicken);
        chickens.add(magmaChicken);

        return chickens;

    }

}
