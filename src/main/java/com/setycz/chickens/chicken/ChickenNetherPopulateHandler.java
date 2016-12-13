package com.setycz.chickens.chicken;

import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;

/**
 * Created by setyc on 05.03.2016.
 */
public class ChickenNetherPopulateHandler {
    private final float chanceMultiplier;

    public ChickenNetherPopulateHandler(float chanceMultiplier) {

        this.chanceMultiplier = chanceMultiplier;
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void populateChunk(PopulateChunkEvent.Populate event) {
        BlockPos chunkCentrePos = new BlockPos(event.getChunkX() * 16 + 8, 0, event.getChunkZ() * 16 + 8);
        Biome biome = event.getWorld().getBiomeForCoordsBody(chunkCentrePos);
        if (biome != Biomes.HELL) {
            return;
        }

        if (event.getWorld().rand.nextFloat() < biome.getSpawningChance() * chanceMultiplier) {

            BlockPos basePosition = getRandomChunkPosition(event.getWorld(), event.getChunkX(), event.getChunkZ());
            BlockPos spawnPos = findFloor(event.getWorld(), basePosition);

            IEntityLivingData livingData = spawn(event.getWorld(), null, spawnPos);
            livingData = spawn(event.getWorld(), livingData, spawnPos.north());
            livingData = spawn(event.getWorld(), livingData, spawnPos.south());
            livingData = spawn(event.getWorld(), livingData, spawnPos.west());
            spawn(event.getWorld(), livingData, spawnPos.east());
        }
    }

    private BlockPos findFloor(World world, BlockPos basePosition) {
        BlockPos spawnPos = basePosition;
        while (spawnPos.getY() < 100 && !WorldEntitySpawner.canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.getPlacementForEntity(EntityChickensChicken.class), world, spawnPos)) {
            spawnPos = spawnPos.up();
        }
        return spawnPos;
    }

    @Nullable
    private IEntityLivingData spawn(World world, @Nullable IEntityLivingData livingData, BlockPos spawnPos) {
        if (WorldEntitySpawner.canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.getPlacementForEntity(EntityChickensChicken.class), world, spawnPos)) {
            EntityChickensChicken entity = new EntityChickensChicken(world);
            entity.setLocationAndAngles(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, world.rand.nextFloat() * 360.0F, 0.0F);
            livingData = entity.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), livingData);
            if (entity.isNotColliding()) {
                world.spawnEntityInWorld(entity);
            }
        }
        return livingData;
    }

    private static BlockPos getRandomChunkPosition(World worldIn, int x, int z) {
        Chunk chunk = worldIn.getChunkFromChunkCoords(x, z);
        int i = x * 16 + worldIn.rand.nextInt(16);
        int j = z * 16 + worldIn.rand.nextInt(16);
        int k = MathHelper.roundUp(chunk.getHeight(new BlockPos(i, 0, j)) + 1, 16);
        int l = worldIn.rand.nextInt(k > 0 ? k : chunk.getTopFilledSegment() + 16 - 1);
        return new BlockPos(i, l, j);
    }
}
