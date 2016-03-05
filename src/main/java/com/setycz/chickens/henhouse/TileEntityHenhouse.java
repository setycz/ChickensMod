package com.setycz.chickens.henhouse;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;

import javax.print.DocFlavor;
import java.util.List;

/**
 * Created by setyc on 01.03.2016.
 */
public class TileEntityHenhouse extends TileEntity implements ITickable, IInventory {
    private String customName;
    private ItemStack[] slots = new ItemStack[8];

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

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        if (hasCustomName()) {
            compound.setString("customName", customName);
        }
        super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        customName = compound.getString("customName");
    }

    @Override
    public int getSizeInventory() {
        return slots.length;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return slots[index];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = slots[index];
        if (stack == null) {
            return null;
        }
        if (count >= stack.stackSize) {
            slots[index] = null;
            return stack;
        }
        return stack.splitStack(count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = slots[index];
        slots[index] = null;
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        slots[index] = stack;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        for (int slotIndex = 0; slotIndex < slots.length; slotIndex++) {
            slots[slotIndex] = null;
        }
    }

    @Override
    public String getName() {
        return hasCustomName() ? customName : "container.henhouse";
    }

    @Override
    public boolean hasCustomName() {
        return customName != null && customName.length() > 0;
    }

    @Override
    public IChatComponent getDisplayName() {
        return hasCustomName() ? new ChatComponentText(getName()) : new ChatComponentTranslation(getName());
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }
}
