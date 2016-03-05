package com.setycz.chickens;

import com.setycz.chickens.chicken.ChickenNetherPopulateHandler;
import com.setycz.chickens.chicken.EntityChickensChicken;
import com.setycz.chickens.coloredEgg.ItemColoredEgg;
import com.setycz.chickens.henhouse.BlockHenhouse;
import com.setycz.chickens.liquidEgg.ItemLiquidEgg;
import com.setycz.chickens.spawnEgg.ItemSpawnEgg;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by setyc on 12.02.2016.
 */
@Mod(modid = ChickensMod.MODID, version = ChickensMod.VERSION)
public class ChickensMod {
    public static final String MODID = "chickens";
    public static final String VERSION = "1.0.1";
    public static final String CHICKEN = "ChickensChicken";

    private static final CreativeTabs tab = new ChickensTab("chickens");

    private int chickenEntityId = 0;

    public static final Item spawnEgg = new ItemSpawnEgg().setUnlocalizedName("spawn_egg").setCreativeTab(tab);
    public static final Item coloredEgg = new ItemColoredEgg().setUnlocalizedName("colored_egg").setCreativeTab(tab);
    public static final Item liquidEgg = new ItemLiquidEgg().setUnlocalizedName("liquid_egg").setCreativeTab(tab);

    public static final Block henhouse = new BlockHenhouse().setUnlocalizedName("henhouse").setCreativeTab(tab);

    @SidedProxy(clientSide = "com.setycz.chickens.ClientProxy", serverSide = "com.setycz.chickens.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        EntityRegistry.registerModEntity(EntityChickensChicken.class, CHICKEN, chickenEntityId, this, 64, 3, true);

        registerChickens();
        GameRegistry.registerItem(coloredEgg, getItemName(coloredEgg));
        GameRegistry.registerItem(spawnEgg, getItemName(spawnEgg));

        registerLiquidEggs();
        GameRegistry.registerItem(liquidEgg, getItemName(liquidEgg));

        GameRegistry.registerBlock(henhouse, "henhouse");

        loadConfiguration(event.getSuggestedConfigurationFile());
    }

    private void loadConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        chickenEntityId = configuration.getInt("entityId", "general", 30000, Integer.MIN_VALUE, Integer.MAX_VALUE, "Chicken Entity ID");

        for (ChickensRegistryItem chicken : ChickensRegistry.getAllItems()) {
            boolean enabled = configuration.getBoolean("enabled", chicken.getEntityName(), true, "Is chicken enabled?");
            chicken.setEnabled(enabled);
            float layCoefficient = configuration.getFloat("layCoefficient", chicken.getEntityName(), 1.0f, 0.01f, 100.f, "Scale time to lay an egg.");
            chicken.setLayCoefficient(layCoefficient);
            ItemStack itemStack = getLayItemStack(configuration, chicken);
            chicken.setLayItem(itemStack);
        }

