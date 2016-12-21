package com.setycz.chickens.analyzer;

import com.setycz.chickens.chicken.EntityChickensChicken;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by setyc on 21.12.2016.
 */
public class ItemAnalyzer extends Item {
    public ItemAnalyzer() {
        setMaxStackSize(1);
        setMaxDamage(238);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        tooltip.add(I18n.translateToLocal("item.analyzer.tooltip1"));
        tooltip.add(I18n.translateToLocal("item.analyzer.tooltip2"));
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
        if (target.worldObj.isRemote || !(target instanceof EntityChickensChicken)) {
            return false;
        }

        EntityChickensChicken chicken = (EntityChickensChicken) target;
        chicken.setStatsAnalyzed(true);

        TextComponentString chickenName = new TextComponentString(chicken.getName());
        chickenName.getStyle().setBold(true).setColor(TextFormatting.GOLD);
        playerIn.addChatMessage(chickenName);

        playerIn.addChatMessage(new TextComponentTranslation("entity.ChickensChicken.tier", chicken.getTier()));

        playerIn.addChatMessage(new TextComponentTranslation("entity.ChickensChicken.growth", chicken.getGrowth()));
        playerIn.addChatMessage(new TextComponentTranslation("entity.ChickensChicken.gain", chicken.getGain()));
        playerIn.addChatMessage(new TextComponentTranslation("entity.ChickensChicken.strength", chicken.getStrength()));

        if (!chicken.isChild()) {
            int layProgress = chicken.getLayProgress();
            if (layProgress <= 0) {
                playerIn.addChatMessage(new TextComponentTranslation("entity.ChickensChicken.nextEggSoon"));
            } else {
                playerIn.addChatMessage(new TextComponentTranslation("entity.ChickensChicken.layProgress", layProgress));
            }
        }

        stack.damageItem(1, target);
        return true;
    }
}
