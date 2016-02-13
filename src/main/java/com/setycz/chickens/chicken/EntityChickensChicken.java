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
        return Items.gunpowder;
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