        configuration.save();
    }

    private ItemStack getLayItemStack(Configuration configuration, ChickensRegistryItem chicken) {
        ItemStack defaultItemStack = chicken.createLayItem();
        String eggItemName = configuration.getString("eggItemName", chicken.getEntityName(), defaultItemStack.getItem().getRegistryName(), "Item name to be laid.");
        int eggItemAmount = configuration.getInt("eggItemAmount", chicken.getEntityName(), defaultItemStack.stackSize, 1, 64, "Item amount to be laid.");
        int eggItemMeta = configuration.getInt("eggItemMeta", chicken.getEntityName(), defaultItemStack.getMetadata(), Integer.MIN_VALUE, Integer.MAX_VALUE, "Item amount to be laid.");

        ResourceLocation itemResourceLocation = new ResourceLocation(eggItemName);
        Item item = GameRegistry.findItem(itemResourceLocation.getResourceDomain(), itemResourceLocation.getResourcePath());
        if (item == null) {
            throw new RuntimeException("Cannot find egg item with name: " + eggItemName);
        }
        return new ItemStack(item, eggItemAmount, eggItemMeta);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();

        List<BiomeGenBase> biomesForSpawning = getAllSpawnBiomes();
        if (biomesForSpawning.size() > 0) {
            EntityRegistry.addSpawn(EntityChickensChicken.class, 10, 3, 5, EnumCreatureType.CREATURE,
                    biomesForSpawning.toArray(new BiomeGenBase[biomesForSpawning.size()])
            );
            if (biomesForSpawning.contains(BiomeGenBase.hell)) {
                MinecraftForge.EVENT_BUS.register(new ChickenNetherPopulateHandler());
            }
        }

        // register all chickens to Minecraft
        for (ChickensRegistryItem chicken : ChickensRegistry.getItems()) {
            proxy.registerChicken(chicken);
        }

        for (LiquidEggRegistryItem liquidEgg : LiquidEggRegistry.getAll()) {
            proxy.registerLiquidEgg(liquidEgg);
        }

        // waila integration
        FMLInterModComms.sendMessage("Waila", "register", "com.setycz.chickens.waila.ChickensEntityProvider.load");
    }

    private List<BiomeGenBase> getAllSpawnBiomes() {
        // chicken entity spawning
        BiomeGenBase[] allPossibleBiomes = {
                BiomeGenBase.plains, BiomeGenBase.extremeHills, BiomeGenBase.forest,
                BiomeGenBase.taiga, BiomeGenBase.swampland, BiomeGenBase.icePlains,
                BiomeGenBase.iceMountains, BiomeGenBase.forestHills, BiomeGenBase.taigaHills,
                BiomeGenBase.extremeHillsEdge, BiomeGenBase.jungle, BiomeGenBase.jungleHills,
                BiomeGenBase.jungleEdge, BiomeGenBase.birchForest, BiomeGenBase.birchForestHills,
                BiomeGenBase.roofedForest, BiomeGenBase.coldTaiga, BiomeGenBase.coldTaigaHills,
                BiomeGenBase.megaTaiga, BiomeGenBase.megaTaigaHills, BiomeGenBase.extremeHillsPlus,
                BiomeGenBase.savanna, BiomeGenBase.savannaPlateau, BiomeGenBase.hell};

        List<BiomeGenBase> biomesForSpawning = new ArrayList<BiomeGenBase>();
        for (BiomeGenBase biome: allPossibleBiomes) {
            if (ChickensRegistry.isAnyIn(ChickensRegistry.getSpawnType(biome))) {
                biomesForSpawning.add(biome);
            }
        }
        return biomesForSpawning;
    }

    private void registerLiquidEggs() {
        LiquidEggRegistry.register(new LiquidEggRegistryItem(0, Blocks.flowing_water, 0x0000ff));
        LiquidEggRegistry.register(new LiquidEggRegistryItem(1, Blocks.flowing_lava, 0xff0000));
    }

    private void registerChickens() {
        // dye chickens
        ChickensRegistryItem whiteChicken = new ChickensRegistryItem(
                0, "WhiteChicken", new ResourceLocation("chickens", "textures/entity/WhiteChicken.png"),
                new ItemStack(Items.dye, 1, EnumDyeColor.WHITE.getDyeDamage()),
                0xf2f2f2, 0xffffff);
        ChickensRegistry.register(whiteChicken);

        ChickensRegistryItem yellowChicken = new ChickensRegistryItem(
                4, "YellowChicken", new ResourceLocation("chickens", "textures/entity/YellowChicken.png"),
                new ItemStack(Items.dye, 1, EnumDyeColor.YELLOW.getDyeDamage()),
                0xffff00, 0xcccc00);
        ChickensRegistry.register(yellowChicken);

        ChickensRegistryItem blueChicken = new ChickensRegistryItem(
                11, "BlueChicken", new ResourceLocation("chickens", "textures/entity/BlueChicken.png"),
                new ItemStack(Items.dye, 1, EnumDyeColor.BLUE.getDyeDamage()),
                0x000066, 0x000033);
        ChickensRegistry.register(blueChicken);

        ChickensRegistryItem brownChicken = new ChickensRegistryItem(
                12, "BrownChicken", new ResourceLocation("chickens", "textures/entity/BrownChicken.png"),
                new ItemStack(Items.dye, 1, EnumDyeColor.BROWN.getDyeDamage()),
                0x663300, 0x1a0d00);
        ChickensRegistry.register(brownChicken);

        ChickensRegistryItem greenChicken = new ChickensRegistryItem(
                13, "GreenChicken", new ResourceLocation("chickens", "textures/entity/GreenChicken.png"),
                new ItemStack(Items.dye, 1, EnumDyeColor.GREEN.getDyeDamage()),
                0x006600, 0x003300);
        ChickensRegistry.register(greenChicken);

        ChickensRegistryItem redChicken = new ChickensRegistryItem(
                14, "RedChicken", new ResourceLocation("chickens", "textures/entity/RedChicken.png"),
                new ItemStack(Items.dye, 1, EnumDyeColor.RED.getDyeDamage()),
                0x660000, 0x330000);
        ChickensRegistry.register(redChicken);

        ChickensRegistry.register(new ChickensRegistryItem(
                15, "BlackChicken", new ResourceLocation("chickens", "textures/entity/BlackChicken.png"),
                new ItemStack(Items.dye, 1, EnumDyeColor.BLACK.getDyeDamage()),
                0x666666, 0x333333));

        // base chickens
        ChickensRegistryItem gunpowderChicken = new ChickensRegistryItem(
                100, "GunpowderChicken", new ResourceLocation("chickens", "textures/entity/GunpowderChicken.png"),
                new ItemStack(Items.gunpowder),
                0x999999, 0x404040);
        ChickensRegistry.register(gunpowderChicken);

        ChickensRegistryItem flintChicken = new ChickensRegistryItem(
                101, "FlintChicken", new ResourceLocation("chickens", "textures/entity/FlintChicken.png"),
                new ItemStack(Items.flint),
                0x6b6b47, 0xa3a375);
        ChickensRegistry.register(flintChicken);

        ChickensRegistryItem snowballChicken = new ChickensRegistryItem(
                102, "SnowballChicken", new ResourceLocation("chickens", "textures/entity/SnowballChicken.png"),
                new ItemStack(Items.snowball),
                0x33bbff, 0x0088cc).setSpawnType(SpawnType.SNOW);
        ChickensRegistry.register(snowballChicken);

        ChickensRegistryItem lavaChicken = new ChickensRegistryItem(
                103, "LavaChicken", new ResourceLocation("chickens", "textures/entity/LavaChicken.png"),
                new ItemStack(liquidEgg, 1, 1),
                0xcc3300, 0xffff00).setSpawnType(SpawnType.HELL);
        ChickensRegistry.register(lavaChicken);

        ChickensRegistryItem quartzChicken = new ChickensRegistryItem(
                104, "QuartzChicken", new ResourceLocation("chickens", "textures/entity/QuartzChicken.png"),
                new ItemStack(Items.quartz),
                0x4d0000, 0x1a0000).setSpawnType(SpawnType.HELL);
        ChickensRegistry.register(quartzChicken);

        // chicken tier 2
        ChickensRegistry.register(new ChickensRegistryItem(
                200, "ClayChicken", new ResourceLocation("chickens", "textures/entity/ClayChicken.png"),
                new ItemStack(Items.clay_ball),
                0xcccccc, 0xbfbfbf,
                flintChicken,
                snowballChicken));

        ChickensRegistry.register(new ChickensRegistryItem(
                201, "RedstoneChicken", new ResourceLocation("chickens", "textures/entity/RedstoneChicken.png"),
                new ItemStack(Items.redstone),
                0xe60000, 0x800000,
                redChicken,
                gunpowderChicken));

        ChickensRegistryItem glowstoneChicken = new ChickensRegistryItem(
                202, "GlowstoneChicken", new ResourceLocation("chickens", "textures/entity/GlowstoneChicken.png"),
                new ItemStack(Items.glowstone_dust),
                0xffff66, 0xffff00,
                yellowChicken, gunpowderChicken);
        ChickensRegistry.register(glowstoneChicken);

        ChickensRegistryItem ironChicken = new ChickensRegistryItem(
                203, "IronChicken", new ResourceLocation("chickens", "textures/entity/IronChicken.png"),
                new ItemStack(Items.iron_ingot),
                0xffffcc, 0xffcccc,
                flintChicken, whiteChicken);
        ChickensRegistry.register(ironChicken);

        ChickensRegistryItem coalChicken = new ChickensRegistryItem(
                204, "CoalChicken", new ResourceLocation("chickens", "textures/entity/CoalChicken.png"),
                new ItemStack(Items.coal),
                0x262626, 0x000000,
                flintChicken, lavaChicken);
        ChickensRegistry.register(coalChicken);

        ChickensRegistryItem slimeChicken = new ChickensRegistryItem(
                205, "SlimeChicken", new ResourceLocation("chickens", "textures/entity/SlimeChicken.png"),
                new ItemStack(Items.slime_ball),
                0x009933, 0x99ffbb,
                snowballChicken, greenChicken);
        ChickensRegistry.register(slimeChicken);

        ChickensRegistry.register(new ChickensRegistryItem(
                206, "WaterChicken", new ResourceLocation("chickens", "textures/entity/WaterChicken.png"),
                new ItemStack(liquidEgg, 1, 0),
                0x000099, 0x8080ff,
                snowballChicken, lavaChicken));

        ChickensRegistry.register(new ChickensRegistryItem(
                207, "NetherwartChicken", new ResourceLocation("chickens", "textures/entity/NetherwartChicken.png"),
                new ItemStack(Items.nether_wart),
                0x800000, 0x331a00,
                brownChicken, quartzChicken));

        // tier 3
        ChickensRegistry.register(new ChickensRegistryItem(
                300, "GoldChicken", new ResourceLocation("chickens", "textures/entity/GoldChicken.png"),
                new ItemStack(Items.gold_nugget),
                0xcccc00, 0xffff80,
                ironChicken, yellowChicken));

        ChickensRegistryItem diamondChicken = new ChickensRegistryItem(
                301, "DiamondChicken", new ResourceLocation("chickens", "textures/entity/DiamondChicken.png"),
                new ItemStack(Items.diamond),
                0x99ccff, 0xe6f2ff,
                coalChicken, blueChicken);
        ChickensRegistry.register(diamondChicken);

        ChickensRegistryItem blazeChicken = new ChickensRegistryItem(
                302, "BlazeChicken", new ResourceLocation("chickens", "textures/entity/BlazeChicken.png"),
                new ItemStack(Items.blaze_rod),
                0xffff66, 0xff3300,
                glowstoneChicken, quartzChicken);
        ChickensRegistry.register(blazeChicken);

        // tier 4
        ChickensRegistry.register(new ChickensRegistryItem(
                400, "EmeraldChicken", new ResourceLocation("chickens", "textures/entity/EmeraldChicken.png"),
                new ItemStack(Items.emerald),
                0x00cc00, 0x003300,
                diamondChicken, greenChicken));

        ChickensRegistry.register(new ChickensRegistryItem(
                401, "EnderChicken", new ResourceLocation("chickens", "textures/entity/EnderChicken.png"),
                new ItemStack(Items.ender_pearl),
                0x001a00, 0x001a33,
                diamondChicken, blazeChicken));

        ChickensRegistry.register(new ChickensRegistryItem(
                402, "GhastChicken", new ResourceLocation("chickens", "textures/entity/GhastChicken.png"),
                new ItemStack(Items.ghast_tear),
                0xffffcc, 0xffffff,
                diamondChicken, quartzChicken));

        ChickensRegistry.register(new ChickensRegistryItem(
                403, "MagmaChicken", new ResourceLocation("chickens", "textures/entity/MagmaChicken.png"),
                new ItemStack(Items.magma_cream),
                0x1a0500, 0x000000,
                slimeChicken, blazeChicken));

    }

    public static String getItemName(Item item) {
        return item.getUnlocalizedName().substring(5);
    }
}
