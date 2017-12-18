package com.setycz.chickens.client.render;

import com.setycz.chickens.ChickensMod;
import com.setycz.chickens.entity.EntityColoredEgg;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderThrownEgg extends RenderSnowball<EntityColoredEgg>{

	
	public RenderThrownEgg(RenderManager renderManagerIn, Item itemIn, RenderItem itemRendererIn) {
		super(renderManagerIn, itemIn, itemRendererIn);
	}

	@Override
    public ItemStack getStackToRender(EntityColoredEgg entityIn)
    {
        return entityIn.getItemStacktoRender();
    }
	
	public static class EntityColoredEggFactory implements IRenderFactory<EntityColoredEgg>{
		@Override
		public Render<? super EntityColoredEgg> createRenderFor(RenderManager manager) {
			return new RenderThrownEgg(manager, ChickensMod.coloredEgg, Minecraft.getMinecraft().getRenderItem());
		}
	}
}
