package com.setycz.chickens.api.registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import com.setycz.chickens.api.properties.ChickenProperites;
import com.setycz.chickens.api.properties.ItemHolder;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Created by setyc on 12.02.2016.
 */
public class ChickensRegistryItem {
    private final ResourceLocation registryName;
    
    private final String entityName;
    private ItemHolder layItem;
    private ItemHolder dropItem;
    private final int bgColor;
    private final int fgColor;
    private final ResourceLocation texture;
    private ChickensRegistryItem parent1;
    private ChickensRegistryItem parent2;
    private String spawnType;

    private boolean isEnabled = true;
    private float layCoefficient = 1.0f;
    
    //TODO New properies
    /**
     * Giving special properties like fire immunity
     */
    private List<ChickenProperites> specialProperties = new ArrayList<ChickenProperites>();
    
    public ChickensRegistryItem(ResourceLocation registryName, String entityName, ResourceLocation texture, ItemStack layItem, int bgColor, int fgColor) {
        this(registryName, entityName, texture, layItem, bgColor, fgColor, null, null);
    }
    
    public ChickensRegistryItem(ResourceLocation registryName, String entityName, ResourceLocation texture, ItemStack layItem, int bgColor, int fgColor, @Nullable ChickensRegistryItem parent1, @Nullable ChickensRegistryItem parent2)   {
    	this(registryName, entityName, texture, new ItemHolder(layItem, false), bgColor, fgColor, parent1, parent2);
    }
    
    public ChickensRegistryItem(ResourceLocation registryName, String entityName, ResourceLocation texture, ItemHolder layItem, int bgColor, int fgColor, @Nullable ChickensRegistryItem parent1, @Nullable ChickensRegistryItem parent2) {
        this.registryName = registryName;
        this.entityName = entityName;
        this.layItem = layItem;
        this.bgColor = bgColor;
        this.fgColor = fgColor;
        this.texture = texture;
        this.spawnType = "normal";
        this.parent1 = parent1;
        this.parent2 = parent2;
    }


    public ItemHolder getDropItemHolder() {
    	return this.dropItem == null ? this.layItem : this.dropItem;
    }
    
    public ItemHolder getLayItemHolder() {
    	return this.layItem;
    }
    
    public ChickensRegistryItem setSpecialProperties(ChickenProperites... props) {
    	specialProperties = Arrays.asList(props);
    	return this;
    }
    
	public List<ChickenProperites> getSpecialProperties() {
		return this.specialProperties;
	}
	
    public ChickensRegistryItem setDropItem(ItemHolder itemHolder) {
        dropItem = itemHolder;
        return this;
    }
    
    @Deprecated
    public ChickensRegistryItem setDropItem(ItemStack itemstack) {
    	return setDropItem(new ItemHolder(itemstack, false));
    }

    public ChickensRegistryItem setSpawnType(String type) {
        spawnType = type;
        return this;
    }

    public ChickensRegistryItem setLayCoefficient(float coef) {
        layCoefficient = coef;
        return this;
    }

    public String getEntityName() {
        return entityName;
    }

    @Nullable
    public ChickensRegistryItem getParent1() {
        return parent1;
    }

    @Nullable
    public ChickensRegistryItem getParent2() {
        return parent2;
    }

    public int getBgColor() {
        return bgColor;
    }

    public int getFgColor() {
        return fgColor;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public ItemStack createLayItem() {
        return layItem.getStack();
    }
    
    public ItemStack createDropItem() {
        if (dropItem != null) {
            return dropItem.getStack();
        }
        return createLayItem();
    }

    public int getTier() {
        if (parent1 == null || parent2 == null) {
            return 1;
        }
        return Math.max(parent1.getTier(), parent2.getTier()) + 1;
    }

    public boolean isChildOf(ChickensRegistryItem parent1, ChickensRegistryItem parent2) {
        return this.parent1 == parent1 && this.parent2 == parent2 || this.parent1 == parent2 && this.parent2 == parent1;
    }

    public boolean isDye() {
        return layItem.getItem() == Items.DYE;
    }

    public boolean isDye(int dyeMetadata) {
        return layItem.getItem() == Items.DYE && layItem.getMeta() == dyeMetadata;
    }

    public int getDyeMetadata() {
        return layItem.getMeta();
    }

    public boolean canSpawn() {
        return getTier() == 1 && !spawnType.toLowerCase().trim().equals("none");
    }

    public ResourceLocation getRegistryName(){
    	return registryName;
    }

    public int getMinLayTime() {
        return (int) Math.max(6000 * getTier() * layCoefficient, 1.0f);
    }

    public int getMaxLayTime() {
        return 2 * getMinLayTime();
    }

    public String getSpawnType() {
        return spawnType;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean isEnabled() {
        return !(!isEnabled
                || parent1 != null && !parent1.isEnabled()
                || parent2 != null && !parent2.isEnabled());
    }

    public void setLayItem(ItemHolder itemHolder) {
        layItem = itemHolder;
    }

    @Deprecated
    public void setLayItem(ItemStack itemstack) {
    	setLayItem(new ItemHolder(itemstack, false));
    }
    
    public void setNoParents() {
        parent1 = null;
        parent2 = null;
    }

    public ChickensRegistryItem setParentsNew(ChickensRegistryItem parent1, ChickensRegistryItem parent2) {
        this.parent1 = parent1;
        this.parent2 = parent2;
        return this;
    }

    @Deprecated
    public void setParents(ChickensRegistryItem parent1, ChickensRegistryItem parent2) {
        this.parent1 = parent1;
        this.parent2 = parent2;
    }

    public boolean isBreedable() {
        return parent1 != null && parent2 != null;
    }
    
    
    public static void registerChickens()
    {
    Item.getByNameOrId("");	
    }


    
}
