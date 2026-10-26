package com.timwoodcreates.roost.client;
import com.timwoodcreates.roost.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
public final class RoostClient implements ClientModInitializer {
 @Override public void onInitializeClient(){
  RoostMod.MENUS.forEach(type->MenuScreens.register(type,MachineScreen::new));
  ItemTooltipCallback.EVENT.register((stack,context,flag,lines)->{
   var bird=ChickenItem.bird(stack);if(bird==null)return;
   lines.add(Component.literal(bird.breed==null?"Vanilla Chicken":bird.breed.name));
   if(bird.breed!=null)lines.add(Component.literal("Growth "+ChickenItem.stat(stack,"Growth")+" | Gain "+ChickenItem.stat(stack,"Gain")+" | Strength "+ChickenItem.stat(stack,"Strength")));
   lines.add(Component.literal("Place in a Roost or Breeder; use on the ground to release."));
  });
 }
}
