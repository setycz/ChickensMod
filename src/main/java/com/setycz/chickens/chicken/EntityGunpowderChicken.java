package com.setycz.chickens.chicken;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * Created by setyc on 12.02.2016.
 */
public class EntityGunpowderChicken extends EntityChicken {

    public EntityGunpowderChicken(World worldIn) {
        super(worldIn);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(19, Integer.valueOf(0));
    }

    public ResourceLocation getTexture() {
        switch (this.dataWatcher.getWatchableObjectInt(19)) {
            case 0:
                return new ResourceLocation("chickens", "textures/entity/GunpowderChicken.png");
            case 1:
                return new ResourceLocation("chickens", "textures/entity/FlintChicken.png");
            default:
                return null;
        }
    }

    @Override
    public EntityChicken createChild(EntityAgeable ageable) {
        return new EntityGunpowderChicken(this.worldObj);
    }

    @Override
    protected Item getDropItem() {
        return Items.gunpowder;
    }

    public void setType(int type) {
        this.dataWatcher.updateObject(19, Integer.valueOf(type));
    }
}
