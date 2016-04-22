package com.setycz.chickens.chicken;

import com.setycz.chickens.ChickensRegistry;
import com.setycz.chickens.ChickensRegistryItem;
import com.setycz.chickens.SpawnType;
import com.setycz.chickens.henhouse.TileEntityHenhouse;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.List;

/**
 * Created by setyc on 12.02.2016.
 */
public class EntityChickensChicken extends EntityChicken {

    public static final int TYPE_ID = 19;
    public static final String TYPE_NBT = "Type";
    public static final int FOOD_LEVEL_ID = 20;
    public static final String FOOD_LEVEL_NBT = "FoodLevel";
    public int foodTimer = 80;

    public EntityChickensChicken(World worldIn) {
        super(worldIn);
        this.tasks.addTask(3, new EntityAIHungry(this));
    }

    public ResourceLocation getTexture() {
        ChickensRegistryItem chickenDescription = getChickenDescription();
        return chickenDescription.getTexture();
    }

    private ChickensRegistryItem getChickenDescription() {
        return ChickensRegistry.getByType(getChickenTypeInternal());
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
        if (!this.worldObj.isRemote && !this.isChild() && !this.isChickenJockey()) {
            if (--this.timeUntilNextEgg <= 1) {
                ChickensRegistryItem chickenDescription = getChickenDescription();
                int foodRequiredToLay = chickenDescription.getTier();
                int foodLevel = getFoodLevel();
                if (foodLevel >= foodRequiredToLay) {
                    ItemStack itemToLay = chickenDescription.createLayItem();

                    itemToLay = TileEntityHenhouse.pushItemStack(itemToLay, worldObj, new Vec3(posX, posY, posZ));
                    if (itemToLay != null) {
                        this.playSound("mob.chicken.plop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                        this.entityDropItem(chickenDescription.createLayItem(), 0);
                    }

                    foodLevel -= foodRequiredToLay;
                    setFoodLevel(foodLevel);
                }

                resetTimeUntilNextEgg();
            }

            if (--foodTimer <= 0) {
                int foodLevel = getFoodLevel();
                if (foodLevel > 1) {
                    setFoodLevel(foodLevel - 1);
                } else {
                    attackEntityFrom(new DamageSource("Hunger"), 1);
                }

                foodTimer = 80;
            }
        }

        super.onLivingUpdate();
    }

    public int getFoodLevel() {
        return dataWatcher.getWatchableObjectInt(FOOD_LEVEL_ID);
    }

    public void setFoodLevel(int level) {
        dataWatcher.updateObject(FOOD_LEVEL_ID, level);
    }

    public int getMaxFoodLevel() {
        return 20;
    }

    public boolean isHungry() {
        return getFoodLevel() * 100 / getMaxFoodLevel() <= 50;
    }

    public void consume(EntityItem entityItem) {
        if (!worldObj.isRemote) {
            ItemStack itemStackToConsume = entityItem.getEntityItem();
            int itemHungerAmount = ((ItemFood) itemStackToConsume.getItem()).getHealAmount(itemStackToConsume);

            int currentFoodLevel = getFoodLevel();
            int canEat = getMaxFoodLevel() - currentFoodLevel;
            int canEatItems = (int)Math.ceil(canEat / (double)itemHungerAmount);
            int willConsumeItems = Math.min(canEatItems, itemStackToConsume.stackSize);

            int newFoodLevel = Math.min(getMaxFoodLevel(), currentFoodLevel + willConsumeItems*itemHungerAmount);
            setFoodLevel(newFoodLevel);
            itemStackToConsume.stackSize -= willConsumeItems;
            if (itemStackToConsume.stackSize == 0) {
                worldObj.removeEntity(entityItem);
            }
        }
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
        this.dataWatcher.addObject(FOOD_LEVEL_ID, getMaxFoodLevel());
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompund) {
        super.writeToNBT(tagCompund);
        tagCompund.setInteger(TYPE_NBT, getChickenTypeInternal());
        tagCompund.setInteger(FOOD_LEVEL_NBT, getFoodLevel());
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompund) {
        super.readFromNBT(tagCompund);
        setChickenTypeInternal(tagCompund.getInteger(TYPE_NBT));
        setFoodLevel(tagCompund.getInteger(FOOD_LEVEL_NBT));
    }

    @Override
    public int getTalkInterval()
    {
        return 20 * 60;
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {
    	if(this.rand.nextFloat() > 0.1) {
    		return;
    	}
    	super.playStepSound(pos,  blockIn);
    }

    @Override
    protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
        ItemStack itemsToDrop = getChickenDescription().createDropItem();
        int count = 1 + rand.nextInt(1 + p_70628_2_);
        itemsToDrop.stackSize *= count;
        entityDropItem(itemsToDrop, 0);

        if (this.isBurning()) {
            this.dropItem(Items.cooked_chicken, 1);
        }
        else {
            this.dropItem(Items.chicken, 1);
        }
    }
}
