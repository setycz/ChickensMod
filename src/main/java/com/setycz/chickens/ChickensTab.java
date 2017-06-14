package com.setycz.chickens;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * Created by setyc on 12.02.2016.
 */
public class ChickensTab extends CreativeTabs {

    public ChickensTab() {
        super("chickens");
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(Items.CHICKEN);
    }
}
