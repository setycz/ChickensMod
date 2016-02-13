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
        return new EntityChickensChicken(this.worldObj);
    }

    @Override
    protected Item getDropItem() {
        ChickensRegistryItem chickenDescription = ChickensRegistry.getByIndex(getChickenType());
        return chickenDescription.getDropItem();
    }

    @Override
    protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {

        int i = this.rand.nextInt(3) + this.rand.nextInt(1 + p_70628_2_);

        for (int j = 0; j < i; ++j)
        {
            this.dropItem(getDropItem(), 1);
        }

        if (this.isBurning())
        {
            this.dropItem(Items.cooked_chicken, 1);
        }
        else
        {
            this.dropItem(Items.chicken, 1);
        }
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
