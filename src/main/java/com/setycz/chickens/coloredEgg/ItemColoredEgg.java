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
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
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
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
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
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        if (!playerIn.capabilities.isCreativeMode) {
            --itemStackIn.stackSize;
        }

        worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_EGG_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if (!worldIn.isRemote) {
            int chickenType = getChickenType(itemStackIn);
            if (chickenType != -1) {
                EntityColoredEgg entityIn = new EntityColoredEgg(worldIn, playerIn);
                entityIn.setHeadingFromThrower(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
                entityIn.setChickenType(chickenType);
                worldIn.spawnEntityInWorld(entityIn);
            }
        }

        //noinspection ConstantConditions
        playerIn.addStat(StatList.getObjectUseStats(this));
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
    }

    public int getChickenType(ItemStack itemStack) {
        ChickensRegistryItem chicken = ChickensRegistry.findDyeChicken(itemStack.getMetadata());
        if (chicken == null) {
            return -1;
        }
        return chicken.getId();
    }
}
