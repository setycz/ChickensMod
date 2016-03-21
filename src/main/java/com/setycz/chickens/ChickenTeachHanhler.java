package com.setycz.chickens;

import com.setycz.chickens.chicken.EntityChickensChicken;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by setyc on 21.03.2016.
 */
public class ChickenTeachHanhler {

    @SubscribeEvent
    public void handleInteraction(EntityInteractEvent event) {
        ItemStack item = event.entityPlayer.getCurrentEquippedItem();
        if (item == null || item.getItem() != Items.book) {
            return;
        }
        if (!(event.target instanceof EntityChicken)) {
            return;
        }
        EntityChicken chicken = (EntityChicken) event.target;

        World worldObj = event.entityPlayer.worldObj;
        if (!worldObj.isRemote) {
            EntityChickensChicken smartChicken = new EntityChickensChicken(worldObj);
            smartChicken.setPositionAndRotation(chicken.posX, chicken.posY, chicken.posZ, chicken.rotationYaw, chicken.rotationPitch);
            smartChicken.onInitialSpawn(worldObj.getDifficultyForLocation(chicken.getPosition()), null);
            smartChicken.setChickenType(50);
            if (chicken.hasCustomName()) {
                smartChicken.setCustomNameTag(chicken.getCustomNameTag());
            }

            worldObj.removeEntity(chicken);
            worldObj.spawnEntityInWorld(smartChicken);
            smartChicken.spawnExplosionParticle();

            event.setCanceled(true);
        }
    }
}
