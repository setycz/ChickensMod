package com.setycz.chickens;

import net.minecraft.block.Block;
import net.minecraftforge.fluids.Fluid;

/**
 * Created by setyc on 14.02.2016.
 */
public class LiquidEggRegistryItem {
    private final int id;
    private final Block liquid;
    private final int eggColor;
    private final Fluid fluid;

    public LiquidEggRegistryItem(int id, Block liquid, int eggColor, Fluid fluid) {
        this.id = id;
        this.liquid = liquid;
        this.eggColor = eggColor;
        this.fluid = fluid;
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

    public Fluid getFluid() {
        return fluid;
    }
}
