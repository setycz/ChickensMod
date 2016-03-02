package com.setycz.chickens.henhouse;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.ITickable;

import java.util.List;

/**
 * Created by setyc on 01.03.2016.
 */
public class TileEntityHenhouse extends TileEntity implements ITickable{
    @Override
    public void update() {
        /*
        EntityZombie z = new EntityZombie(worldObj);
        z.setPositionAndRotation(this.getPos().getX(), this.getPos().getY() + 3, this.getPos().getZ(), 0, 0);
        worldObj.spawnEntityInWorld(z);
        */
        if (!worldObj.isRemote) {
            List<EntityItem> entityItems = worldObj.getEntitiesWithinAABB(
                    EntityItem.class,
                    new AxisAlignedBB(getPos().add(-1, -1, -1), getPos().add(1,1,1)),
                    EntitySelectors.selectAnything
                    );
            for (EntityItem entity :
                    entityItems) {
                worldObj.removeEntity(entity);
            }
        }
    }
}
