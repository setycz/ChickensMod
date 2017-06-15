package com.setycz.chickens.coloredEgg;

import com.setycz.chickens.ChickensRegistry;
import com.setycz.chickens.ChickensRegistryItem;
import com.setycz.chickens.IColorSource;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by setyc on 13.02.2016.
 */
public class ItemColoredEgg extends ItemEgg implements IColorSource {
    public ItemColoredEgg() {
        setHasSubtypes(true);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        tooltip.add(I18n.translateToLocal("item.colored_egg.tooltip"));
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        EnumDyeColor color = EnumDyeColor.byDyeDamage(stack.getMetadata());
        String unlocalizedName = color.getUnlocalizedName();
        // hotfix for compatibility with MoreChickens
        if (unlocalizedName.equals("silver")) {
            unlocalizedName += "Dye";
        }
        return I18n.translateToLocal(getUnlocalizedName() + "." + unlocalizedName + ".name");
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
        for (ChickensRegistryItem chicken : ChickensRegistry.getItems()) {
            if (chicken.isDye()) {
                subItems.add(new ItemStack(itemIn, 1, chicken.getDyeMetadata()));
            }
        }
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        return EnumDyeColor.byDyeDamage(stack.getMetadata()).getMapColor().colorValue;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
        ItemStack itemStackIn = playerIn.getHeldItem(hand);

        if (!playerIn.capabilities.isCreativeMode) {
            itemStackIn.shrink(1);
        }

        worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_EGG_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if (!worldIn.isRemote) {
            String chickenType = getChickenType(itemStackIn);
            if (chickenType != null) {
                EntityColoredEgg entityIn = new EntityColoredEgg(worldIn, playerIn);
                entityIn.setHeadingFromThrower(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
                entityIn.setChickenType(chickenType);
                worldIn.spawnEntity(entityIn);
            }
        }

        //noinspection ConstantConditions
        playerIn.addStat(StatList.getObjectUseStats(this));
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
    }

    public String getChickenType(ItemStack itemStack) {
        ChickensRegistryItem chicken = ChickensRegistry.findDyeChicken(itemStack.getMetadata());
        if (chicken == null) {
            return null;
        }
        return chicken.getRegistryName().toString();
    }
}
