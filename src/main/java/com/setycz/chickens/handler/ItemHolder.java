package com.setycz.chickens.handler;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.setycz.chickens.ChickensMod;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

public class ItemHolder
{
	private String source = null;
	
	private String itemID;
	private int metaID;
	private NBTTagCompound nbtData;
	private JsonObject nbtRawJson;
	
	private boolean isComplete = false;
	
	private ItemStack stack =  ItemStack.EMPTY;
	
	private int stackSize = 1;
	
	public static HashMap<String, Integer> ErroredItems = new HashMap<String, Integer>();
	
	Gson gson = new Gson();
	
	public ItemHolder(){
		itemID = Items.AIR.getRegistryName().toString();
		metaID = 0;
		nbtData = null;
		stack = ItemStack.EMPTY;
	}
	
	public ItemHolder(Item itemIn) {
		itemID = itemIn.getRegistryName().toString();
		metaID = 0;
		nbtData = null;
		stack = ItemStack.EMPTY;
	}
	
	public ItemHolder(ItemStack stackIn, boolean isFinal){
		itemID = stackIn.getItem().getRegistryName().toString();
		metaID = stackIn.getMetadata();
		stack = stackIn;
		nbtData = stackIn.hasTagCompound() ?  stackIn.getTagCompound() : null;
		stackSize = stackIn.getCount();
		isComplete = isFinal;
	}
	
	public ItemHolder(String itemID, int metaID, int qty) {
		this.itemID = itemID;
		this.metaID = metaID;
		this.nbtData = null;
	}
	
	public boolean hasSource() {
		return this.source != null;
	}
	
	public String getSource() {
		return this.source;
	}
	
	public ItemHolder setSource(String sourceIn) {
		this.source = sourceIn;
		return this;
	}

	@Nullable
	public Item getItem() {
		return Item.getByNameOrId(this.itemID);
	}
	
	public int getStackSize() {
		return !this.stack.isEmpty() ? this.stack.getCount() : this.stackSize;
	}
	
	public int getMeta() {
		return this.metaID;
	}
	
	public int getAmount() {
		return stackSize;
	}
	
	/**
	 * Get or Create itemstack for this Loot Item
	 * @return
	 */
	public ItemStack getStack() {
		
		
		if(ErroredItems.containsKey(this.itemID)) {
			handleItemNotFound();
		}
		
		if(!isComplete) {
			isComplete = true;
			Item item = getItem();
			if(item != null) {
				stack = new ItemStack(getItem(), this.getAmount(), this.metaID);
				
				if(this.nbtData != null && !this.nbtData.isEmpty())
	                	stack.setTagCompound(this.nbtData);
			}else {
				handleItemNotFound();
			}
		}

		//System.out.println("Getting: "+ stack.getDisplayName());
		return stack.copy();
	}
	
	private void handleItemNotFound() {
		
		if(!ErroredItems.containsKey(this.itemID)) 
			ErroredItems.put(this.itemID, 1);
		else
			ErroredItems.replace(this.itemID, ErroredItems.get(this.itemID) + 1);

		if(ErroredItems.get(this.itemID) <= 3) {
			ChickensMod.log.error("Could not find specfied Item: ["+ this.itemID +"]"+(this.hasSource() ? " | Source: ["+ this.getSource()+"]" : "")+ " | Dropping Default Item: ["+ this.stack.getDisplayName()+"]");
			if(ErroredItems.get(this.itemID) == 3)
				ChickensMod.log.error("Will silent error this itemID: ["+ this.itemID +"]");
		}
	}
	
	public ItemHolder readJsonObject(JsonObject data) throws NumberFormatException {
		itemID =  data.has("itemID") ? data.get("itemID").getAsString() : Items.AIR.getRegistryName().toString();
		metaID =  data.has("metaID") ? data.get("metaID").getAsInt() : 0;
		stackSize = data.has("qty") ? data.get("qty").getAsInt() : 1;
		
		nbtRawJson =  data.has("nbt")  ? data.get("nbt").getAsJsonObject() : null; 
			
		try {
			nbtData = data.has("nbt")  ? JsonToNBT.getTagFromJson(data.get("nbt").getAsJsonObject().toString()) : null;
		} catch (NBTException e) {
			e.printStackTrace();
			nbtData = null;
		}
		
		return this;
	}
	
	public JsonObject writeJsonObject(JsonObject data) throws NumberFormatException {
		data.addProperty("itemID", itemID);
		data.addProperty("metaID", metaID);
		
		if(stackSize > 1)
			data.addProperty("qty", getStackSize());
		
		if(nbtData != null && !nbtData.isEmpty()) {
			JsonElement element = gson.fromJson(nbtData.toString(), JsonElement.class);
			data.add("nbt", element.getAsJsonObject());
		}
		
		return data;
	}
	
	@Override
	public String toString() {
		return this.itemID +":"+this.metaID+":"+this.stackSize+(this.nbtData != null ? ":"+this.nbtData.toString() : "") ;
	}
}
