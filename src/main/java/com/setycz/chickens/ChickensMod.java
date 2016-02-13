package com.setycz.chickens;

import com.setycz.chickens.chicken.EntityGunpowderChicken;
import com.setycz.chickens.chicken.ModelChickenBase;
import com.setycz.chickens.chicken.RenderChickenBase;
import com.setycz.chickens.spawnEgg.ItemSpawnEgg;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

/**
 * Created by setyc on 12.02.2016.
 */
@Mod(modid = ChickensMod.MODID, version = ChickensMod.VERSION)
public class ChickensMod {
    public static final String MODID = "chickens";
    public static final String VERSION = "1.0";

    private static final CreativeTabs tab = new ChickensTab("chickens");

    private static final Item spawnEgg = new ItemSpawnEgg().setUnlocalizedName("spawn_egg").setCreativeTab(tab);


    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        ChickensRegistry.register(new ChickensRegistryItem(30000, EntityGunpowderChicken.class, "GunpowderChicken", 0x0000ff, 0x00ff00));

        GameRegistry.registerItem(spawnEgg, getItemName(spawnEgg));

        List<ChickensRegistryItem> chickens = ChickensRegistry.getItems();
        for (int chickenIndex=0; chickenIndex < chickens.size(); chickenIndex++) {
            registerChicken(chickenIndex, chickens.get(chickenIndex), event);
        }
    }

    private void registerChicken(int index, ChickensRegistryItem chickenDescription, FMLInitializationEvent event) {
        EntityRegistry.registerModEntity(chickenDescription.getEntityClass(), chickenDescription.getEntityName(), chickenDescription.getId(), this, 64, 3, true);

        if (event.getSide() == Side.CLIENT) {
            ModelResourceLocation resourceLocation = new ModelResourceLocation(MODID + ":" + getItemName(spawnEgg), "inventory");
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(spawnEgg, index, resourceLocation);

            RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
            renderManager.entityRenderMap.put(chickenDescription.getEntityClass(), new RenderChickenBase(renderManager, new ModelChickenBase(), 0.3F));
        }
    }

    private String getItemName(Item item) {
        return item.getUnlocalizedName().substring(5);
    }
}
