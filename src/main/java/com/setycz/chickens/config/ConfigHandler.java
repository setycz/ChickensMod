package com.setycz.chickens.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gson.JsonObject;
import com.setycz.chickens.handler.ItemHolder;
import com.setycz.chickens.handler.SpawnType;
import com.setycz.chickens.registry.ChickensRegistry;
import com.setycz.chickens.registry.ChickensRegistryItem;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {
	public static final File configDir = new File("config/chickens");
	public static final File ChickensMainFile = new File(configDir, "main_chickens.cfg");
	public static final File ChickensFile = new File(configDir, "chickens.json");
	
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
			config.getString(comment, "spawn_type", "Chicken spawn type, can be: " + String.join(",", SpawnType.names()));
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
        	
            SpawnType spawnType = SpawnType.valueOf(config.getString(registryName, "spawn_type", chicken.getSpawnType().toString()));
            chicken.setSpawnType(spawnType);
            
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
