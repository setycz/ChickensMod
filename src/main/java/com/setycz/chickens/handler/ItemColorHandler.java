package com.setycz.chickens.handler;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by setyc on 25.03.2016.
 */
@SideOnly(Side.CLIENT)
public class ItemColorHandler implements IItemColor {
	@Override
	public int colorMultiplier(ItemStack stack, int tintIndex) {
		return ((IColorSource) stack.getItem()).getColorFromItemStack(stack, tintIndex);
	}
}
