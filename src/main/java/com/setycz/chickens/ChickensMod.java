package com.setycz.chickens;

import com.setycz.chickens.chicken.ChickenNetherPopulateHandler;
import com.setycz.chickens.chicken.EntityChickensChicken;
import com.setycz.chickens.coloredEgg.ItemColoredEgg;
import com.setycz.chickens.henhouse.BlockHenhouse;
import com.setycz.chickens.henhouse.TileEntityHenhouse;
import com.setycz.chickens.liquidEgg.ItemLiquidEgg;
import com.setycz.chickens.spawnEgg.ItemSpawnEgg;
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
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by setyc on 12.02.2016.
 */
@Mod(modid = ChickensMod.MODID,
        version = ChickensMod.VERSION,
        acceptedMinecraftVersions = "[1.9.4]",
        dependencies = "required-after:Forge@[12.16.1.1887,);")
public class ChickensMod {
    public static final String MODID = "chickens";
    public static final String VERSION = "@VERSION@";
    public static final String CHICKEN = "ChickensChicken";

    public static final Logger log = LogManager.getLogger(MODID);

    @Mod.Instance(MODID)
    public static ChickensMod instance;

    private static final CreativeTabs tab = new ChickensTab("chickens");

    private int chickenEntityId = 0;

    public static final Item spawnEgg = new ItemSpawnEgg().setRegistryName("spawn_egg").setUnlocalizedName("spawn_egg").setCreativeTab(tab);
    public static final Item coloredEgg = new ItemColoredEgg().setRegistryName("colored_egg").setUnlocalizedName("colored_egg").setCreativeTab(tab);
    public static final Item liquidEgg = new ItemLiquidEgg().setRegistryName("liquid_egg").setUnlocalizedName("liquid_egg").setCreativeTab(tab);

    public static final Block henhouse = new BlockHenhouse().setRegistryName("henhouse").setUnlocalizedName("henhouse").setCreativeTab(tab);
    public static final Block henhouse_acacia = new BlockHenhouse().setRegistryName("henhouse_acacia").setUnlocalizedName("henhouse_acacia").setCreativeTab(tab);
    public static final Block henhouse_birch = new BlockHenhouse().setRegistryName("henhouse_birch").setUnlocalizedName("henhouse_birch").setCreativeTab(tab);
    public static final Block henhouse_dark_oak = new BlockHenhouse().setRegistryName("henhouse_dark_oak").setUnlocalizedName("henhouse_dark_oak").setCreativeTab(tab);
    public static final Block henhouse_jungle = new BlockHenhouse().setRegistryName("henhouse_jungle").setUnlocalizedName("henhouse_jungle").setCreativeTab(tab);
    public static final Block henhouse_spruce = new BlockHenhouse().setRegistryName("henhouse_spruce").setUnlocalizedName("henhouse_spruce").setCreativeTab(tab);

    public static TileEntityGuiHandler guiHandler = new TileEntityGuiHandler();

