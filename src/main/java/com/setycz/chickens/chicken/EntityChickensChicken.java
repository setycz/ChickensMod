package com.setycz.chickens.chicken;

import com.setycz.chickens.ChickensRegistry;
import com.setycz.chickens.ChickensRegistryItem;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
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
        ChickensRegistryItem chickenDescription = ChickensRegistry.getByIndex(getChickenType());
        return chickenDescription.getTexture();
    }

    @Override
    public EntityChicken createChild(EntityAgeable ageable) {
        ChickensRegistryItem chickenDescription = ChickensRegistry.getByIndex(getChickenType());
        EntityChickensChicken mate = (EntityChickensChicken)ageable;
        ChickensRegistryItem mateChickenDescription = ChickensRegistry.getByIndex(mate.getChickenType());

        ArrayList<ChickensRegistryItem> possibleChildren = new ArrayList<ChickensRegistryItem>(ChickensRegistry.getChildrens(chickenDescription, mateChickenDescription));
        possibleChildren.add(chickenDescription);
        possibleChildren.add(mateChickenDescription);

        ChickensRegistryItem childToBeBorn = getRandomChickenToBeBorn(possibleChildren);
        if (childToBeBorn == null) {
            return null;
        }

        EntityChickensChicken newChicken = new EntityChickensChicken(this.worldObj);
        newChicken.setChickenType(ChickensRegistry.getChildIndex(childToBeBorn));
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
        for(ChickensRegistryItem child: possibleChildren) {
            currentVale += maxChance - (child.getTier() + 1);
            if (diceValue < currentVale) {
                return child;
            }
        }
        return null;
    }

    private int getMaxDiceValue(ArrayList<ChickensRegistryItem> possibleChildren, int maxChance) {
        int maxDiceValue = 0;
        for(ChickensRegistryItem child: possibleChildren) {
            maxDiceValue += maxChance - (child.getTier() + 1);
        }
        return maxDiceValue;
    }

    private int getMaxChance(ArrayList<ChickensRegistryItem> possibleChildren) {
        int maxChance =0;
        for(ChickensRegistryItem child: possibleChildren) {
            maxChance = Math.max(maxChance, child.getTier() + 1);
        }
        maxChance += 1;
        return maxChance;
    }

    @Override
    public void onLivingUpdate() {
        if (!this.worldObj.isRemote && !this.isChild() && !this.isChickenJockey() && --this.timeUntilNextEgg <= 1)
        {
            ChickensRegistryItem chickenDescription = ChickensRegistry.getByIndex(getChickenType());
            this.playSound("mob.chicken.plop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            this.entityDropItem(chickenDescription.createLayItem(), 0);
            this.timeUntilNextEgg = this.rand.nextInt(6000) + 6000;
        }
        super.onLivingUpdate();
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        if (livingdata instanceof GroupData) {
            GroupData groupData = (GroupData)livingdata;
            setChickenType(groupData.getType());
        }
        else {
            List<ChickensRegistryItem> possibleChickens = getPossibleChickensToSpawn();
            ChickensRegistryItem chickenToSpawn = possibleChickens.get(rand.nextInt(possibleChickens.size()));

            int type = ChickensRegistry.getChildIndex(chickenToSpawn);
            setChickenType(type);
            livingdata = new GroupData(type);
        }

        if (rand.nextInt(5) == 0)
        {
            setGrowingAge(-24000);
        }

        return livingdata;
    }

    private List<ChickensRegistryItem> getPossibleChickensToSpawn() {
        List<ChickensRegistryItem> result = new ArrayList<ChickensRegistryItem>();
        for (ChickensRegistryItem chicken : ChickensRegistry.getItems()) {
            if (chicken.getTier() == 0 && !chicken.isDye()) {
                result.add(chicken);
            }
        }
        return result;
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
        this.dataWatcher.updateObject(TYPE_ID, type);
    }

    private int getChickenType() {
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
        tagCompund.setInteger(TYPE_NBT, getChickenType());
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompund) {
        super.readFromNBT(tagCompund);
        setChickenType(tagCompund.getInteger(TYPE_NBT));
    }
}
