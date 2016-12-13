package com.setycz.chickens.chicken;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by setyc on 12.02.2016.
 */
@SideOnly(Side.CLIENT)
public class RenderChickensChicken extends RenderLiving<EntityChickensChicken> {

    public RenderChickensChicken(RenderManager renderManagerIn, ModelBase modelBaseIn) {
        super(renderManagerIn, modelBaseIn, 0.3F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityChickensChicken entity) {
        return entity.getTexture();
    }

    @Override
    protected float handleRotationFloat(EntityChickensChicken livingBase, float partialTicks) {
        float f = livingBase.oFlap + (livingBase.wingRotation - livingBase.oFlap) * partialTicks;
        float f1 = livingBase.oFlapSpeed + (livingBase.destPos - livingBase.oFlapSpeed) * partialTicks;
        return (MathHelper.sin(f) + 1.0F) * f1;
    }
}
