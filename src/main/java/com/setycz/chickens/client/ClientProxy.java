package com.setycz.chickens.client;

import com.setycz.chickens.ChickensMod;
import com.setycz.chickens.client.model.ModelChickensChicken;
import com.setycz.chickens.client.render.RenderChickensChicken;
import com.setycz.chickens.client.render.RenderThrownEgg.EntityColoredEggFactory;
import com.setycz.chickens.common.CommonProxy;
import com.setycz.chickens.entity.EntityChickensChicken;
import com.setycz.chickens.entity.EntityColoredEgg;
import com.setycz.chickens.handler.ItemColorHandler;
import com.setycz.chickens.registry.ChickensRegistry;
import com.setycz.chickens.registry.ChickensRegistryItem;
import com.setycz.chickens.registry.LiquidEggRegistryItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by setyc on 18.02.2016.
 */
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

	
	@Override 
	public void preInit() {

		RenderingRegistry.registerEntityRenderingHandler(EntityColoredEgg.class, new EntityColoredEggFactory());
	}
	
    @Override
    public void init() {
        super.init();

        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(
                new ItemColorHandler(),
                ChickensMod.spawnEgg, ChickensMod.coloredEgg, ChickensMod.liquidEgg);

        // chicken entity registration
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        renderManager.entityRenderMap.put(EntityChickensChicken.class, new RenderChickensChicken(renderManager, new ModelChickensChicken()));
        
          //noinspection ConstantConditions
        registerItemModel(Item.getItemFromBlock(ChickensMod.henhouse), 0);
        //noinspection ConstantConditions
        registerItemModel(Item.getItemFromBlock(ChickensMod.henhouse_acacia), 0);
        //noinspection ConstantConditions
        registerItemModel(Item.getItemFromBlock(ChickensMod.henhouse_birch), 0);
        //noinspection ConstantConditions
        registerItemModel(Item.getItemFromBlock(ChickensMod.henhouse_dark_oak), 0);
        //noinspection ConstantConditions
        registerItemModel(Item.getItemFromBlock(ChickensMod.henhouse_jungle), 0);
        //noinspection ConstantConditions
        registerItemModel(Item.getItemFromBlock(ChickensMod.henhouse_spruce), 0);
        
        registerItemModel(ChickensMod.spawnEgg, 0);

        registerItemModel(ChickensMod.analyzer, 0);
        
        for (ChickensRegistryItem chicken : ChickensRegistry.getItems()) {		
        	registerChicken(chicken);		
         }
    }

    private void registerItemModel(Item item, int meta) {
        ModelResourceLocation resourceLocation = new ModelResourceLocation(ChickensMod.MODID + ":" + ChickensMod.getItemName(item), "inventory");
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta, resourceLocation);
    }

    @Override
    public void registerLiquidEgg(LiquidEggRegistryItem liquidEgg) {
        super.registerLiquidEgg(liquidEgg);

        registerItemModel(ChickensMod.liquidEgg, liquidEgg.getId());
    }

    public void registerChicken(ChickensRegistryItem chicken) {
        //registerItemModel(ChickensMod.spawnEgg, chicken.getId());

        if (chicken.isDye()) {
            registerItemModel(ChickensMod.coloredEgg, chicken.getDyeMetadata());
        }
    }
}
