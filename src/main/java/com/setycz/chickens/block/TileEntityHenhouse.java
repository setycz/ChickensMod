package com.setycz.chickens.block;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.setycz.chickens.capabilities.InventoryStroageModifiable;
import com.setycz.chickens.client.gui.GuiHenhouse;
import com.setycz.chickens.client.gui.IInventoryGui;
import com.setycz.chickens.client.gui.container.ContainerHenhouse;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;

/**
 * Created by setyc on 01.03.2016.
 */
public class TileEntityHenhouse extends TileEntity implements IInventoryGui {
    public static final int hayBaleEnergy = 100;

    public static final int hayBaleSlotIndex = 0;
    public static final int dirtSlotIndex = 1;
    public static final int firstItemSlotIndex = 2;
    private static final int lastItemSlotIndex = 10;
    private static final double HENHOUSE_RADIUS = 0.5;
    private static final double FENCE_THRESHOLD = 0.5;

    private String customName;
    private final InventoryStroageModifiable slots = new InventoryStroageModifiable("container.henhouse", 11)
    		{
    			@Override
    			public boolean canInsertSlot(int slotIndex, ItemStack stackIn)
    			{
    				if(slotIndex == hayBaleSlotIndex && stackIn.getItem() == Item.getItemFromBlock(Blocks.HAY_BLOCK)) 
    					return true;
    				else if(slotIndex == dirtSlotIndex && stackIn.getItem() == Item.getItemFromBlock(Blocks.DIRT))
    					return true;
    				else 
    					return false;
    			}
    			
    		    @Override
    		    public boolean canExtractSlot(int slotIndex)
    		    {
    		    	if(slotIndex == hayBaleSlotIndex)
    		    		return false;
    		    	
    		    	return true;
    		    }
    		};
    		
    private int energy = 0;

    @Nullable
    public static ItemStack pushItemStack(ItemStack itemToLay, World worldObj, Vec3d pos) {
        List<TileEntityHenhouse> henhouses = findHenhouses(worldObj, pos, 4 + HENHOUSE_RADIUS + FENCE_THRESHOLD);
        for (TileEntityHenhouse henhouse : henhouses) {
            itemToLay = henhouse.pushItemStack(itemToLay);
            if (itemToLay.isEmpty()) {
                break;
            }
        }
        return itemToLay;
    }

    private static List<TileEntityHenhouse> findHenhouses(World worldObj, Vec3d pos, double radius) {
        int firstChunkX = MathHelper.floor((pos.x - radius - World.MAX_ENTITY_RADIUS) / 16.0D);
        int lastChunkX = MathHelper.floor((pos.x + radius + World.MAX_ENTITY_RADIUS) / 16.0D);
        int firstChunkY = MathHelper.floor((pos.z - radius - World.MAX_ENTITY_RADIUS) / 16.0D);
        int lastChunkY = MathHelper.floor((pos.z + radius + World.MAX_ENTITY_RADIUS) / 16.0D);

        List<Double> distances = new ArrayList<Double>();
        List<TileEntityHenhouse> result = new ArrayList<TileEntityHenhouse>();
        for (int chunkX = firstChunkX; chunkX <= lastChunkX; ++chunkX) {
            for (int chunkY = firstChunkY; chunkY <= lastChunkY; ++chunkY) {
                Chunk chunk = worldObj.getChunk(chunkX, chunkY);
                for (TileEntity tileEntity : chunk.getTileEntityMap().values()) {
                    if (tileEntity instanceof TileEntityHenhouse) {
                        Vec3d tileEntityPos = new Vec3d(tileEntity.getPos()).add(HENHOUSE_RADIUS, HENHOUSE_RADIUS, HENHOUSE_RADIUS);
                        boolean inRage = testRange(pos, tileEntityPos, radius);
                        if (inRage) {
                            double distance = pos.distanceTo(tileEntityPos);
                            addHenhouseToResults((TileEntityHenhouse) tileEntity, distance, distances, result);
                        }
                    }
                }
            }
        }
        return result;
    }

    private static boolean testRange(Vec3d pos1, Vec3d pos2, double range) {
        return Math.abs(pos1.x - pos2.x) <= range &&
                Math.abs(pos1.y - pos2.y) <= range &&
                Math.abs(pos1.z - pos2.z) <= range;
    }

