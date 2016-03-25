package com.setycz.chickens;

import net.minecraft.item.ItemStack;

/**
 * Created by setyc on 25.03.2016.
 */
public interface IColorSource {
    int getColorFromItemStack(ItemStack stack, int renderPass);
}