    @SidedProxy(clientSide = "com.setycz.chickens.ClientProxy", serverSide = "com.setycz.chickens.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, guiHandler);

        EntityRegistry.registerModEntity(EntityChickensChicken.class, CHICKEN, chickenEntityId, this, 64, 3, true);

        GameRegistry.register(coloredEgg);
        GameRegistry.register(spawnEgg);

        GameRegistry.register(liquidEgg);

        GameRegistry.registerTileEntity(TileEntityHenhouse.class, "henhouse");
        registerBlock(henhouse);
        registerBlock(henhouse_acacia);
        registerBlock(henhouse_birch);
        registerBlock(henhouse_dark_oak);
        registerBlock(henhouse_jungle);
        registerBlock(henhouse_spruce);

        registerLiquidEggs();
        loadConfiguration(event.getSuggestedConfigurationFile());

        log.info("Enabled chickens: {}", getChickenNames(ChickensRegistry.getItems()));
        log.info("Disabled chickens: {}", getChickenNames(ChickensRegistry.getDisabledItems()));
        for (SpawnType spawnType : SpawnType.values()) {
            log.info("[{}] biome type will spawn {} ({})",
                    spawnType, getChickenNames(ChickensRegistry.getPossibleChickensToSpawn(spawnType)));
        }

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

    private void loadConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        chickenEntityId = configuration.getInt("entityId", "general", 30000, Integer.MIN_VALUE, Integer.MAX_VALUE, "Chicken Entity ID");

        Collection<ChickensRegistryItem> allChickens = generateDefaultChickens();
        for (ChickensRegistryItem chicken : allChickens) {
            boolean enabled = configuration.getBoolean("enabled", chicken.getEntityName(), true, "Is chicken enabled?");
            chicken.setEnabled(enabled);

            float layCoefficient = configuration.getFloat("layCoefficient", chicken.getEntityName(), 1.0f, 0.01f, 100.f, "Scale time to lay an egg.");
            chicken.setLayCoefficient(layCoefficient);

            ItemStack itemStack = loadItemStack(configuration, chicken, "egg", chicken.createLayItem());
            chicken.setLayItem(itemStack);

            ItemStack dropItemStack = loadItemStack(configuration, chicken, "drop", chicken.createDropItem());
            chicken.setDropItem(dropItemStack);

            ChickensRegistryItem parent1 = getChickenParent(configuration, "parent1", allChickens, chicken, chicken.getParent1());
            ChickensRegistryItem parent2 = getChickenParent(configuration, "parent2", allChickens, chicken, chicken.getParent2());
            if (parent1 != null && parent2 != null) {
                chicken.setParents(parent1, parent2);
            } else {
                chicken.setNoParents();
            }

            SpawnType spawnType = SpawnType.valueOf(configuration.getString("spawnType", chicken.getEntityName(), chicken.getSpawnType().toString(), "Chicken spawn type, can be: " + String.join(",", SpawnType.names())));
            chicken.setSpawnType(spawnType);

            ChickensRegistry.register(chicken);
        }

        configuration.save();
    }

    private ChickensRegistryItem getChickenParent(Configuration configuration, String propertyName, Collection<ChickensRegistryItem> allChickens, ChickensRegistryItem chicken, ChickensRegistryItem parent) {
        String parentName = configuration.getString(propertyName, chicken.getEntityName(), parent != null ? parent.getEntityName() : "", "First parent, empty if it's base chicken.");
        return findChicken(allChickens, parentName);
    }

    private ChickensRegistryItem findChicken(Collection<ChickensRegistryItem> chickens, String name) {
        for (ChickensRegistryItem chicken : chickens) {
            if (chicken.getEntityName().compareToIgnoreCase(name) == 0) {
                return chicken;
            }
        }
        return null;
    }