    private static void addHenhouseToResults(TileEntityHenhouse henhouse, double distance, List<Double> distances, List<TileEntityHenhouse> henhouses) {
        for (int resultIndex = 0; resultIndex < distances.size(); resultIndex++) {
            if (distance < distances.get(resultIndex)) {
                distances.add(resultIndex, distance);
                henhouses.add(resultIndex, henhouse);
                return;
            }
        }
        distances.add(distance);
        henhouses.add(henhouse);
    }

    private ItemStack pushItemStack(ItemStack stack) {

        int capacity = getEffectiveCapacity();
        if (capacity <= 0) {
            return stack;
        }
        
        for (int slotIndex = firstItemSlotIndex; slotIndex <= lastItemSlotIndex; slotIndex++) {
        	
        	if(stack.isEmpty())
        		break;
        	int stackSizePre = stack.getCount();
        		ItemStack simulated = slots.insertItemInternal(slotIndex, stack, true);
        	int powerToUse = stackSizePre - simulated.getCount();
        	
        	if(powerToUse > 0)
        	{
        		consumeEnergy(powerToUse);
        		capacity -= powerToUse;
        		
        		stack = slots.insertItemInternal(slotIndex, stack, false);
        	}
        }
        
        markDirty(); 
        return stack;
    }

    private void consumeEnergy(int amount) {
    	
        while (amount > 0) {
            if (energy == 0) {
                assert !slots.getStackInSlot(hayBaleSlotIndex).isEmpty();
                	slots.extractItemInternal(hayBaleSlotIndex, 1, false);
                energy += hayBaleEnergy;
            }

            int consumed = Math.min(amount, energy);
            energy -= consumed;
            amount -= consumed;

            if (energy <= 0) {
                    slots.insertItemInternal(dirtSlotIndex, new ItemStack(Blocks.DIRT, 1), false);
            }
        }
    }


    private int getEffectiveCapacity() {
        return Math.min(getInputCapacity(), getOutputCapacity());
    }

    private int getInputCapacity() {
        int potential = energy;

        ItemStack hayBaleStack = slots.getStackInSlot(hayBaleSlotIndex);
        if (!hayBaleStack.isEmpty() && hayBaleStack.getItem() == Item.getItemFromBlock(Blocks.HAY_BLOCK)) {
            potential += hayBaleStack.getCount() * hayBaleEnergy;
        }

        return potential;
    }

    private int getOutputCapacity() {
        ItemStack dirtStack = slots.getStackInSlot(dirtSlotIndex);
        if (dirtStack.isEmpty()) {
            return slots.getSlotLimit(dirtSlotIndex) * hayBaleEnergy;
        }
        if (dirtStack.getItem() != Item.getItemFromBlock(Blocks.DIRT)) {
            return 0;
        }
        return (slots.getSlotLimit(dirtSlotIndex) - dirtStack.getCount()) * hayBaleEnergy;
    }
    
    /**
     * Drop all contents
     */
	public void dropContents()
	{
        for (int i = 0; i < this.slots.getSlots(); ++i)
        {
        	ItemStack stack = this.slots.extractItemInternal(i, this.slots.getSlotLimit(i), false);
        	
        	if(!stack.isEmpty())
        	{
        		this.world.spawnEntity(new EntityItem(world, this.pos.getX(), this.pos.getY()+1, this.pos.getZ(), stack));
        	}
        }
	}

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        //TODO I sort of broke this with custom names, I need to fix this later
//        if (slots.hasCustomName()) {
//            compound.setString("customName", customName);
//        }

        compound.setInteger("energy", energy);
        
        slots.writeToNBT(compound);

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        //TODO I sort of broke this with custom names, I need to fix this later
//        customName = compound.getString("customName");

        energy = compound.getInteger("energy");
        
        slots.readFromNBT(compound);
    }

    public int getField(int id) {
        switch (id) {
            case 0:
                return energy;
            default:
                return 0;
        }
    }

    public void setField(int id, int value) {
        switch (id) {
            case 0:
                energy = value;
                break;
        }
    }

    public int getFieldCount() {
        return 1;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation(slots.getName());
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    @Override
    public Container createContainer(InventoryPlayer inventoryplayer) {
        return new ContainerHenhouse(inventoryplayer, this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiContainer createGui(InventoryPlayer inventoryplayer) {
        return new GuiHenhouse(inventoryplayer, this);
    }

    public int getEnergy() {
        return energy;
    }

	@SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) this.slots;
        }
        return super.getCapability(capability, facing);
    }
		
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ||
        super.hasCapability(capability, facing);
    }
}
