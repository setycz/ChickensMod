package com.setycz.chickens;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

/**
 * Created by setyc on 12.02.2016.
 */
public class ChickensTab extends CreativeTabs {

    public ChickensTab(String label) {
        super(label);
    }

    @Override
    public Item getTabIconItem() {
        return Items.CHICKEN;
    }
}