    private ItemStack loadItemStack(Configuration configuration, ChickensRegistryItem chicken, String prefix, ItemStack defaultItemStack) {
        String itemName = configuration.getString(prefix + "ItemName", chicken.getEntityName(), defaultItemStack.getItem().getRegistryName().toString(), "Item name to be laid/dropped.");
        int itemAmount = configuration.getInt(prefix + "ItemAmount", chicken.getEntityName(), defaultItemStack.stackSize, 1, 64, "Item amount to be laid/dropped.");
        int itemMeta = configuration.getInt(prefix + "ItemMeta", chicken.getEntityName(), defaultItemStack.getMetadata(), Integer.MIN_VALUE, Integer.MAX_VALUE, "Item amount to be laid/dropped.");

        ResourceLocation itemResourceLocation = new ResourceLocation(itemName);
        Item item = Item.REGISTRY.getObject(itemResourceLocation);
        if (item == null) {
            throw new RuntimeException("Cannot find egg item with name: " + itemName);
        }
        return new ItemStack(item, itemAmount, itemMeta);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();

        MinecraftForge.EVENT_BUS.register(new ChickenTeachHanhler());

        List<Biome> biomesForSpawning = getAllSpawnBiomes();
        if (biomesForSpawning.size() > 0) {
            EntityRegistry.addSpawn(EntityChickensChicken.class, 10, 3, 5, EnumCreatureType.CREATURE,
                    biomesForSpawning.toArray(new Biome[biomesForSpawning.size()])
            );
            if (biomesForSpawning.contains(Biomes.HELL)) {
                MinecraftForge.TERRAIN_GEN_BUS.register(new ChickenNetherPopulateHandler());
            }
        }

        // register all chickens to Minecraft
        for (ChickensRegistryItem chicken : ChickensRegistry.getItems()) {
            proxy.registerChicken(chicken);
        }

        for (LiquidEggRegistryItem liquidEgg : LiquidEggRegistry.getAll()) {
            proxy.registerLiquidEgg(liquidEgg);
        }

        registerHenhouse(henhouse_acacia, BlockPlanks.EnumType.ACACIA);
        registerHenhouse(henhouse_birch, BlockPlanks.EnumType.BIRCH);
        registerHenhouse(henhouse_dark_oak, BlockPlanks.EnumType.DARK_OAK);
        registerHenhouse(henhouse_jungle, BlockPlanks.EnumType.JUNGLE);
        registerHenhouse(henhouse_spruce, BlockPlanks.EnumType.SPRUCE);
        registerHenhouse(henhouse, BlockPlanks.EnumType.OAK);

        // waila integration
        FMLInterModComms.sendMessage("Waila", "register", "com.setycz.chickens.waila.ChickensEntityProvider.load");
    }

    private boolean requiresWisitingNether(ChickensRegistryItem chicken) {
        return chicken.getTier() == 1
                ? chicken.getSpawnType() == SpawnType.HELL
                : requiresWisitingNether(chicken.getParent1()) || requiresWisitingNether(chicken.getParent2());
    }

    private void dumpChickens(Collection<ChickensRegistryItem> items) {
        try {
            FileWriter file = new FileWriter("logs/chickens.gml");
            file.write("graph [\n");
            file.write("\tdirected 1\n");
            for (ChickensRegistryItem item : items) {
                file.write("\tnode [\n");
                file.write("\t\tid " + item.getId() + "\n");
                file.write("\t\tlabel \"" + item.getEntityName() + "\"\n");
                if (requiresWisitingNether(item)) {
                    file.write("\t\tgraphics [\n");
                    file.write("\t\t\tfill \"#FF6600\"\n");
                    file.write("\t\t]\n");
                }
                file.write("\t]\n");
            }
            for (ChickensRegistryItem item : items) {
                if (item.getParent1() != null) {
                    file.write("\tedge [\n");
                    file.write("\t\tsource " + item.getParent1().getId() + "\n");
                    file.write("\t\ttarget " + item.getId() + "\n");
                    file.write("\t]\n");
                }
                if (item.getParent2() != null) {
                    file.write("\tedge [\n");
                    file.write("\t\tsource " + item.getParent2().getId() + "\n");
                    file.write("\t\ttarget " + item.getId() + "\n");
                    file.write("\t]\n");
                }
            }
            file.write("]\n");
            file.close();
        } catch (IOException ignored) {
        }
    }

    private void registerHenhouse(Block henhouse, BlockPlanks.EnumType type) {
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
        LiquidEggRegistry.register(new LiquidEggRegistryItem(0, Blocks.FLOWING_WATER, 0x0000ff));
        LiquidEggRegistry.register(new LiquidEggRegistryItem(1, Blocks.FLOWING_LAVA, 0xff0000));
    }

