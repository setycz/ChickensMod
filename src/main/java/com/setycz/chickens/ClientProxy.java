package com.setycz.chickens;

import com.setycz.chickens.chicken.EntityChickensChicken;
import com.setycz.chickens.chicken.ModelChickensChicken;
import com.setycz.chickens.chicken.RenderChickensChicken;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.model.ModelResourceLocation;

/**
 * Created by setyc on 18.02.2016.
 */
public class ClientProxy extends CommonProxy {
    @Override
    public void init() {
        super.init();

        // chicken entity registration
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        renderManager.entityRenderMap.put(EntityChickensChicken.class, new RenderChickensChicken(renderManager, new ModelChickensChicken(), 0.3F));

        // liquid egg
        ModelResourceLocation eggResourceLocation = new ModelResourceLocation(ChickensMod.MODID + ":" + ChickensMod.getItemName(ChickensMod.liquidEgg), "inventory");
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(ChickensMod.liquidEgg, 0, eggResourceLocation);
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(ChickensMod.liquidEgg, 1, eggResourceLocation);
    }

    @Override
    public void registerChicken(ChickensRegistryItem chicken) {
        super.registerChicken(chicken);

        ModelResourceLocation spawnResourceLocation = new ModelResourceLocation(ChickensMod.MODID + ":" + ChickensMod.getItemName(ChickensMod.spawnEgg), "inventory");
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(ChickensMod.spawnEgg, chicken.getId(), spawnResourceLocation);

        if (chicken.isDye()) {
            ModelResourceLocation eggResourceLocation = new ModelResourceLocation(ChickensMod.MODID + ":" + ChickensMod.getItemName(ChickensMod.coloredEgg), "inventory");
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(ChickensMod.coloredEgg, chicken.getDyeMetadata(), eggResourceLocation);
        }
    }
}
