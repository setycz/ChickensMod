package com.setycz.chickens.client;
import com.setycz.chickens.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.state.ChickenRenderState;
import net.minecraft.resources.Identifier;
public final class ChickensClient implements ClientModInitializer {
 @Override public void onInitializeClient(){
  for(Breed breed:Breeds.ALL.values())EntityRendererRegistry.register(breed.entityType,context->new ResourceChickenRenderer(context,breed));
  EntityRendererRegistry.register(ChickensMod.COLORED_EGG_ENTITY,ThrownItemRenderer::new);
 }
}
