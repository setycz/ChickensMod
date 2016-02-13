package com.setycz.chickens;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

/**
 * Created by setyc on 12.02.2016.
 */
public class ChickensRegistryItem {
    private final String entityName;
    private final Item dropItem;
    private final Item layItem;
    private final int bgColor;
    private final int fgColor;
    private final ResourceLocation texture;

    public ChickensRegistryItem(String entityName, ResourceLocation texture, Item layItem, Item dropItem, int bgColor, int fgColor) {
        this.entityName = entityName;
        this.dropItem = dropItem;
        this.layItem = layItem;
        this.bgColor = bgColor;
        this.fgColor = fgColor;
        this.texture = texture;
    }

    public String getEntityName() {
        return entityName;
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

    public Item getDropItem() {
        return dropItem;
    }

    public Item getLayItem() {
        return layItem;
    }
}
