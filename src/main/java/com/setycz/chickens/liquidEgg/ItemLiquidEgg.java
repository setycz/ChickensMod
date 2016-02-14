package com.setycz.chickens.liquidEgg;

import com.setycz.chickens.LiquidEggRegistry;
import com.setycz.chickens.LiquidEggRegistryItem;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by setyc on 14.02.2016.
 */
public class ItemLiquidEgg extends ItemEgg {
    public ItemLiquidEgg() {
        setHasSubtypes(true);
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for (LiquidEggRegistryItem liquid : LiquidEggRegistry.getAll()) {
            subItems.add(new ItemStack(itemIn, 1, liquid.getId()));
        }
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        return LiquidEggRegistry.findById(stack.getMetadata()).getEggColor();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        Block liquid = LiquidEggRegistry.findById(stack.getMetadata()).getLiquid();
        return StatCollector.translateToLocal(getUnlocalizedName() + "." + liquid.getUnlocalizedName().substring(5) + ".name");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        Block liquid = LiquidEggRegistry.findById(itemStackIn.getMetadata()).getLiquid();
        ItemStack result = new ItemBucket(liquid).onItemRightClick(itemStackIn, worldIn, playerIn);
        if (result.getItem() == Items.bucket) {
            return itemStackIn.stackSize > 1 ? new ItemStack(itemStackIn.getItem(), itemStackIn.stackSize - 1, itemStackIn.getMetadata()) : null;
        }
        return itemStackIn;
    }
}
