package com.setycz.chickens.chicken;

import com.setycz.chickens.ChickensRegistry;
import com.setycz.chickens.ChickensRegistryItem;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

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

        ArrayList<ChickensRegistryItem> possibleChildren = new ArrayList<ChickensRegistryItem>(ChickensRegistry.getChildren(chickenDescription, mateChickenDescription));
        possibleChildren.add(chickenDescription);
        possibleChildren.add(mateChickenDescription);

        ChickensRegistryItem childToBeBorn = getRandomChickenToBeBorn(possibleChildren);
        if (childToBeBorn == null) {
            return null;
        }

        EntityChickensChicken newChicken = new EntityChickensChicken(this.worldObj);
        newChicken.setChickenType(childToBeBorn.getId());
        return newChicken;
    }

    private ChickensRegistryItem getRandomChickenToBeBorn(ArrayList<ChickensRegistryItem> possibleChildren) {
        int maxChance = getMaxChance(possibleChildren);
        int maxDiceValue = getMaxDiceValue(possibleChildren, maxChance);

        int diceValue = rand.nextInt(maxDiceValue);
        return getChickenToBeBorn(possibleChildren, maxChance, diceValue);
    }

    private ChickensRegistryItem getChickenToBeBorn(ArrayList<ChickensRegistryItem> possibleChildren, int maxChance, int diceValue) {
        int currentVale = 0;
        for (ChickensRegistryItem child : possibleChildren) {
            currentVale += maxChance - child.getTier();
            if (diceValue < currentVale) {
                return child;
            }
        }
        return null;
    }

    private int getMaxDiceValue(ArrayList<ChickensRegistryItem> possibleChildren, int maxChance) {
        int maxDiceValue = 0;
        for (ChickensRegistryItem child : possibleChildren) {
            maxDiceValue += maxChance - child.getTier();
        }
        return maxDiceValue;
    }

    private int getMaxChance(ArrayList<ChickensRegistryItem> possibleChildren) {
        int maxChance = 0;
        for (ChickensRegistryItem child : possibleChildren) {
            maxChance = Math.max(maxChance, child.getTier());
        }
        maxChance += 1;
        return maxChance;
    }

    @Override
    public void onLivingUpdate() {
        if (!this.worldObj.isRemote && !this.isChild() && !this.isChickenJockey() && --this.timeUntilNextEgg <= 1) {
            ChickensRegistryItem chickenDescription = getChickenDescription();
            this.playSound("mob.chicken.plop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            this.entityDropItem(chickenDescription.createLayItem(), 0);
            resetTimeUntilNextEgg();
        }
        super.onLivingUpdate();
    }

    private void resetTimeUntilNextEgg() {
        this.timeUntilNextEgg = (rand.nextInt(6000) + 6000) * 2 * getTier();
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingData) {
        livingData = super.onInitialSpawn(difficulty, livingData);
        if (livingData instanceof GroupData) {
            GroupData groupData = (GroupData) livingData;
            setChickenType(groupData.getType());
        } else {
            List<ChickensRegistryItem> possibleChickens = ChickensRegistry.getPossibleChickensToSpawn();
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
