package com.setycz.chickens.coloredEgg;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by setyc on 13.02.2016.
 */
public class ItemColoredEgg extends ItemEgg {
    public ItemColoredEgg() {
        setHasSubtypes(true);
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        subItems.add(new ItemStack(itemIn, 1, EnumDyeColor.BLACK.getDyeDamage()));
        subItems.add(new ItemStack(itemIn, 1, EnumDyeColor.BLUE.getDyeDamage()));
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass)
    {
        return EnumDyeColor.byDyeDamage(stack.getMetadata()).getMapColor().colorValue;
    }
}
