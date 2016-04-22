package com.setycz.chickens.chicken;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemFood;
import net.minecraft.util.AxisAlignedBB;

import java.util.List;

/**
 * Created by setyc on 20.04.2016.
 */
public class EntityAIHungry extends EntityAIBase {

    private final EntityChickensChicken chicken;
    private EntityItem temptingItem;

    public EntityAIHungry(EntityChickensChicken chicken) {
        this.chicken = chicken;
    }

    @Override
    public boolean shouldExecute() {
        if (!chicken.isHungry()) {
            return false;
        }
        AxisAlignedBB range = new AxisAlignedBB(chicken.posX - 10.0, chicken.posY - 10.0, chicken.posZ - 10.0, chicken.posX + 10.0, chicken.posY + 10.0, chicken.posZ + 10.0);
        List<EntityItem> items = chicken.worldObj.getEntitiesWithinAABB(EntityItem.class, range);
        for (EntityItem item : items) {
            if (item.getEntityItem().getItem() instanceof ItemFood) {
                temptingItem = item;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean continueExecuting() {
        return temptingItem != null && temptingItem.isEntityAlive() && chicken.isHungry() && super.continueExecuting();
    }

    @Override
    public void updateTask() {
        if (chicken.getDistanceSqToEntity(temptingItem) <= 1.0) {
            chicken.consume(temptingItem);
            temptingItem = null;
        }
        else {
            chicken.getNavigator().tryMoveToEntityLiving(temptingItem, 1.5);
        }
        super.updateTask();
    }
}
