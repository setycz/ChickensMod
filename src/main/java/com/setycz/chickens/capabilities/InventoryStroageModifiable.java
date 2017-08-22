package com.setycz.chickens.capabilities;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.items.ItemStackHandler;

public class InventoryStroageModifiable extends ItemStackHandler
{
	   private final String name;
	    
	    public InventoryStroageModifiable(int inventorySize)
	    {
	        this(null, inventorySize);
	    }
	 
	    public InventoryStroageModifiable(String name, int inventorySize)
	    {
	        super(inventorySize);
	        this.name = name;
	    }
	 
	    public String getName()
	    {
	        return name;
	    }
	 
	    public boolean hasCustomName()
	    {
	        return name != null;
	    }
	    
	    @Override
	    @Nonnull
	    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
	    {
	    	if(canInsertSlot(slot, stack))
	    		return super.insertItem(slot, stack, simulate);
	    	else 
	    		return stack;
	    }
	    
	    @Override	    
	    @Nonnull
	    public ItemStack extractItem(int slot, int amount, boolean simulate)
	    {
	    	if(canExtractSlot(slot))
	    		return super.extractItem(slot, amount, simulate);
	    	
	    	return ItemStack.EMPTY; 
	    }
	    
	    
	    public ItemStack insertItemInternal(int slot, ItemStack stack, boolean simulate)
	    {
	    	return super.insertItem(slot, stack, simulate);
	    }
	    
	    public ItemStack extractItemInternal(int slot, int amount, boolean simulate)
	    {
	    	return super.extractItem(slot, amount, simulate);
	    }
	    
		public boolean canExtractSlot(int slot)
		{
			return true;
		}
		
		public boolean canInsertSlot(int slot, ItemStack stack)
		{
			return true;
		}
				
	    @Override
	    public int getSlotLimit(int slot)
	    {
	    	if(!this.stacks.get(slot).isEmpty())
	    	{
	    		return this.stacks.get(slot).getMaxStackSize();
	    	}
	        return 64;
	    }
	    
	    public ITextComponent getDisplayName()
	    {
	        return new TextComponentTranslation(name);
	    }
	    
	    
	    public void writeToNBT(NBTTagCompound compound)
	    {
	    	compound.setTag("Inventory", serializeNBT());
        }
	     
	    public void readFromNBT(NBTTagCompound compound)
	    {
	    	deserializeNBT(compound.getCompoundTag("Inventory"));
	    }

}
