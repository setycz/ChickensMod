package com.setycz.chickens.entity;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.setycz.chickens.block.TileEntityHenhouse;
import com.setycz.chickens.config.ConfigHandler;
import com.setycz.chickens.handler.SpawnType;
import com.setycz.chickens.registry.ChickensRegistry;
import com.setycz.chickens.registry.ChickensRegistryItem;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Biomes;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

/**
 * Created by setyc on 12.02.2016.
 */
public class EntityChickensChicken extends EntityChicken {
    //private static final DataParameter<Integer> CHICKEN_TYPE = EntityDataManager.createKey(EntityChickensChicken.class, DataSerializers.VARINT);
    private static final DataParameter<String>  CHICKEN_TYPE = EntityDataManager.createKey(EntityChickensChicken.class, DataSerializers.STRING);
    private static final DataParameter<Boolean> CHICKEN_STATS_ANALYZED = EntityDataManager.createKey(EntityChickensChicken.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> CHICKEN_GROWTH = EntityDataManager.createKey(EntityChickensChicken.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> CHICKEN_GAIN = EntityDataManager.createKey(EntityChickensChicken.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> CHICKEN_STRENGTH = EntityDataManager.createKey(EntityChickensChicken.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> LAY_PROGRESS = EntityDataManager.createKey(EntityChickensChicken.class, DataSerializers.VARINT);

    private static final String TYPE_NBT = "Type";
    private static final String CHICKEN_STATS_ANALYZED_NBT = "Analyzed";
    private static final String CHICKEN_GROWTH_NBT = "Growth";
    private static final String CHICKEN_GAIN_NBT = "Gain";
    private static final String CHICKEN_STRENGTH_NBT = "Strength";

    public EntityChickensChicken(World worldIn) {
        super(worldIn);
    }

    public boolean getStatsAnalyzed() {
        return dataManager.get(CHICKEN_STATS_ANALYZED);
    }

    public void setStatsAnalyzed(boolean statsAnalyzed) {
        dataManager.set(CHICKEN_STATS_ANALYZED, statsAnalyzed);
    }

    public int getGain() {
        return dataManager.get(CHICKEN_GAIN);
    }

    private void setGain(int gain) {
        dataManager.set(CHICKEN_GAIN, gain);
    }

    public int getGrowth() {
        return dataManager.get(CHICKEN_GROWTH);
    }

    private void setGrowth(int growth) {
        dataManager.set(CHICKEN_GROWTH, growth);
    }

    public int getStrength() {
        return dataManager.get(CHICKEN_STRENGTH);
    }

    private void setStrength(int strength) {
        dataManager.set(CHICKEN_STRENGTH, strength);
    }

    public ResourceLocation getTexture() {
        ChickensRegistryItem chickenDescription = getChickenDescription();
        return chickenDescription.getTexture();
    }

    private ChickensRegistryItem getChickenDescription() {
    	ChickensRegistryItem description = ChickensRegistry.getByRegistryName(getChickenTypeInternal());
    	
    	if(description == null || !description.isEnabled()){
    		description = ChickensRegistry.getByResourceLocation(ChickensRegistry.SMART_CHICKEN_ID);
    		if(!description.isEnabled())
    			this.despawnEntity();
    	}
        return description;
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
        return I18n.translateToLocal("entity." + chickenDescription.getEntityName() + ".name");
    }

    @Override
    public EntityChicken createChild(EntityAgeable ageable) {
        EntityChickensChicken mateChicken = (EntityChickensChicken) ageable;

        ChickensRegistryItem chickenDescription = getChickenDescription();
        ChickensRegistryItem mateChickenDescription = mateChicken.getChickenDescription();

        ChickensRegistryItem childToBeBorn = ChickensRegistry.getRandomChild(chickenDescription, mateChickenDescription);
        if (childToBeBorn == null) {
            return null;
        }

        EntityChickensChicken newChicken = new EntityChickensChicken(this.world);
        newChicken.setChickenType(childToBeBorn.getRegistryName().toString());

        boolean mutatingStats = chickenDescription.getRegistryName() == mateChickenDescription.getRegistryName() && childToBeBorn.getRegistryName() == chickenDescription.getRegistryName();
        if (mutatingStats) {
            increaseStats(newChicken, this, mateChicken, rand);
        } else if (chickenDescription.getRegistryName() == childToBeBorn.getRegistryName()) {
            inheritStats(newChicken, this);
        } else if (mateChickenDescription.getRegistryName() == childToBeBorn.getRegistryName()) {
            inheritStats(newChicken, mateChicken);
        } else if (ConfigHandler.crossbreedInheritStats) {
            inheritStatsCrossbreeding(newChicken, this, mateChicken);
        }

        return newChicken;
    }

    private static void inheritStats(EntityChickensChicken newChicken, EntityChickensChicken parent) {
        newChicken.setGrowth(parent.getGrowth());
        newChicken.setGain(parent.getGain());
        newChicken.setStrength(parent.getStrength());
    }

    private void inheritStatsCrossbreeding(EntityChickensChicken newChicken, EntityChickensChicken parent1, EntityChickensChicken parent2) {
        float ratio = ConfigHandler.crossbreedInheritStatsRatio;
        newChicken.setGrowth(Math.max((int) (((parent1.getGrowth() + parent2.getGrowth()) / 2.0f) * ratio), 1));
        newChicken.setGain(Math.max((int) (((parent1.getGain() + parent2.getGain()) / 2.0f) * ratio), 1));
        newChicken.setStrength(Math.max((int) (((parent1.getStrength() + parent2.getStrength()) / 2.0f) * ratio), 1));
    }

    private static void increaseStats(EntityChickensChicken newChicken, EntityChickensChicken parent1, EntityChickensChicken parent2, Random rand) {
        int parent1Strength = parent1.getStrength();
        int parent2Strength = parent2.getStrength();
        newChicken.setGrowth(calculateNewStat(parent1Strength, parent2Strength, parent1.getGrowth(), parent2.getGrowth(), rand));
        newChicken.setGain(calculateNewStat(parent1Strength, parent2Strength, parent2.getGain(), parent2.getGain(), rand));
        newChicken.setStrength(calculateNewStat(parent1Strength, parent2Strength, parent1Strength, parent2Strength, rand));
    }

    private static int calculateNewStat(int thisStrength, int mateStrength, int stat1, int stat2, Random rand) {
        int mutation = rand.nextInt(2) + 1;
        int newStatValue = (stat1 * thisStrength + stat2 * mateStrength) / (thisStrength + mateStrength) + mutation;
        if (newStatValue <= 1) return 1;
        if (newStatValue >= 10) return 10;
        return newStatValue;
    }

    @Override
    public void onLivingUpdate() {
        if (!this.world.isRemote && !this.isChild() && !this.isChickenJockey()) {
            int newTimeUntilNextEgg = timeUntilNextEgg - 1;
            setTimeUntilNextEgg(newTimeUntilNextEgg);
            if (newTimeUntilNextEgg <= 1) {
                ChickensRegistryItem chickenDescription = getChickenDescription();
                ItemStack itemToLay = chickenDescription.createLayItem();

                int gain = getGain();
                if (gain >= 5) {
                	itemToLay.grow(chickenDescription.createLayItem().getCount());
                }
                if (gain >= 10) {
                	itemToLay.grow(chickenDescription.createLayItem().getCount());
                }
                
                itemToLay = TileEntityHenhouse.pushItemStack(itemToLay, world, new Vec3d(posX, posY, posZ));

                if (!itemToLay.isEmpty()) {
                    entityDropItem(chickenDescription.createLayItem(), 0);
                    playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                }

                resetTimeUntilNextEgg();
            }
        }
        super.onLivingUpdate();
    }

    private void setTimeUntilNextEgg(int value) {
        timeUntilNextEgg = value;
        updateLayProgress();
    }

    public int getLayProgress() {
        return dataManager.get(LAY_PROGRESS);
    }

    private void updateLayProgress() {
        dataManager.set(LAY_PROGRESS, timeUntilNextEgg / 60 / 20 / 2);
    }

    private void resetTimeUntilNextEgg() {
        ChickensRegistryItem chickenDescription = getChickenDescription();
        int newBaseTimeUntilNextEgg = (chickenDescription.getMinLayTime()
                + rand.nextInt(chickenDescription.getMaxLayTime() - chickenDescription.getMinLayTime()));
        int newTimeUntilNextEgg = (int) Math.max(1.0f, (newBaseTimeUntilNextEgg * (10.f - getGrowth() + 1.f)) / 10.f);
        setTimeUntilNextEgg(newTimeUntilNextEgg * 2);
    }

    @Override
    public boolean getCanSpawnHere() {
        boolean anyInNether = ChickensRegistry.isAnyIn(SpawnType.HELL);
        boolean anyInOverworld = ChickensRegistry.isAnyIn(SpawnType.NORMAL) || ChickensRegistry.isAnyIn(SpawnType.SNOW);
        Biome biome = world.getBiomeForCoordsBody(getPosition());
        return anyInNether && biome == Biomes.HELL || anyInOverworld && super.getCanSpawnHere();
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingData) {
        livingData = super.onInitialSpawn(difficulty, livingData);
        if (livingData instanceof GroupData) {
            GroupData groupData = (GroupData) livingData;
            setChickenType(groupData.getType());
        } else {
            SpawnType spawnType = getSpawnType();
            List<ChickensRegistryItem> possibleChickens = ChickensRegistry.getPossibleChickensToSpawn(spawnType);
            if (possibleChickens.size() > 0) {
                ChickensRegistryItem chickenToSpawn = possibleChickens.get(rand.nextInt(possibleChickens.size()));

                String type = chickenToSpawn.getRegistryName().toString();
                setChickenType(type);
                livingData = new GroupData(type);
            }
        }

        if (rand.nextInt(5) == 0) {
            setGrowingAge(-24000);
        }

        return livingData;
    }

    private SpawnType getSpawnType() {
        Biome biome = world.getBiomeForCoordsBody(getPosition());
        return ChickensRegistry.getSpawnType(biome);
    }

    private static class GroupData implements IEntityLivingData {
        private final String type;

        public GroupData(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    public void setChickenType(String registryName) {
        setChickenTypeInternal(registryName);
        isImmuneToFire = getChickenDescription().isImmuneToFire();
        resetTimeUntilNextEgg();
    }

    private void setChickenTypeInternal(String registryName) {
        this.dataManager.set(CHICKEN_TYPE, registryName);
    }
    
    private String getChickenTypeInternal() {
        return this.dataManager.get(CHICKEN_TYPE);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(CHICKEN_TYPE, "");
        dataManager.register(CHICKEN_GROWTH, 1);
        dataManager.register(CHICKEN_GAIN, 1);
        dataManager.register(CHICKEN_STRENGTH, 1);
        dataManager.register(LAY_PROGRESS, 0);
        dataManager.register(CHICKEN_STATS_ANALYZED, false);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setString(TYPE_NBT, getChickenTypeInternal());
        tagCompound.setBoolean(CHICKEN_STATS_ANALYZED_NBT, getStatsAnalyzed());
        tagCompound.setInteger(CHICKEN_GROWTH_NBT, getGrowth());
        tagCompound.setInteger(CHICKEN_GAIN_NBT, getGain());
        tagCompound.setInteger(CHICKEN_STRENGTH_NBT, getStrength());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        setChickenTypeInternal(tagCompound.getString(TYPE_NBT));
        setStatsAnalyzed(tagCompound.getBoolean(CHICKEN_STATS_ANALYZED_NBT));
        setGrowth(getStatusValue(tagCompound, CHICKEN_GROWTH_NBT));
        setGain(getStatusValue(tagCompound, CHICKEN_GAIN_NBT));
        setStrength(getStatusValue(tagCompound, CHICKEN_STRENGTH_NBT));
        updateLayProgress();
    }

    private int getStatusValue(NBTTagCompound compound, String statusName) {
        return compound.hasKey(statusName) ? compound.getInteger(statusName) : 1;
    }

    @Override
    public int getTalkInterval() {
        return 20 * 60;
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {
        if (this.rand.nextFloat() > 0.1) {
            return;
        }
        super.playStepSound(pos, blockIn);
    }

    @Override
    protected ResourceLocation getLootTable() {
        return null;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        ItemStack itemsToDrop = getChickenDescription().createDropItem();
        int count = 1 + rand.nextInt(1 + lootingModifier);
        itemsToDrop.setCount(itemsToDrop.getCount() * count);
        entityDropItem(itemsToDrop, 0);

        if (this.isBurning()) {
            this.dropItem(Items.COOKED_CHICKEN, 1);
        } else {
            this.dropItem(Items.CHICKEN, 1);
        }
    }

    @Override
    public void setGrowingAge(int age) {
        int childAge = -24000;
        boolean resetToChild = age == childAge;
        if (resetToChild) {
            age = Math.min(-1, (childAge * (10 - getGrowth() + 1)) / 10);
        }

        int loveAge = 6000;
        boolean resetLoveAfterBreeding = age == loveAge;
        if (resetLoveAfterBreeding) {
            age = Math.max(1, (loveAge * (10 - getGrowth() + 1)) / 10);
        }

        super.setGrowingAge(age);
    }
}
