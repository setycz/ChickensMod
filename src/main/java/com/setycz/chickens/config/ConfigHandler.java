package com.setycz.chickens.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.gson.JsonObject;
import com.setycz.chickens.api.properties.ItemHolder;
import com.setycz.chickens.api.registry.ChickensRegistry;
import com.setycz.chickens.api.registry.ChickensRegistryItem;
import com.setycz.chickens.registry.BiomeSpawnItem;
import com.setycz.chickens.registry.SpawnRegistry;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ConfigHandler {
	public static final File configDir = new File("config/chickens");
	public static final File ChickensMainFile = new File(configDir, "main_chickens.cfg");
	public static final File ChickensFile = new File(configDir, "chickens.json");
	public static final File CustomBiomeDir = new File(configDir, "customBiomes");
	
	private static JsonConfig config;
	
	public static ArrayList<String> ErrorList = new ArrayList<String>();
	
    public static int chickenEntityId = 30000;
    public static int spawnProbability = 10;
    public static int minBroodSize = 3;
    public static int maxBroodSize = 5;
    public static float netherSpawnChanceMultiplier = 1.0f;
    public static boolean alwaysShowStats = false;

	
    public static void LoadConfigs(List<ChickensRegistryItem> allchickens) {
    	loadConfiguration();
    	loadChickens(allchickens);
    }
    
    public static void initLoadConfigs() {
    	loadBiomeListFromDir(CustomBiomeDir);    	
    }
    
    
	/**
	 * Loads main configuration file. 
	 * 
	 * @param configFile
	 */
	private static void loadConfiguration() {
	     Configuration mainConfig = new Configuration(ChickensMainFile);
	     	mainConfig.load();

	     	chickenEntityId = mainConfig.getInt("entityId", "general", 30000, Integer.MIN_VALUE, Integer.MAX_VALUE, "Chicken Entity ID");
	        spawnProbability = mainConfig.getInt("spawnProbability", "general", 10, Integer.MIN_VALUE, Integer.MAX_VALUE, "Spawn probability");
	        minBroodSize = mainConfig.getInt("minBroodSize", "general", 3, 1, Integer.MAX_VALUE, "Minimal brood size");
	        maxBroodSize = mainConfig.getInt("maxBroodSize", "general", 5, 2, Integer.MAX_VALUE, "Maximal brood size, must be greater than the minimal size");
	        netherSpawnChanceMultiplier = mainConfig.getFloat("netherSpawnChanceMultiplier", "general", 1.0f, 0.f, Float.MAX_VALUE, "Nether chicken spawn chance multiplier, e.g. 0=no initial spawn, 2=two times more spawn rate");
	        alwaysShowStats = mainConfig.getBoolean("alwaysShowStats", "general", false, "Stats will be always shown in WAILA without the need to analyze chickens first when enabled.");

			if (mainConfig.hasChanged()) {
	        mainConfig.save();
			}
	}
	
	
	private static File Normal = new File(CustomBiomeDir, "Normal.json");
	private static File Hell = new File(CustomBiomeDir, "Nether.json");
	private static File Snow = new File(CustomBiomeDir, "Snow.json");
	private static File All = new File(CustomBiomeDir, "All.txt");
	
	
	/**
	 * Load Custom Biome files
	 */
	public static void loadBiomeListFromDir(File directory){
		if(!directory.isDirectory()) 
			directory.mkdirs();
		
		 File[] files = directory.listFiles();
		 
		 if(!Normal.exists() || !Hell.exists() || !Snow.exists() || !All.exists())
			 createSpawnDefaults();
		 
		 for(File file : files) {
			 if(file.getPath().toLowerCase().endsWith(".json")) {
				 loadBiomesFromFile(file);
			 }
		 }
		
	}

	
	public static void loadBiomesFromFile(File fileIn) {
		
		config = new JsonConfig(fileIn);
		config.Load();
		String cat = "settings";
		ArrayList<String> biomes = config.getStringList(cat, "biome_ids", new ArrayList<String>());
		
		BiomeSpawnItem customSpawn = new BiomeSpawnItem(biomes);
		SpawnRegistry.registerBiomeSpawns(fileIn.getName().replace(".json","").toLowerCase(), customSpawn);
        if(config.hasChanged) {
        	config.Save();
        }
	}
	

	public static void createSpawnDefaults() {
		
		// Get Biomes
		ArrayList<String> Normal_biomes = new ArrayList<String>();
		ArrayList<String> Nether_biomes = new ArrayList<String>();
		ArrayList<String> Snow_biomes = new ArrayList<String>();
		
		for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
			if (!BiomeDictionary.hasType(biome, BiomeDictionary.Type.END)
					&& (!BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER))
					&& (!BiomeDictionary.hasType(biome, BiomeDictionary.Type.BEACH))
					&& (!BiomeDictionary.hasType(biome, BiomeDictionary.Type.COLD))
					&& (!BiomeDictionary.hasType(biome, BiomeDictionary.Type.SNOWY))
					&& (!BiomeDictionary.hasType(biome, BiomeDictionary.Type.BEACH))
					&& (!BiomeDictionary.hasType(biome, BiomeDictionary.Type.MUSHROOM))
					&& (!BiomeDictionary.hasType(biome, BiomeDictionary.Type.SWAMP))
					&& (!BiomeDictionary.hasType(biome, BiomeDictionary.Type.VOID))
					&& (!BiomeDictionary.hasType(biome, BiomeDictionary.Type.SANDY))
					&& (!BiomeDictionary.hasType(biome, BiomeDictionary.Type.MAGICAL))
					&& (!BiomeDictionary.hasType(biome, BiomeDictionary.Type.WATER))) {
				Normal_biomes.add(biome.getRegistryName().toString());
			}else if(BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER)) {
				Nether_biomes.add(biome.getRegistryName().toString());
			}else if(BiomeDictionary.hasType(biome, BiomeDictionary.Type.COLD) 
					|| BiomeDictionary.hasType(biome, BiomeDictionary.Type.SNOWY)) {
				Snow_biomes.add(biome.getRegistryName().toString());
			}
		}
		
		if(!Normal.exists())
			createBiomeFile(Normal, Normal_biomes);

		if(!Hell.exists())
			createBiomeFile(Hell, Nether_biomes);

		if(!Snow.exists())
			createBiomeFile(Snow, Snow_biomes);

		if(!All.exists()) {
			ArrayList<String> allbiomes = new ArrayList<String>();
			ForgeRegistries.BIOMES.getKeys().forEach((n) -> allbiomes.add(n.toString()));
			createBiomeFile(All, allbiomes);
		}
	}
	
	private static void createBiomeFile(File name, ArrayList<String> biomes) {
		config = new JsonConfig(name);
		config.Load();
		
		if(name.equals(All)) {
			String comment = "_comments";
			int i = 1;
			config.getString(comment, "_comment"+i++, "This folder allows you to create special biome spawning rules. You can add any biome to the list.");
			config.getString(comment, "_comment"+i++, "Use one of the default json files as an example. This file contains all registered biomes. Delete this file to regen it, if you have added more biomes to the game.");
			config.getString(comment, "_comment"+i++, "This currently is a whitelist only for now.");
			config.getString(comment, "_comment"+i++, "You can create any number of custom spawn rules. And can add it to any chicken in the chicken json file.");
			config.getString(comment, "_comment"+i++, "best to keep file name simple. ie. 'Normal.json' inputed into chicken spawn type is just: 'normal' ");
			config.getString(comment, "_comment"+i++, "no limit to the amount of files you can add in your spawnrules folder. ");
			config.getString(comment, "_comment"+i++, "You can not delete the Normal.json, Snow.json, Nether.json, All.txt. As this could break things.");			
		}
		
		String cat = "settings";
		config.getStringList(cat, "biome_ids", biomes);
		config.Save();
	}
	
	
	/**
	 * Load json file of all Chickens. 
	 * 
	 * @param allChickens
	 */
	public static void loadChickensFromFile(File fileIn, Collection<ChickensRegistryItem> allChickens) {
		
		config = new JsonConfig(fileIn);
		config.Load();
		
		// Add Comments
			String comment = "_comment";
			config.getString(comment, "name", "Just a Reference to the old system naming. Changing does nothing.");
			config.getString(comment, "is_enabled", "Is chicken enabled?");
			config.getString(comment, "lay_item", "Item the chicken will Lay. Changing the qty will double that amount on each gain bonus. ");
			config.getFullJson().get(comment).getAsJsonObject().add("lay_item_example", new ItemHolder(new ItemStack(Items.GOLD_INGOT), true).writeJsonObject(new JsonObject()));
			config.getString(comment, "drop_item", "Item the chicken will Lay. Changing the qty will double that amount on each gain bonus. ");
			config.getFullJson().get(comment).getAsJsonObject().add("drop_item_example", new ItemHolder(new ItemStack(Items.BONE, 2).setStackDisplayName("Bone of my Enemy"), true).writeJsonObject(new JsonObject()));
			config.getString(comment, "spawn_type", "Chicken spawn type is the name of the file in your custom biomes folder. Which gives what biomes it can spawn in.");
			config.getString(comment, "parent_1", "First parent, empty if it cant be breed. modid:chickenid #example: chickens:waterchicken");
			config.getString(comment, "parent_2", "Second parent, empty if it cant be breed. ");
		
        for (ChickensRegistryItem chicken : allChickens) {
        	
        	String registryName = chicken.getRegistryName().toString();
        	
        	config.getString(registryName, "name", chicken.getEntityName());

        	boolean enabled = config.getBoolean(registryName, "is_enabled", true);
        	chicken.setEnabled(enabled);
        	
        	float layCoefficient = config.getFloat(registryName, "lay_coefficient", 1.0f, 0.01f, 100f);
        	chicken.setLayCoefficient(layCoefficient);
        	
        	chicken.setLayItem(loadItemStack(config, registryName, chicken, "lay_item", chicken.getLayItemHolder().setSource(registryName)));
        	chicken.setDropItem(loadItemStack(config, registryName, chicken, "drop_item", chicken.getDropItemHolder().setSource(registryName)));
        	
            String spawnType = config.getString(registryName, "spawn_type", chicken.getSpawnType().toString());
            chicken.setSpawnType(legacySpawn(spawnType).toLowerCase());

            ChickensRegistry.register(chicken);

        }
        
        // Set Parents after Chickens have been registered
        for (ChickensRegistryItem chicken : allChickens) {
        	
        	ChickensRegistryItem parent1 = ChickensRegistry.getByRegistryName(getChickenParent(config, "parent_1", allChickens, chicken, chicken.getParent1()));
        	ChickensRegistryItem parent2 = ChickensRegistry.getByRegistryName(getChickenParent(config, "parent_2", allChickens, chicken, chicken.getParent2()));
            
        	if (parent1 != null && parent2 != null) {
        		chicken.setParentsNew(parent1, parent2);
        	} else {
        		chicken.setNoParents();
        	}
        }
		
        if(config.hasChanged) {
        	config.Save();
        }
	}

	// Renames old spawn names. 
	private static String legacySpawn(String var1) {
		if(var1.equals("HELL"))
			var1 = "nether";
		return var1;
	}
	
	public static void loadChickens(Collection<ChickensRegistryItem> allChickens) {
		loadChickensFromFile(ChickensFile, allChickens);
	}
		
    private static String getChickenParent(JsonConfig configuration, String propertyName, Collection<ChickensRegistryItem> allChickens, ChickensRegistryItem chicken, ChickensRegistryItem parent) {
    	String Category = chicken.getRegistryName().toString();
        return configuration.getString(Category, propertyName, parent != null ? parent.getRegistryName().toString() : "");
    }

    private static ItemHolder loadItemStack(JsonConfig configuration, String Category, ChickensRegistryItem chicken, String prefix, ItemHolder defaultItemStack) {
        return configuration.getItemHolder(Category, prefix, defaultItemStack);
    }
    
}
