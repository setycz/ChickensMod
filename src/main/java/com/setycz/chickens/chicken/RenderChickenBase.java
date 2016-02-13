package com.setycz.chickens.chicken;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderChicken;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

/**
 * Created by setyc on 12.02.2016.
 */
public class RenderChickenBase extends RenderLiving<EntityGunpowderChicken>  {

    public RenderChickenBase(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityGunpowderChicken entity) {
        return entity.getTexture();
    }

    @Override
    protected float handleRotationFloat(EntityGunpowderChicken livingBase, float partialTicks) {
        float f = livingBase.field_70888_h + (livingBase.wingRotation - livingBase.field_70888_h) * partialTicks;
        float f1 = livingBase.field_70884_g + (livingBase.destPos - livingBase.field_70884_g) * partialTicks;
        return (MathHelper.sin(f) + 1.0F) * f1;
    }
}
