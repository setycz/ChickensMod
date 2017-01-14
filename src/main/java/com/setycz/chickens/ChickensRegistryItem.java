package com.setycz.chickens;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Created by setyc on 12.02.2016.
 */
public class ChickensRegistryItem {
    private final int id;
    private final String entityName;
    private ItemStack layItem;
    private ItemStack dropItem;
    private final int bgColor;
    private final int fgColor;
    private final ResourceLocation texture;
    private ChickensRegistryItem parent1;
    private ChickensRegistryItem parent2;
    private SpawnType spawnType;
    private boolean isEnabled = true;
    private float layCoefficient = 1.0f;

    public ChickensRegistryItem(int id, String entityName, ResourceLocation texture, ItemStack layItem, int bgColor, int fgColor) {
        this(id, entityName, texture, layItem, bgColor, fgColor, null, null);
    }

    public ChickensRegistryItem(int id, String entityName, ResourceLocation texture, ItemStack layItem, int bgColor, int fgColor, @Nullable ChickensRegistryItem parent1, @Nullable ChickensRegistryItem parent2) {
        this.id = id;
        this.entityName = entityName;
        this.layItem = layItem;
        this.bgColor = bgColor;
        this.fgColor = fgColor;
        this.texture = texture;
        this.spawnType = SpawnType.NORMAL;
        this.parent1 = parent1;
        this.parent2 = parent2;
    }

    public ChickensRegistryItem setDropItem(ItemStack stack) {
        dropItem = stack;
        return this;
    }

    public ChickensRegistryItem setSpawnType(SpawnType type) {
        spawnType = type;
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
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
        return layItem.copy();
    }

    public ItemStack createDropItem() {
        if (dropItem != null) {
            return dropItem.copy();
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
        return layItem.getItem() == Items.DYE && layItem.getMetadata() == dyeMetadata;
    }

    public int getDyeMetadata() {
        return layItem.getMetadata();
    }

    public boolean canSpawn() {
        return getTier() == 1 && spawnType != SpawnType.NONE;
    }

    public int getId() {
        return id;
    }

    public int getMinLayTime() {
        return (int) Math.max(6000 * getTier() * layCoefficient, 1.0f);
    }

    public int getMaxLayTime() {
        return 2 * getMinLayTime();
    }

    public SpawnType getSpawnType() {
        return spawnType;
    }

    public boolean isImmuneToFire() {
        return spawnType == SpawnType.HELL;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean isEnabled() {
        return !(!isEnabled
                || parent1 != null && !parent1.isEnabled()
                || parent2 != null && !parent2.isEnabled());
    }

    public void setLayItem(ItemStack itemStack) {
        layItem = itemStack;
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
    @SuppressWarnings("UnusedReturnValue")
    public void setParents(ChickensRegistryItem parent1, ChickensRegistryItem parent2) {
        this.parent1 = parent1;
        this.parent2 = parent2;
    }

    public boolean isBreedable() {
        return parent1 != null && parent2 != null;
    }
}
