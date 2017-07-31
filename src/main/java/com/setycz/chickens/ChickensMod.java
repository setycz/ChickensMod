package com.setycz.chickens;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.setycz.chickens.block.BlockHenhouse;
import com.setycz.chickens.block.TileEntityHenhouse;
import com.setycz.chickens.client.gui.TileEntityGuiHandler;
import com.setycz.chickens.common.CommonProxy;
import com.setycz.chickens.config.ConfigHandler;
import com.setycz.chickens.entity.EntityChickensChicken;
import com.setycz.chickens.handler.ChickenNetherPopulateHandler;
import com.setycz.chickens.handler.ChickenTeachHandler;
import com.setycz.chickens.handler.ChickensTab;
import com.setycz.chickens.handler.SpawnType;
import com.setycz.chickens.item.ItemAnalyzer;
import com.setycz.chickens.item.ItemColoredEgg;
import com.setycz.chickens.item.ItemLiquidEgg;
import com.setycz.chickens.item.ItemSpawnEgg;
import com.setycz.chickens.registry.ChickensRegistry;
import com.setycz.chickens.registry.ChickensRegistryItem;
import com.setycz.chickens.registry.LiquidEggRegistry;
import com.setycz.chickens.registry.LiquidEggRegistryItem;

import joptsimple.internal.Strings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * Created by setyc on 12.02.2016.
 */
@Mod(modid = ChickensMod.MODID,
		name=ChickensMod.NAME,
        version = ChickensMod.VERSION,
        acceptedMinecraftVersions = "[1.11.2]",
        dependencies = "required-after:forge@[13.20.1.2386,);")
public class ChickensMod {
    public static final String MODID = "chickens";
    public static final String VERSION = "@VERSION@";
    public static final String CHICKEN = "ChickensChicken";
    public static final String NAME = "Chickens";

    public static final Logger log = LogManager.getLogger(MODID);

    @Mod.Instance(MODID)
    public static ChickensMod instance;

    private static final CreativeTabs chickensTab = new ChickensTab();

    public static final Item spawnEgg = new ItemSpawnEgg().setRegistryName("spawn_egg").setUnlocalizedName("spawn_egg").setCreativeTab(chickensTab);
    public static final Item coloredEgg = new ItemColoredEgg().setRegistryName("colored_egg").setUnlocalizedName("colored_egg").setCreativeTab(chickensTab);
    public static final Item liquidEgg = new ItemLiquidEgg().setRegistryName("liquid_egg").setUnlocalizedName("liquid_egg").setCreativeTab(chickensTab);
    public static final Item analyzer = new ItemAnalyzer().setRegistryName("analyzer").setUnlocalizedName("analyzer").setCreativeTab(chickensTab);

    public static final Block henhouse = new BlockHenhouse().setRegistryName("henhouse").setUnlocalizedName("henhouse").setCreativeTab(chickensTab);
    public static final Block henhouse_acacia = new BlockHenhouse().setRegistryName("henhouse_acacia").setUnlocalizedName("henhouse_acacia").setCreativeTab(chickensTab);
    public static final Block henhouse_birch = new BlockHenhouse().setRegistryName("henhouse_birch").setUnlocalizedName("henhouse_birch").setCreativeTab(chickensTab);
    public static final Block henhouse_dark_oak = new BlockHenhouse().setRegistryName("henhouse_dark_oak").setUnlocalizedName("henhouse_dark_oak").setCreativeTab(chickensTab);
    public static final Block henhouse_jungle = new BlockHenhouse().setRegistryName("henhouse_jungle").setUnlocalizedName("henhouse_jungle").setCreativeTab(chickensTab);
    public static final Block henhouse_spruce = new BlockHenhouse().setRegistryName("henhouse_spruce").setUnlocalizedName("henhouse_spruce").setCreativeTab(chickensTab);

    public static final TileEntityGuiHandler guiHandler = new TileEntityGuiHandler();

    @SidedProxy(clientSide = "com.setycz.chickens.client.ClientProxy", serverSide = "com.setycz.chickens.common.CommonProxy")
    public static CommonProxy proxy;

