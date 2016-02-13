package com.setycz.chickens;

import net.minecraft.entity.Entity;

/**
 * Created by setyc on 12.02.2016.
 */
public class ChickensRegistryItem {
    private final int id;
    private final Class<? extends Entity> entityClass;
    private final String entityName;
    private final int bgColor;
    private final int fgColor;

    public ChickensRegistryItem(int id, Class<? extends Entity> entityClass, String entityName, int bgColor, int fgColor) {
        this.id = id;
        this.entityClass = entityClass;
        this.entityName = entityName;
        this.bgColor = bgColor;
        this.fgColor = fgColor;
    }

    public Class<? extends Entity> getEntityClass() {
        return entityClass;
    }

    public int getId() {
        return id;
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
}
