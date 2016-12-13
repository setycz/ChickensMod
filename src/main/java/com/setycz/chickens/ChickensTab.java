package com.setycz.chickens;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

/**
 * Created by setyc on 12.02.2016.
 */
@SuppressWarnings("WeakerAccess")
public class ChickensTab extends CreativeTabs {

    public ChickensTab() {
        super("chickens");
    }

    @Override
    public Item getTabIconItem() {
        return Items.CHICKEN;
    }
}
