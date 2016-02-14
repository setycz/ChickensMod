package com.setycz.chickens;

import net.minecraft.block.Block;

/**
 * Created by setyc on 14.02.2016.
 */
public class LiquidEggRegistryItem {
    private final int id;
    private final Block liquid;
    private final int eggColor;

    public LiquidEggRegistryItem(int id, Block liquid, int eggColor) {
        this.id = id;
        this.liquid = liquid;
        this.eggColor = eggColor;
    }

    public int getId() {
        return id;
    }

    public Block getLiquid() {
        return liquid;
    }

    public int getEggColor() {
        return eggColor;
    }
}
