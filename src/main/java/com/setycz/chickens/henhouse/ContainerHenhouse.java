package com.setycz.chickens.henhouse;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Created by setyc on 06.03.2016.
 */
@SuppressWarnings("WeakerAccess")
public class ContainerHenhouse extends Container {

    private final TileEntityHenhouse tileEntityHenhouse;
    private int energy;

    public ContainerHenhouse(InventoryPlayer playerInventory, TileEntityHenhouse tileEntityHenhouse) {
        this.tileEntityHenhouse = tileEntityHenhouse;

        this.addSlotToContainer(new Slot(tileEntityHenhouse, TileEntityHenhouse.hayBaleSlotIndex, 25, 19));
        this.addSlotToContainer(new Slot(tileEntityHenhouse, TileEntityHenhouse.dirtSlotIndex, 25, 55));

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                this.addSlotToContainer(new Slot(
                        tileEntityHenhouse,
                        TileEntityHenhouse.firstItemSlotIndex + (row * 3) + column,
                        98 + column * 18,
                        17 + row * 18));
            }
        }

        for (int l = 0; l < 3; ++l) {
            for (int k = 0; k < 9; ++k) {
                this.addSlotToContainer(new Slot(playerInventory, k + l * 9 + 9, 8 + k * 18, l * 18 + 84));
            }
        }

        for (int i1 = 0; i1 < 9; ++i1) {
            this.addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 142));
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
            ItemStack itemStack1 = slot.getStack();
            assert itemStack1 != null;
            itemstack = itemStack1.copy();

            if (index < this.tileEntityHenhouse.getSizeInventory()) {
                if (!this.mergeItemStack(itemStack1, this.tileEntityHenhouse.getSizeInventory(), this.inventorySlots.size(), true)) {
                    return null;
                }
            } else if (!this.mergeItemStack(itemStack1, 0, this.tileEntityHenhouse.getSizeInventory(), false)) {
                return null;
            }

            if (itemStack1.stackSize == 0) {
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

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for (IContainerListener listener : listeners) {
            if (energy != tileEntityHenhouse.getField(0)) {
                listener.sendProgressBarUpdate(this, 0, tileEntityHenhouse.getField(0));
            }
        }

        energy = tileEntityHenhouse.getField(0);
    }

    @Override
    public void updateProgressBar(int id, int data) {
        tileEntityHenhouse.setField(id, data);
    }
}
