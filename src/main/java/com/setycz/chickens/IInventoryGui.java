package com.setycz.chickens;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by setyc on 06.03.2016.
 */
public interface IInventoryGui {
    Container createContainer(InventoryPlayer inventoryplayer);

    @SideOnly(Side.CLIENT)
    GuiContainer createGui(InventoryPlayer inventoryplayer);
}
