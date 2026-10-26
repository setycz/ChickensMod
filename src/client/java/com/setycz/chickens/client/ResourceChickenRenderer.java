package com.setycz.chickens.client;
import com.setycz.chickens.*;
import net.minecraft.client.model.animal.chicken.AdultChickenModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.state.ChickenRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
/** All legacy breed textures use the temperate adult UV layout, including scaled chicks. */
public final class ResourceChickenRenderer extends MobRenderer<ResourceChicken,ChickenRenderState,AdultChickenModel> {
 private final Identifier texture;
 public ResourceChickenRenderer(EntityRendererProvider.Context ctx,Breed b){super(ctx,new AdultChickenModel(ctx.bakeLayer(ModelLayers.CHICKEN)),.3f);texture=b.texture;}
 @Override public Identifier getTextureLocation(ChickenRenderState state){return texture;}
 @Override public ChickenRenderState createRenderState(){return new ChickenRenderState();}
 @Override public void extractRenderState(ResourceChicken entity,ChickenRenderState state,float partial){
  super.extractRenderState(entity,state,partial);
  state.flap=Mth.lerp(partial,entity.oFlap,entity.flap);state.flapSpeed=Mth.lerp(partial,entity.oFlapSpeed,entity.flapSpeed);
 }
}
