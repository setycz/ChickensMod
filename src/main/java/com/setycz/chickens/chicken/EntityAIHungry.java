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

    public static final double SEEK_RANGE = 10.0;
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
        temptingItem = findSomeFood();
        return temptingItem != null;
    }

    private EntityItem findSomeFood() {
        AxisAlignedBB range = new AxisAlignedBB(chicken.posX - SEEK_RANGE, chicken.posY - SEEK_RANGE, chicken.posZ - SEEK_RANGE, chicken.posX + SEEK_RANGE, chicken.posY + SEEK_RANGE, chicken.posZ + SEEK_RANGE);
        List<EntityItem> items = chicken.worldObj.getEntitiesWithinAABB(EntityItem.class, range);
        for (EntityItem item : items) {
            if (item.isEntityAlive() && item.getEntityItem().getItem() instanceof ItemFood) {
                if (chicken.canConsume(item)) {
                    return item;
                }
            }
        }
        return null;
    }

    @Override
    public boolean continueExecuting() {
        return temptingItem != null && temptingItem.isEntityAlive() && !chicken.isFed();
    }

    @Override
    public void updateTask() {
        if (chicken.getDistanceSqToEntity(temptingItem) <= 1.0) {
            chicken.consume(temptingItem);
            if (!chicken.isFed()) {
                temptingItem = findSomeFood();
            }
            else {
                temptingItem = null;
            }
        }
        else {
            chicken.getNavigator().tryMoveToEntityLiving(temptingItem, 1.5);
        }
        super.updateTask();
    }
}
