package com.setycz.chickens.chicken;

import com.setycz.chickens.ChickensRegistry;
import com.setycz.chickens.ChickensRegistryItem;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
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

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(TYPE_ID, Integer.valueOf(0));
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
    protected Item getDropItem() {
        ChickensRegistryItem chickenDescription = ChickensRegistry.getByIndex(getChickenType());
        return chickenDescription.getDropItem();
    }

    @Override
    public void onLivingUpdate() {
        if (!this.worldObj.isRemote && !this.isChild() && !this.isChickenJockey() && --this.timeUntilNextEgg <= 1)
        {
            ChickensRegistryItem chickenDescription = ChickensRegistry.getByIndex(getChickenType());
            this.playSound("mob.chicken.plop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            this.dropItem(chickenDescription.getLayItem(), 1);
            this.timeUntilNextEgg = this.rand.nextInt(6000) + 6000;
        }
        super.onLivingUpdate();
    }

    public void setChickenType(int type) {
        this.dataWatcher.updateObject(TYPE_ID, Integer.valueOf(type));
    }

    private int getChickenType() {
        return this.dataWatcher.getWatchableObjectInt(TYPE_ID);
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