    public boolean getAlwaysShowStats() {
        return ConfigHandler.alwaysShowStats;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, guiHandler);

        EntityRegistry.registerModEntity(new ResourceLocation(ChickensMod.MODID, CHICKEN), EntityChickensChicken.class, CHICKEN, ConfigHandler.chickenEntityId, this, 64, 3, true);

        GameRegistry.register(coloredEgg);
        GameRegistry.register(spawnEgg);

        GameRegistry.register(liquidEgg);
        GameRegistry.register(analyzer);

        GameRegistry.registerTileEntity(TileEntityHenhouse.class, "henhouse");
        registerBlock(henhouse);
        registerBlock(henhouse_acacia);
        registerBlock(henhouse_birch);
        registerBlock(henhouse_dark_oak);
        registerBlock(henhouse_jungle);
        registerBlock(henhouse_spruce);

        registerLiquidEggs();
        
        ConfigHandler.LoadConfigs(generateDefaultChickens());

        log.info("Enabled chickens: {}", getChickenNames(ChickensRegistry.getItems()));
        log.info("Disabled chickens: {}", getChickenNames(ChickensRegistry.getDisabledItems()));
        for (SpawnType spawnType : SpawnType.values()) {
            log.info("[{}] biome type will spawn {} ({})",
                    spawnType, getChickenNames(ChickensRegistry.getPossibleChickensToSpawn(spawnType)));
        }

        
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    	dumpChickens(ChickensRegistry.getItems());
    }

    private void registerBlock(Block block) {
        GameRegistry.register(block);
        GameRegistry.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
    }

    private List<String> getChickenNames(Collection<ChickensRegistryItem> chickens) {
        List<String> result = new ArrayList<String>();
        for (ChickensRegistryItem chicken : chickens) {
            result.add(chicken.getEntityName());
        }
        return result;
    }

    @SuppressWarnings("unused")
	private String getAllAvailableSpawnTypes() {
        String spawnTypes = "";
        String[] spawnTypeNames = SpawnType.names();
        for (int spawnTypeIndex = 0; spawnTypeIndex < spawnTypeNames.length; spawnTypeIndex++) {
            if (spawnTypeIndex > 0) {
                spawnTypes += ", ";
            }
            spawnTypes += spawnTypeNames[spawnTypeIndex];
        }
        return spawnTypes;
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();

        MinecraftForge.EVENT_BUS.register(new ChickenTeachHandler());

        List<Biome> biomesForSpawning = getAllSpawnBiomes();
        if (biomesForSpawning.size() > 0) {
            EntityRegistry.addSpawn(EntityChickensChicken.class, ConfigHandler.spawnProbability, ConfigHandler.minBroodSize, ConfigHandler.maxBroodSize, EnumCreatureType.CREATURE,
                    biomesForSpawning.toArray(new Biome[biomesForSpawning.size()])
            );
            if (biomesForSpawning.contains(Biomes.HELL)) {
                MinecraftForge.TERRAIN_GEN_BUS.register(new ChickenNetherPopulateHandler(ConfigHandler.netherSpawnChanceMultiplier));
            }
        }

        // register all chickens to Minecraft
        for (ChickensRegistryItem chicken : ChickensRegistry.getItems()) {
            proxy.registerChicken(chicken);
        }

        for (LiquidEggRegistryItem liquidEgg : LiquidEggRegistry.getAll()) {
            proxy.registerLiquidEgg(liquidEgg);
        }

        GameRegistry.addShapelessRecipe(
                new ItemStack(ChickensMod.analyzer, 1),
                new ItemStack(Items.EGG), new ItemStack(Items.COMPASS)
        );

        registerHenhouse(henhouse_acacia, BlockPlanks.EnumType.ACACIA);
        registerHenhouse(henhouse_birch, BlockPlanks.EnumType.BIRCH);
        registerHenhouse(henhouse_dark_oak, BlockPlanks.EnumType.DARK_OAK);
        registerHenhouse(henhouse_jungle, BlockPlanks.EnumType.JUNGLE);
        registerHenhouse(henhouse_spruce, BlockPlanks.EnumType.SPRUCE);
        registerHenhouse(henhouse, BlockPlanks.EnumType.OAK);

        // waila integration
        FMLInterModComms.sendMessage("waila", "register", "com.setycz.chickens.waila.ChickensEntityProvider.load");
    }

    private boolean requiresVisitingNether(ChickensRegistryItem chicken) {
        //noinspection ConstantConditions
        return chicken.getTier() == 1 
                ? chicken.getSpawnType() == SpawnType.HELL
                :  chicken.isBreedable() && (requiresVisitingNether(chicken.getParent1()) || requiresVisitingNether(chicken.getParent2()));
    }

    private void dumpChickens(Collection<ChickensRegistryItem> items) {
        try {
            FileWriter file = new FileWriter("logs/chickens.gml");
            file.write("graph [\n");
            file.write("\tdirected 1\n");
            for (ChickensRegistryItem item : items) {
                file.write("\tnode [\n");
                file.write("\t\tid " + item.getRegistryName().toString() + "\n");
                file.write("\t\tlabel \"" + item.getEntityName() + "\"\n");
                if (requiresVisitingNether(item)) {
                    file.write("\t\tgraphics [\n");
                    file.write("\t\t\tfill \"#FF6600\"\n");
                    file.write("\t\t]\n");
                }
                file.write("\t]\n");
            }
            for (ChickensRegistryItem item : items) {
                if (item.getParent1() != null) {
                    file.write("\tedge [\n");
                    file.write("\t\tsource " + item.getParent1().getRegistryName().toString() + "\n");
                    file.write("\t\ttarget " + item.getRegistryName().toString() + "\n");
                    file.write("\t]\n");
                }
                if (item.getParent2() != null) {
                    file.write("\tedge [\n");
                    file.write("\t\tsource " + item.getParent2().getRegistryName().toString() + "\n");
                    file.write("\t\ttarget " + item.getRegistryName().toString() + "\n");
                    file.write("\t]\n");
                }
            }
            file.write("]\n");
            file.close();
        } catch (IOException ignored) {
        }
    }

    private void registerHenhouse(Block henhouse, BlockPlanks.EnumType type) {
        //noinspection ConstantConditions
        GameRegistry.addRecipe(new ShapedOreRecipe(
                new ItemStack(Item.getItemFromBlock(henhouse)),
                "PPP",
                "PHP",
                "PPP",
                'P', type == BlockPlanks.EnumType.OAK ? "plankWood" : new ItemStack(Blocks.PLANKS, 1, type.getMetadata()),
                'H', Blocks.HAY_BLOCK
        ));
    }

    private List<Biome> getAllSpawnBiomes() {
        // chicken entity spawning
        Biome[] allPossibleBiomes = {
                Biomes.PLAINS, Biomes.EXTREME_HILLS, Biomes.FOREST,
                Biomes.TAIGA, Biomes.SWAMPLAND, Biomes.ICE_PLAINS,
                Biomes.ICE_MOUNTAINS, Biomes.FOREST_HILLS, Biomes.TAIGA_HILLS,
                Biomes.EXTREME_HILLS_EDGE, Biomes.JUNGLE, Biomes.JUNGLE_HILLS,
                Biomes.JUNGLE_EDGE, Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS,
                Biomes.ROOFED_FOREST, Biomes.COLD_TAIGA, Biomes.COLD_TAIGA_HILLS,
                Biomes.EXTREME_HILLS,
                Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.HELL};

        List<Biome> biomesForSpawning = new ArrayList<Biome>();
        for (Biome biome : allPossibleBiomes) {
            if (ChickensRegistry.isAnyIn(ChickensRegistry.getSpawnType(biome))) {
                biomesForSpawning.add(biome);
            }
        }
        return biomesForSpawning;
    }

    private void registerLiquidEggs() {
        LiquidEggRegistry.register(new LiquidEggRegistryItem(0, Blocks.FLOWING_WATER, 0x0000ff, FluidRegistry.WATER));
        LiquidEggRegistry.register(new LiquidEggRegistryItem(1, Blocks.FLOWING_LAVA, 0xff0000, FluidRegistry.LAVA));
    }

    ChickensRegistryItem createDyeChicken(EnumDyeColor color, String name) {
        return new ChickensRegistryItem(
                new ResourceLocation(ChickensMod.MODID, name), name, new ResourceLocation("chickens", "textures/entity/" + Strings.join(name.split("(?=[A-Z])"), "_").toLowerCase() + ".png"),
                new ItemStack(Items.DYE, 1, color.getDyeDamage()),
                0xf2f2f2, color.getMapColor().colorValue).setSpawnType(SpawnType.NONE);
    }

    private List<ChickensRegistryItem> generateDefaultChickens() {
        List<ChickensRegistryItem> chickens = new ArrayList<ChickensRegistryItem>();

        chickens.add(new ChickensRegistryItem(
                ChickensRegistry.SMART_CHICKEN_ID, "SmartChicken", new ResourceLocation("chickens", "textures/entity/smart_chicken.png"),
                new ItemStack(Items.EGG),
                0xffffff, 0xffff00).setSpawnType(SpawnType.NONE));

        // dye chickens
        ChickensRegistryItem whiteChicken = createDyeChicken(EnumDyeColor.WHITE, "WhiteChicken").setDropItem(new ItemStack(Items.BONE)).setSpawnType(SpawnType.NORMAL);
        chickens.add(whiteChicken);
        ChickensRegistryItem yellowChicken = createDyeChicken(EnumDyeColor.YELLOW, "YellowChicken");
        chickens.add(yellowChicken);
        ChickensRegistryItem blueChicken = createDyeChicken(EnumDyeColor.BLUE, "BlueChicken");
        chickens.add(blueChicken);
        ChickensRegistryItem greenChicken = createDyeChicken(EnumDyeColor.GREEN, "GreenChicken");
        chickens.add(greenChicken);
        ChickensRegistryItem redChicken = createDyeChicken(EnumDyeColor.RED, "RedChicken");
        chickens.add(redChicken);
        ChickensRegistryItem blackChicken = createDyeChicken(EnumDyeColor.BLACK, "BlackChicken");
        chickens.add(blackChicken);

        ChickensRegistryItem pinkChicken = createDyeChicken(EnumDyeColor.PINK, "PinkChicken").setParentsNew(redChicken, whiteChicken);
        chickens.add(pinkChicken);
        ChickensRegistryItem purpleChicken = createDyeChicken(EnumDyeColor.PURPLE, "PurpleChicken").setParentsNew(blueChicken, redChicken);
        chickens.add(purpleChicken);
        chickens.add(createDyeChicken(EnumDyeColor.ORANGE, "OrangeChicken").setParentsNew(redChicken, yellowChicken));
        chickens.add(createDyeChicken(EnumDyeColor.LIGHT_BLUE, "LightBlueChicken").setParentsNew(whiteChicken, blueChicken));
        chickens.add(createDyeChicken(EnumDyeColor.LIME, "LimeChicken").setParentsNew(greenChicken, whiteChicken));
        ChickensRegistryItem grayChicken = createDyeChicken(EnumDyeColor.GRAY, "GrayChicken").setParentsNew(blackChicken, whiteChicken);
        chickens.add(grayChicken);
        chickens.add(createDyeChicken(EnumDyeColor.CYAN, "CyanChicken").setParentsNew(blueChicken, greenChicken));

        chickens.add(createDyeChicken(EnumDyeColor.SILVER, "SilverDyeChicken").setParentsNew(grayChicken, whiteChicken));
        chickens.add(createDyeChicken(EnumDyeColor.MAGENTA, "MagentaChicken").setParentsNew(purpleChicken, pinkChicken));

        // base chickens
        ChickensRegistryItem flintChicken = new ChickensRegistryItem(
                new ResourceLocation(ChickensMod.MODID, "FlintChicken"), "FlintChicken", new ResourceLocation("chickens", "textures/entity/flint_chicken.png"),
                new ItemStack(Items.FLINT),
                0x6b6b47, 0xa3a375);
        chickens.add(flintChicken);

        ChickensRegistryItem quartzChicken = new ChickensRegistryItem(
                new ResourceLocation(ChickensMod.MODID, "QuartzChicken"), "QuartzChicken", new ResourceLocation("chickens", "textures/entity/quartz_chicken.png"),
                new ItemStack(Items.QUARTZ),
                0x4d0000, 0x1a0000).setSpawnType(SpawnType.HELL);
        chickens.add(quartzChicken);


        ChickensRegistryItem logChicken = new ChickensRegistryItem(
                new ResourceLocation(ChickensMod.MODID, "LogChicken"), "LogChicken", new ResourceLocation("chickens", "textures/entity/log_chicken.png"),
                new ItemStack(Blocks.LOG),
                0x98846d, 0x528358);
        chickens.add(logChicken);

        ChickensRegistryItem sandChicken = new ChickensRegistryItem(
                new ResourceLocation(ChickensMod.MODID, "SandChicken"), "SandChicken", new ResourceLocation("chickens", "textures/entity/sand_chicken.png"),
                new ItemStack(Blocks.SAND),
                0xece5b1, 0xa7a06c);
        chickens.add(sandChicken);

        // Tier 2
        ChickensRegistryItem stringChicken = new ChickensRegistryItem(
                new ResourceLocation(ChickensMod.MODID, "StringChicken"), "StringChicken", new ResourceLocation("chickens", "textures/entity/string_chicken.png"),
                new ItemStack(Items.STRING),
                0x331a00, 0x800000,
                blackChicken, logChicken
        ).setDropItem(new ItemStack(Items.SPIDER_EYE));
        chickens.add(stringChicken);

        ChickensRegistryItem glowstoneChicken = new ChickensRegistryItem(
                new ResourceLocation(ChickensMod.MODID, "GlowstoneChicken"), "GlowstoneChicken", new ResourceLocation("chickens", "textures/entity/glowstone_chicken.png"),
                new ItemStack(Items.GLOWSTONE_DUST),
                0xffff66, 0xffff00,
                quartzChicken, yellowChicken);
        chickens.add(glowstoneChicken);

        ChickensRegistryItem gunpowderChicken = new ChickensRegistryItem(
                new ResourceLocation(ChickensMod.MODID, "GunpowderChicken"), "GunpowderChicken", new ResourceLocation("chickens", "textures/entity/gunpowder_chicken.png"),
                new ItemStack(Items.GUNPOWDER),
                0x999999, 0x404040,
                sandChicken, flintChicken);
        chickens.add(gunpowderChicken);

        ChickensRegistryItem redstoneChicken = new ChickensRegistryItem(
                new ResourceLocation(ChickensMod.MODID, "RedstoneChicken"), "RedstoneChicken", new ResourceLocation("chickens", "textures/entity/redstone_chicken.png"),
                new ItemStack(Items.REDSTONE),
                0xe60000, 0x800000,
                redChicken,
                sandChicken);
        chickens.add(redstoneChicken);

        ChickensRegistryItem glassChicken = new ChickensRegistryItem(
                new ResourceLocation(ChickensMod.MODID, "GlassChicken"), "GlassChicken", new ResourceLocation("chickens", "textures/entity/glass_chicken.png"),
                new ItemStack(Blocks.GLASS),
                0xffffff, 0xeeeeff,
                quartzChicken, redstoneChicken);
        chickens.add(glassChicken);

        ChickensRegistryItem ironChicken = new ChickensRegistryItem(
                new ResourceLocation(ChickensMod.MODID, "IronChicken"), "IronChicken", new ResourceLocation("chickens", "textures/entity/iron_chicken.png"),
                new ItemStack(Items.IRON_INGOT),
                0xffffcc, 0xffcccc,
                flintChicken, whiteChicken);
        chickens.add(ironChicken);

        ChickensRegistryItem coalChicken = new ChickensRegistryItem(
                new ResourceLocation(ChickensMod.MODID, "CoalChicken"), "CoalChicken", new ResourceLocation("chickens", "textures/entity/coal_chicken.png"),
                new ItemStack(Items.COAL),
                0x262626, 0x000000,
                flintChicken, logChicken);
        chickens.add(coalChicken);

        ChickensRegistryItem brownChicken = createDyeChicken(EnumDyeColor.BROWN, "BrownChicken").setParentsNew(redChicken, greenChicken);
        chickens.add(brownChicken);

        // tier 3
        ChickensRegistryItem goldChicken = new ChickensRegistryItem(
                new ResourceLocation(ChickensMod.MODID, "GoldChicken"), "GoldChicken", new ResourceLocation("chickens", "textures/entity/gold_chicken.png"),
                new ItemStack(Items.GOLD_NUGGET),
                0xcccc00, 0xffff80,
                ironChicken, yellowChicken);
        chickens.add(goldChicken);

        ChickensRegistryItem snowballChicken = new ChickensRegistryItem(
                new ResourceLocation(ChickensMod.MODID, "SnowballChicken"), "SnowballChicken", new ResourceLocation("chickens", "textures/entity/snowball_chicken.png"),
                new ItemStack(Items.SNOWBALL),
                0x33bbff, 0x0088cc,
                blueChicken, logChicken).setSpawnType(SpawnType.SNOW);
        chickens.add(snowballChicken);

        ChickensRegistryItem waterChicken = new ChickensRegistryItem(
                new ResourceLocation(ChickensMod.MODID, "WaterChicken"), "WaterChicken", new ResourceLocation("chickens", "textures/entity/water_chicken.png"),
                new ItemStack(liquidEgg, 1, 0),
                0x000099, 0x8080ff,
                gunpowderChicken, snowballChicken);
        chickens.add(waterChicken);

        ChickensRegistryItem lavaChicken = new ChickensRegistryItem(
                new ResourceLocation(ChickensMod.MODID, "LavaChicken"), "LavaChicken", new ResourceLocation("chickens", "textures/entity/lava_chicken.png"),
                new ItemStack(liquidEgg, 1, 1),
                0xcc3300, 0xffff00,
                coalChicken, quartzChicken).setSpawnType(SpawnType.HELL);
        chickens.add(lavaChicken);

        ChickensRegistryItem clayChicken = new ChickensRegistryItem(
                new ResourceLocation(ChickensMod.MODID, "ClayChicken"), "ClayChicken", new ResourceLocation("chickens", "textures/entity/clay_chicken.png"),
                new ItemStack(Items.CLAY_BALL),
                0xcccccc, 0xbfbfbf,
                snowballChicken, sandChicken);
        chickens.add(clayChicken);

        ChickensRegistryItem leatherChicken = new ChickensRegistryItem(
                new ResourceLocation(ChickensMod.MODID, "LeatherChicken"), "LeatherChicken", new ResourceLocation("chickens", "textures/entity/leather_chicken.png"),
                new ItemStack(Items.LEATHER),
                0xA7A06C, 0x919191,
                stringChicken, brownChicken);
        chickens.add(leatherChicken);

        ChickensRegistryItem netherwartChicken = new ChickensRegistryItem(
                new ResourceLocation(ChickensMod.MODID, "NetherwartChicken"), "NetherwartChicken", new ResourceLocation("chickens", "textures/entity/netherwart_chicken.png"),
                new ItemStack(Items.NETHER_WART),
                0x800000, 0x331a00,
                brownChicken, glowstoneChicken);
        chickens.add(netherwartChicken);

        // Tier 4
        ChickensRegistryItem diamondChicken = new ChickensRegistryItem(
                new ResourceLocation(ChickensMod.MODID, "DiamondChicken"), "DiamondChicken", new ResourceLocation("chickens", "textures/entity/diamond_chicken.png"),
                new ItemStack(Items.DIAMOND),
                0x99ccff, 0xe6f2ff,
                glassChicken, goldChicken);
        chickens.add(diamondChicken);

        ChickensRegistryItem blazeChicken = new ChickensRegistryItem(
                new ResourceLocation(ChickensMod.MODID, "BlazeChicken"), "BlazeChicken", new ResourceLocation("chickens", "textures/entity/blaze_chicken.png"),
                new ItemStack(Items.BLAZE_ROD),
                0xffff66, 0xff3300,
                goldChicken, lavaChicken);
        chickens.add(blazeChicken);

        ChickensRegistryItem slimeChicken = new ChickensRegistryItem(
                new ResourceLocation(ChickensMod.MODID, "SlimeChicken"), "SlimeChicken", new ResourceLocation("chickens", "textures/entity/slime_chicken.png"),
                new ItemStack(Items.SLIME_BALL),
                0x009933, 0x99ffbb,
                clayChicken, greenChicken);
        chickens.add(slimeChicken);

        // Tier 5
        ChickensRegistryItem enderChicken = new ChickensRegistryItem(
                new ResourceLocation(ChickensMod.MODID, "EnderChicken"), "EnderChicken", new ResourceLocation("chickens", "textures/entity/ender_chicken.png"),
                new ItemStack(Items.ENDER_PEARL),
                0x001a00, 0x001a33,
                diamondChicken, netherwartChicken);
        chickens.add(enderChicken);

        ChickensRegistryItem ghastChicken = new ChickensRegistryItem(
                new ResourceLocation(ChickensMod.MODID, "GhastChicken"), "GhastChicken", new ResourceLocation("chickens", "textures/entity/ghast_chicken.png"),
                new ItemStack(Items.GHAST_TEAR),
                0xffffcc, 0xffffff,
                whiteChicken, blazeChicken);
        chickens.add(ghastChicken);

        ChickensRegistryItem emeraldChicken = new ChickensRegistryItem(
                new ResourceLocation(ChickensMod.MODID, "EmeraldChicken"), "EmeraldChicken", new ResourceLocation("chickens", "textures/entity/emerald_chicken.png"),
                new ItemStack(Items.EMERALD),
                0x00cc00, 0x003300,
                diamondChicken, greenChicken);
        chickens.add(emeraldChicken);

        ChickensRegistryItem magmaChicken = new ChickensRegistryItem(
                new ResourceLocation(ChickensMod.MODID, "MagmaChicken"), "MagmaChicken", new ResourceLocation("chickens", "textures/entity/magma_chicken.png"),
                new ItemStack(Items.MAGMA_CREAM),
                0x1a0500, 0x000000,
                slimeChicken, blazeChicken);
        chickens.add(magmaChicken);

        ChickensRegistryItem pShardChicken = new ChickensRegistryItem(
                new ResourceLocation(ChickensMod.MODID, "pShardChicken"), "pShardChicken", new ResourceLocation("chickens", "textures/entity/pshard_chicken.png"),
                new ItemStack(Items.PRISMARINE_SHARD),
                0x43806e, 0x9fcbbc,
                waterChicken, blueChicken);
        chickens.add(pShardChicken);
        
        ChickensRegistryItem pCrystalChicken = new ChickensRegistryItem(
                new ResourceLocation(ChickensMod.MODID, "pCrystalChicken"), "pCrystalChicken", new ResourceLocation("chickens", "textures/entity/pcrystal_chicken.png"),
                new ItemStack(Items.PRISMARINE_CRYSTALS, 1 , 0),
                0x4e6961, 0xdfe9dc,
                waterChicken, emeraldChicken);
        chickens.add(pCrystalChicken);
        
        ChickensRegistryItem obsidianChicken = new ChickensRegistryItem(
                new ResourceLocation(ChickensMod.MODID, "obsidianChicken"), "obsidianChicken", new ResourceLocation("chickens", "textures/entity/obsidian_chicken.png"),
                new ItemStack(Blocks.OBSIDIAN, 1 , 0),
                0x08080e, 0x463a60,
                waterChicken, lavaChicken);
        chickens.add(obsidianChicken);
        
        ChickensRegistryItem soulSandChicken = new ChickensRegistryItem(
                new ResourceLocation(ChickensMod.MODID, "soulSandChicken"), "soulSandChicken", new ResourceLocation("chickens", "textures/entity/soulsand_chicken.png"),
                new ItemStack(Blocks.SOUL_SAND, 1 , 0),
                0x453125, 0xd52f08).setSpawnType(SpawnType.HELL);
        chickens.add(soulSandChicken);
        
        return chickens;

    }

    public static String getItemName(Item item) {
        return item.getUnlocalizedName().substring(5);
    }

    public static String getBlockName(Block block) {
        return block.getUnlocalizedName().substring(5);
    }
}
