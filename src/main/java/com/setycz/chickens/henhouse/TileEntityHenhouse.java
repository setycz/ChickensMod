package com.setycz.chickens.henhouse;

import com.setycz.chickens.IInventoryGui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;

/**
 * Created by setyc on 01.03.2016.
 */
public class TileEntityHenhouse extends TileEntity implements IInventory, IInventoryGui {
    private String customName;
    private final ItemStack[] slots = new ItemStack[5];

    public ItemStack pushItemStack(ItemStack stack) {
        ItemStack rest = stack.copy();
        for (int slotIndex = 0; slotIndex < slots.length; slotIndex++) {
            if (slots[slotIndex] == null) {
                slots[slotIndex] = stack;
                return null;
            }
            if (slots[slotIndex].stackSize < getInventoryStackLimit() && slots[slotIndex].isItemEqual(rest)) {
                int toAdd = Math.min(rest.stackSize, getInventoryStackLimit() - slots[slotIndex].stackSize);
                slots[slotIndex].stackSize += toAdd;
                rest.stackSize -= toAdd;
                if (rest.stackSize == 0) {
                    return null;
                }
            }
        }
        return rest;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        if (hasCustomName()) {
            compound.setString("customName", customName);
        }

        NBTTagList items = new NBTTagList();
        for (int slotIndex = 0; slotIndex < slots.length; slotIndex++) {
            ItemStack itemStack = slots[slotIndex];
            if (itemStack != null) {
                NBTTagCompound item = new NBTTagCompound();
                item.setInteger("slot", slotIndex);
                itemStack.writeToNBT(item);
                items.appendTag(item);
            }
        }
        compound.setTag("items", items);

        super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        customName = compound.getString("customName");

        Arrays.fill(slots, null);
        NBTTagList items = compound.getTagList("items", 10);
        for (int itemIndex=0; itemIndex<items.tagCount(); itemIndex++) {
            NBTTagCompound item = items.getCompoundTagAt(itemIndex);
            int slotIndex = item.getInteger("slot");
            ItemStack itemStack = ItemStack.loadItemStackFromNBT(item);
            slots[slotIndex] = itemStack;
        }
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

    @Override
    public Container createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
        return new ContainerHenhouse(inventoryplayer, this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
        return new GuiHenhouse(inventoryplayer, this);
    }
}