    private List<ChickensRegistryItem> generateDefaultChickens() {
        List<ChickensRegistryItem> chickens = new ArrayList<ChickensRegistryItem>();

        chickens.add(new ChickensRegistryItem(
                ChickensRegistry.SMART_CHICKEN_ID, "SmartChicken", new ResourceLocation("chickens", "textures/entity/SmartChicken.png"),
                new ItemStack(Items.EGG),
                0xffffff, 0xffff00).setSpawnType(SpawnType.NONE));

        // dye chickens
        ChickensRegistryItem yellowChicken = new ChickensRegistryItem(
                4, "YellowChicken", new ResourceLocation("chickens", "textures/entity/YellowChicken.png"),
                new ItemStack(Items.DYE, 1, EnumDyeColor.YELLOW.getDyeDamage()),
                0xffff00, 0xcccc00).setSpawnType(SpawnType.NONE);
        chickens.add(yellowChicken);

        ChickensRegistryItem blueChicken = new ChickensRegistryItem(
                11, "BlueChicken", new ResourceLocation("chickens", "textures/entity/BlueChicken.png"),
                new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()),
                0x000066, 0x000033).setSpawnType(SpawnType.NONE);
        chickens.add(blueChicken);

        ChickensRegistryItem greenChicken = new ChickensRegistryItem(
                13, "GreenChicken", new ResourceLocation("chickens", "textures/entity/GreenChicken.png"),
                new ItemStack(Items.DYE, 1, EnumDyeColor.GREEN.getDyeDamage()),
                0x006600, 0x003300).setSpawnType(SpawnType.NONE);
        chickens.add(greenChicken);

        ChickensRegistryItem redChicken = new ChickensRegistryItem(
                14, "RedChicken", new ResourceLocation("chickens", "textures/entity/RedChicken.png"),
                new ItemStack(Items.DYE, 1, EnumDyeColor.RED.getDyeDamage()),
                0x660000, 0x330000).setSpawnType(SpawnType.NONE);
        chickens.add(redChicken);

        ChickensRegistryItem blackChicken = new ChickensRegistryItem(
                15, "BlackChicken", new ResourceLocation("chickens", "textures/entity/BlackChicken.png"),
                new ItemStack(Items.DYE, 1, EnumDyeColor.BLACK.getDyeDamage()),
                0x666666, 0x333333).setSpawnType(SpawnType.NONE);
        chickens.add(blackChicken);

        // base chickens
        ChickensRegistryItem flintChicken = new ChickensRegistryItem(
                101, "FlintChicken", new ResourceLocation("chickens", "textures/entity/FlintChicken.png"),
                new ItemStack(Items.FLINT),
                0x6b6b47, 0xa3a375);
        chickens.add(flintChicken);

        ChickensRegistryItem quartzChicken = new ChickensRegistryItem(
                104, "QuartzChicken", new ResourceLocation("chickens", "textures/entity/QuartzChicken.png"),
                new ItemStack(Items.QUARTZ),
                0x4d0000, 0x1a0000).setSpawnType(SpawnType.HELL);
        chickens.add(quartzChicken);


        ChickensRegistryItem logChicken = new ChickensRegistryItem(
                108, "LogChicken", new ResourceLocation("chickens", "textures/entity/LogChicken.png"),
                new ItemStack(Blocks.LOG),
                0x98846d, 0x528358);
        chickens.add(logChicken);

        ChickensRegistryItem sandChicken = new ChickensRegistryItem(
                105, "SandChicken", new ResourceLocation("chickens", "textures/entity/SandChicken.png"),
                new ItemStack(Blocks.SAND),
                0xece5b1, 0xa7a06c);
        chickens.add(sandChicken);

