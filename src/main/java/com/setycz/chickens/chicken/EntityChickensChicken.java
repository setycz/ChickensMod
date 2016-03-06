package com.setycz.chickens.chicken;

import com.setycz.chickens.ChickensRegistry;
import com.setycz.chickens.ChickensRegistryItem;
import com.setycz.chickens.SpawnType;
import com.setycz.chickens.henhouse.TileEntityHenhouse;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by setyc on 12.02.2016.
 */
public class EntityChickensChicken extends EntityChicken {

    public static final int TYPE_ID = 19;
    public static final String TYPE_NBT = "Type";

    public EntityChickensChicken(World worldIn) {
        super(worldIn);
    }

    public ResourceLocation getTexture() {
        ChickensRegistryItem chickenDescription = getChickenDescription();
        return chickenDescription.getTexture();
    }

    private ChickensRegistryItem getChickenDescription() {
        return ChickensRegistry.getByType(getChickenTypeInternal());
    }

    public int getTier() {
        return getChickenDescription().getTier();
    }

    @Override
    public String getName() {
        if (this.hasCustomName()) {
            return getCustomNameTag();
        }

        ChickensRegistryItem chickenDescription = getChickenDescription();
        return StatCollector.translateToLocal("entity." + chickenDescription.getEntityName() + ".name");
    }

    @Override
    public EntityChicken createChild(EntityAgeable ageable) {
        ChickensRegistryItem chickenDescription = getChickenDescription();
        ChickensRegistryItem mateChickenDescription = ((EntityChickensChicken)ageable).getChickenDescription();

        ChickensRegistryItem childToBeBorn = ChickensRegistry.getRandomChild(chickenDescription, mateChickenDescription);
        if (childToBeBorn == null) {
            return null;
        }

        EntityChickensChicken newChicken = new EntityChickensChicken(this.worldObj);
        newChicken.setChickenType(childToBeBorn.getId());
        return newChicken;
    }

    @Override
    public void onLivingUpdate() {
        if (!this.worldObj.isRemote && !this.isChild() && !this.isChickenJockey() && --this.timeUntilNextEgg <= 1) {
            ChickensRegistryItem chickenDescription = getChickenDescription();
            ItemStack itemToLay = chickenDescription.createLayItem();

            List<TileEntityHenhouse> henhouses = findHenhouses();
            for (TileEntityHenhouse henhouse : henhouses) {
                itemToLay = henhouse.pushItemStack(itemToLay);
                if (itemToLay == null) {
                    break;
                }
            }

            if (itemToLay != null) {
                this.playSound("mob.chicken.plop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                this.entityDropItem(chickenDescription.createLayItem(), 0);
            }

            resetTimeUntilNextEgg();
        }
        super.onLivingUpdate();
    }

    private List<TileEntityHenhouse> findHenhouses() {
        Vec3 pos = new Vec3(posX, posY, posZ);
        double radius = 5.5;

        int i = MathHelper.floor_double((pos.xCoord - radius - World.MAX_ENTITY_RADIUS) / 16.0D);
        int j = MathHelper.floor_double((pos.xCoord + radius + World.MAX_ENTITY_RADIUS) / 16.0D);
        int k = MathHelper.floor_double((pos.zCoord - radius - World.MAX_ENTITY_RADIUS) / 16.0D);
        int l = MathHelper.floor_double((pos.zCoord + radius + World.MAX_ENTITY_RADIUS) / 16.0D);
        List<TileEntityHenhouse> result = new ArrayList<TileEntityHenhouse>();
        for (int i1 = i; i1 <= j; ++i1) {
            for (int j1 = k; j1 <= l; ++j1) {
                Chunk chunk = worldObj.getChunkFromChunkCoords(i1, j1);
                for (TileEntity tileEntity : chunk.getTileEntityMap().values()) {
                    if (new Vec3(tileEntity.getPos()).addVector(0.5, 0.5, 0.5).distanceTo(pos) <= radius) {
                        if (tileEntity instanceof TileEntityHenhouse) {
                            result.add((TileEntityHenhouse) tileEntity);
                        }
                    }
                }
            }
        }
        return result;
    }

    private void resetTimeUntilNextEgg() {
        ChickensRegistryItem chickenDescription = getChickenDescription();
        this.timeUntilNextEgg = (chickenDescription.getMinLayTime()
                + rand.nextInt(chickenDescription.getMaxLayTime()-chickenDescription.getMinLayTime())) * 2;
    }

    @Override
    public boolean getCanSpawnHere() {
        boolean anyInNether = ChickensRegistry.isAnyIn(SpawnType.HELL);
        boolean anyInOverworld = ChickensRegistry.isAnyIn(SpawnType.NORMAL) || ChickensRegistry.isAnyIn(SpawnType.SNOW);
        BiomeGenBase biome = worldObj.getBiomeGenForCoords(getPosition());
        return anyInNether && biome == BiomeGenBase.hell || anyInOverworld && super.getCanSpawnHere();
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingData) {
        livingData = super.onInitialSpawn(difficulty, livingData);
        if (livingData instanceof GroupData) {
            GroupData groupData = (GroupData) livingData;
            setChickenType(groupData.getType());
        } else {
            SpawnType spawnType = getSpawnType();
            List<ChickensRegistryItem> possibleChickens = ChickensRegistry.getPossibleChickensToSpawn(spawnType);
            ChickensRegistryItem chickenToSpawn = possibleChickens.get(rand.nextInt(possibleChickens.size()));

            int type = chickenToSpawn.getId();
            setChickenType(type);
            livingData = new GroupData(type);
        }

        if (rand.nextInt(5) == 0) {
            setGrowingAge(-24000);
        }

        return livingData;
    }

    private SpawnType getSpawnType() {
        BiomeGenBase biome = worldObj.getBiomeGenForCoords(getPosition());
        return ChickensRegistry.getSpawnType(biome);
    }

    private static class GroupData implements IEntityLivingData {
        private final int type;

        public GroupData(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }

    public void setChickenType(int type) {
        setChickenTypeInternal(type);
        isImmuneToFire = getChickenDescription().isImmuneToFire();
        resetTimeUntilNextEgg();
    }

    private void setChickenTypeInternal(int type) {
        this.dataWatcher.updateObject(TYPE_ID, type);
    }

    private int getChickenTypeInternal() {
        return this.dataWatcher.getWatchableObjectInt(TYPE_ID);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(TYPE_ID, 0);
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompund) {
        super.writeToNBT(tagCompund);
        tagCompund.setInteger(TYPE_NBT, getChickenTypeInternal());
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompund) {
        super.readFromNBT(tagCompund);
        setChickenTypeInternal(tagCompund.getInteger(TYPE_NBT));
    }
}
