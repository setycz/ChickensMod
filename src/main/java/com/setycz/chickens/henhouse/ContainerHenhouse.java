package com.setycz.chickens.henhouse;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Created by setyc on 06.03.2016.
 */
public class ContainerHenhouse extends Container {

    private final InventoryPlayer playerInventory;
    private final TileEntityHenhouse tileEntityHenhouse;

    public ContainerHenhouse(InventoryPlayer playerInventory, TileEntityHenhouse tileEntityHenhouse) {
        this.playerInventory = playerInventory;
        this.tileEntityHenhouse = tileEntityHenhouse;
        int i = 51;

        for (int j = 0; j < tileEntityHenhouse.getSizeInventory(); ++j) {
            this.addSlotToContainer(new Slot(tileEntityHenhouse, j, 44 + j * 18, 20));
        }

        for (int l = 0; l < 3; ++l) {
            for (int k = 0; k < 9; ++k) {
                this.addSlotToContainer(new Slot(playerInventory, k + l * 9 + 9, 8 + k * 18, l * 18 + i));
            }
        }

        for (int i1 = 0; i1 < 9; ++i1) {
            this.addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 58 + i));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return tileEntityHenhouse.isUseableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {

        ItemStack itemstack = null;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < this.tileEntityHenhouse.getSizeInventory()) {
                if (!this.mergeItemStack(itemstack1, this.tileEntityHenhouse.getSizeInventory(), this.inventorySlots.size(), true)) {
                    return null;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, this.tileEntityHenhouse.getSizeInventory(), false)) {
                return null;
            }

            if (itemstack1.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        tileEntityHenhouse.closeInventory(playerIn);
    }
}
