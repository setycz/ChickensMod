package com.setycz.chickens.registry;

import java.util.ArrayList;
import java.util.HashMap;

import com.setycz.chickens.ChickensMod;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class SpawnRegistry {
	
	// Reads a custom biome file.

	public static HashMap<String, BiomeSpawnItem> registeredSpawns = new HashMap<String, BiomeSpawnItem>();
	
	public static ArrayList<String> allBiomeIDs = new ArrayList<String>();
	
	public static void registerBiomeSpawns(String spawnID, BiomeSpawnItem biomes) {
		if(registeredSpawns.containsKey(spawnID))
			ChickensMod.log.error("Failed to add biome spawnlist. Duplicate Entry for Biome spawn list"+ spawnID +" ");
		else {
			registeredSpawns.put(spawnID, biomes);
			
			for(String biome: biomes.biomeList)
				if(!allBiomeIDs.contains(biome))
					allBiomeIDs.add(biome);
		}
	}
	
	public static void setupDefault() {
		ArrayList<String> biomes = new ArrayList<String>();
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
				biomes.add(biome.getRegistryName().toString());
			}
		}
	}
	
	public static BiomeSpawnItem getSpawnListFromID (String spawnID) {
		return registeredSpawns.get(spawnID);
	}
	
	public static boolean containsBiome(Biome biomeIn) {
		if(allBiomeIDs.contains(biomeIn.getRegistryName().toString()))
				return true;
		return false;
	}
	
	public static boolean canSpawnInBiome(Biome biome, String spawnID) {
		BiomeSpawnItem Spawner = getSpawnListFromID(spawnID);
		if(Spawner.biomeList.contains(biome.getRegistryName().toString()))
			return true;
		else
			return false;
	}
}
