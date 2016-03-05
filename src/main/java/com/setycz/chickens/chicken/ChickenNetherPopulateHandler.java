package com.setycz.chickens.chicken;

import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by setyc on 05.03.2016.
 */
public class ChickenNetherPopulateHandler {
    @SubscribeEvent
    public void populateChunk(PopulateChunkEvent.Post event) {
        BlockPos chunkCentrePos = new BlockPos(event.chunkX * 16 + 8, 0, event.chunkZ * 16 + 8);
        BiomeGenBase biome = event.world.getBiomeGenForCoords(chunkCentrePos);
        if (biome != BiomeGenBase.hell) {
            return;
        }

        if (event.world.rand.nextFloat() < biome.getSpawningChance()) {

            BlockPos basePosition = getRandomChunkPosition(event.world, event.chunkX, event.chunkZ);
            BlockPos spawnPos = findFloor(event.world, basePosition);

            IEntityLivingData livingData = spawn(event.world, null, spawnPos);
            livingData = spawn(event.world, livingData, spawnPos.north());
            livingData = spawn(event.world, livingData, spawnPos.south());
            livingData = spawn(event.world, livingData, spawnPos.west());
            spawn(event.world, livingData, spawnPos.east());
        }
    }

    private BlockPos findFloor(World world, BlockPos basePosition) {
        BlockPos spawnPos = basePosition;
        while (spawnPos.getY() < 100 && !SpawnerAnimals.canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.getPlacementForEntity(EntityChickensChicken.class), world, spawnPos)) {
            spawnPos = spawnPos.up();
        }
        return spawnPos;
    }

    private IEntityLivingData spawn(World world, IEntityLivingData livingData, BlockPos spawnPos) {
        if (SpawnerAnimals.canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.getPlacementForEntity(EntityChickensChicken.class), world, spawnPos)) {
            EntityChickensChicken entity = new EntityChickensChicken(world);
            entity.setLocationAndAngles(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, world.rand.nextFloat() * 360.0F, 0.0F);
            livingData = entity.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), livingData);
            if (entity.isNotColliding()) {
                world.spawnEntityInWorld(entity);
            }
        }
        return livingData;
    }

    protected static BlockPos getRandomChunkPosition(World worldIn, int x, int z)
    {
        Chunk chunk = worldIn.getChunkFromChunkCoords(x, z);
        int i = x * 16 + worldIn.rand.nextInt(16);
        int j = z * 16 + worldIn.rand.nextInt(16);
        int k = MathHelper.roundUp(chunk.getHeight(new BlockPos(i, 0, j)) + 1, 16);
        int l = worldIn.rand.nextInt(k > 0 ? k : chunk.getTopFilledSegment() + 16 - 1);
        return new BlockPos(i, l, j);
    }

}