        ChickensRegistryItem whiteChicken = new ChickensRegistryItem(
                0, "WhiteChicken", new ResourceLocation("chickens", "textures/entity/WhiteChicken.png"),
                new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage()),
                0xf2f2f2, 0xffffff).setDropItem(new ItemStack(Items.BONE));
        chickens.add(whiteChicken);

        // Tier 2
        ChickensRegistryItem stringChicken = new ChickensRegistryItem(
                303, "StringChicken", new ResourceLocation("chickens", "textures/entity/StringChicken.png"),
                new ItemStack(Items.STRING),
                0x331a00, 0x800000,
                blackChicken, logChicken
        ).setDropItem(new ItemStack(Items.SPIDER_EYE));
        chickens.add(stringChicken);

        ChickensRegistryItem glowstoneChicken = new ChickensRegistryItem(
                202, "GlowstoneChicken", new ResourceLocation("chickens", "textures/entity/GlowstoneChicken.png"),
                new ItemStack(Items.GLOWSTONE_DUST),
                0xffff66, 0xffff00,
                quartzChicken, yellowChicken);
        chickens.add(glowstoneChicken);

        ChickensRegistryItem gunpowderChicken = new ChickensRegistryItem(
                100, "GunpowderChicken", new ResourceLocation("chickens", "textures/entity/GunpowderChicken.png"),
                new ItemStack(Items.GUNPOWDER),
                0x999999, 0x404040,
                sandChicken, flintChicken);
        chickens.add(gunpowderChicken);

        ChickensRegistryItem redstoneChicken = new ChickensRegistryItem(
                201, "RedstoneChicken", new ResourceLocation("chickens", "textures/entity/RedstoneChicken.png"),
                new ItemStack(Items.REDSTONE),
                0xe60000, 0x800000,
                redChicken,
                sandChicken);
        chickens.add(redstoneChicken);

        ChickensRegistryItem glassChicken = new ChickensRegistryItem(
                106, "GlassChicken", new ResourceLocation("chickens", "textures/entity/GlassChicken.png"),
                new ItemStack(Blocks.GLASS),
                0xffffff, 0xeeeeff,
                quartzChicken, redstoneChicken);
        chickens.add(glassChicken);

        ChickensRegistryItem ironChicken = new ChickensRegistryItem(
                203, "IronChicken", new ResourceLocation("chickens", "textures/entity/IronChicken.png"),
                new ItemStack(Items.IRON_INGOT),
                0xffffcc, 0xffcccc,
                flintChicken, whiteChicken);
        chickens.add(ironChicken);

        ChickensRegistryItem coalChicken = new ChickensRegistryItem(
                204, "CoalChicken", new ResourceLocation("chickens", "textures/entity/CoalChicken.png"),
                new ItemStack(Items.COAL),
                0x262626, 0x000000,
                flintChicken, logChicken);
        chickens.add(coalChicken);

        ChickensRegistryItem brownChicken = new ChickensRegistryItem(
                12, "BrownChicken", new ResourceLocation("chickens", "textures/entity/BrownChicken.png"),
                new ItemStack(Items.DYE, 1, EnumDyeColor.BROWN.getDyeDamage()),
                0x663300, 0x1a0d00,
                redChicken, greenChicken).setSpawnType(SpawnType.NONE);
        chickens.add(brownChicken);

        // tier 3
        ChickensRegistryItem goldChicken = new ChickensRegistryItem(
                300, "GoldChicken", new ResourceLocation("chickens", "textures/entity/GoldChicken.png"),
                new ItemStack(Items.GOLD_NUGGET),
                0xcccc00, 0xffff80,
                ironChicken, yellowChicken);
        chickens.add(goldChicken);

        ChickensRegistryItem snowballChicken = new ChickensRegistryItem(
                102, "SnowballChicken", new ResourceLocation("chickens", "textures/entity/SnowballChicken.png"),
                new ItemStack(Items.SNOWBALL),
                0x33bbff, 0x0088cc,
                blueChicken, logChicken).setSpawnType(SpawnType.SNOW);
        chickens.add(snowballChicken);

        ChickensRegistryItem waterChicken = new ChickensRegistryItem(
                206, "WaterChicken", new ResourceLocation("chickens", "textures/entity/WaterChicken.png"),
                new ItemStack(liquidEgg, 1, 0),
                0x000099, 0x8080ff,
                gunpowderChicken, snowballChicken);
        chickens.add(waterChicken);

        ChickensRegistryItem lavaChicken = new ChickensRegistryItem(
                103, "LavaChicken", new ResourceLocation("chickens", "textures/entity/LavaChicken.png"),
                new ItemStack(liquidEgg, 1, 1),
                0xcc3300, 0xffff00,
                coalChicken, quartzChicken).setSpawnType(SpawnType.HELL);
        chickens.add(lavaChicken);

        ChickensRegistryItem clayChicken = new ChickensRegistryItem(
                200, "ClayChicken", new ResourceLocation("chickens", "textures/entity/ClayChicken.png"),
                new ItemStack(Items.CLAY_BALL),
                0xcccccc, 0xbfbfbf,
                snowballChicken, sandChicken);
        chickens.add(clayChicken);

        ChickensRegistryItem leatherChicken = new ChickensRegistryItem(
                107, "LeatherChicken", new ResourceLocation("chickens", "textures/entity/LeatherChicken.png"),
                new ItemStack(Items.LEATHER),
                0xA7A06C, 0x919191,
                stringChicken, brownChicken);
        chickens.add(leatherChicken);

        ChickensRegistryItem netherwartChicken = new ChickensRegistryItem(
                207, "NetherwartChicken", new ResourceLocation("chickens", "textures/entity/NetherwartChicken.png"),
                new ItemStack(Items.NETHER_WART),
                0x800000, 0x331a00,
                brownChicken, glowstoneChicken);
        chickens.add(netherwartChicken);

        // Tier 4
        ChickensRegistryItem diamondChicken = new ChickensRegistryItem(
                301, "DiamondChicken", new ResourceLocation("chickens", "textures/entity/DiamondChicken.png"),
                new ItemStack(Items.DIAMOND),
                0x99ccff, 0xe6f2ff,
                glassChicken, goldChicken);
        chickens.add(diamondChicken);

        ChickensRegistryItem blazeChicken = new ChickensRegistryItem(
                302, "BlazeChicken", new ResourceLocation("chickens", "textures/entity/BlazeChicken.png"),
                new ItemStack(Items.BLAZE_ROD),
                0xffff66, 0xff3300,
                goldChicken, lavaChicken);
        chickens.add(blazeChicken);

        ChickensRegistryItem slimeChicken = new ChickensRegistryItem(
                205, "SlimeChicken", new ResourceLocation("chickens", "textures/entity/SlimeChicken.png"),
                new ItemStack(Items.SLIME_BALL),
                0x009933, 0x99ffbb,
                clayChicken, greenChicken);
        chickens.add(slimeChicken);

        // Tier 5
        ChickensRegistryItem enderChicken = new ChickensRegistryItem(
                401, "EnderChicken", new ResourceLocation("chickens", "textures/entity/EnderChicken.png"),
                new ItemStack(Items.ENDER_PEARL),
                0x001a00, 0x001a33,
                diamondChicken, netherwartChicken);
        chickens.add(enderChicken);

        ChickensRegistryItem ghastChicken = new ChickensRegistryItem(
                402, "GhastChicken", new ResourceLocation("chickens", "textures/entity/GhastChicken.png"),
                new ItemStack(Items.GHAST_TEAR),
                0xffffcc, 0xffffff,
                whiteChicken, blazeChicken);
        chickens.add(ghastChicken);

        ChickensRegistryItem emeraldChicken = new ChickensRegistryItem(
                400, "EmeraldChicken", new ResourceLocation("chickens", "textures/entity/EmeraldChicken.png"),
                new ItemStack(Items.EMERALD),
                0x00cc00, 0x003300,
                diamondChicken, greenChicken);
        chickens.add(emeraldChicken);

        ChickensRegistryItem magmaChicken = new ChickensRegistryItem(
                403, "MagmaChicken", new ResourceLocation("chickens", "textures/entity/MagmaChicken.png"),
                new ItemStack(Items.MAGMA_CREAM),
                0x1a0500, 0x000000,
                slimeChicken, blazeChicken);
        chickens.add(magmaChicken);

        return chickens;

    }

    public static String getItemName(Item item) {
        return item.getUnlocalizedName().substring(5);
    }

    public static String getBlockName(Block block) {
        return block.getUnlocalizedName().substring(5);
    }
}
