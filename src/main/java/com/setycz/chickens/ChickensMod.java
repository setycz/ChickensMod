package com.setycz.chickens;

import com.setycz.chickens.chicken.EntityChickensChicken;
import com.setycz.chickens.chicken.ModelChickensChicken;
import com.setycz.chickens.chicken.RenderChickensChicken;
import com.setycz.chickens.spawnEgg.ItemSpawnEgg;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
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
    public static final String CHICKEN = "ChickensChicken";

    private static final CreativeTabs tab = new ChickensTab("chickens");

    private static final Item spawnEgg = new ItemSpawnEgg().setUnlocalizedName("spawn_egg").setCreativeTab(tab);


    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        ChickensRegistryItem gunpowderChicken = new ChickensRegistryItem(
                "GunpowderChicken", new ResourceLocation("chickens", "textures/entity/GunpowderChicken.png"),
                Items.gunpowder, Item.getItemFromBlock(Blocks.tnt),
                0x999999, 0x404040);
        ChickensRegistry.register(gunpowderChicken);

        ChickensRegistry.register(new ChickensRegistryItem(
                "GlowstoneChicken", new ResourceLocation("chickens", "textures/entity/GlowstoneChicken.png"),
                Items.glowstone_dust, Item.getItemFromBlock(Blocks.glowstone),
                0xffff66, 0xffff00));

        ChickensRegistry.register(new ChickensRegistryItem(
                "BlackChicken", new ResourceLocation("chickens", "textures/entity/BlackChicken.png"),
                Items.dye, Item.getItemFromBlock(Blocks.stone),
                0x666666, 0x333333));

        ChickensRegistry.register(new ChickensRegistryItem(
                "BlueChicken", new ResourceLocation("chickens", "textures/entity/BlueChicken.png"),
                Items.dye, Item.getItemFromBlock(Blocks.stone),
                0x000066, 0x000033));

        ChickensRegistry.register(new ChickensRegistryItem(
                "GreenChicken", new ResourceLocation("chickens", "textures/entity/GreenChicken.png"),
                Items.dye, Item.getItemFromBlock(Blocks.stone),
                0x006600, 0x003300));

        ChickensRegistryItem redChicken = new ChickensRegistryItem(
                "RedChicken", new ResourceLocation("chickens", "textures/entity/RedChicken.png"),
                Items.dye, Item.getItemFromBlock(Blocks.stone),
                0x660000, 0x330000);
        ChickensRegistry.register(redChicken);

        ChickensRegistry.register(new ChickensRegistryItem(
                "RedstoneChicken", new ResourceLocation("chickens", "textures/entity/RedstoneChicken.png"),
                Items.redstone, Item.getItemFromBlock(Blocks.redstone_block),
                0xe60000, 0x800000,
                redChicken, gunpowderChicken
                ));

        // register spawn egg item to Minecraft
        GameRegistry.registerItem(spawnEgg, getItemName(spawnEgg));

        // register chicken entity to Minecraft
        EntityRegistry.registerModEntity(EntityChickensChicken.class, CHICKEN, 30000, this, 64, 3, true);
        if (event.getSide() == Side.CLIENT) {
            RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
            renderManager.entityRenderMap.put(EntityChickensChicken.class, new RenderChickensChicken(renderManager, new ModelChickensChicken(), 0.3F));
        }

        // register all chickens to Minecraft
        List<ChickensRegistryItem> chickens = ChickensRegistry.getItems();
        for (int chickenIndex=0; chickenIndex < chickens.size(); chickenIndex++) {
            registerChicken(chickenIndex, chickens.get(chickenIndex), event);
        }
    }

    private void registerChicken(int index, ChickensRegistryItem chickenDescription, FMLInitializationEvent event) {
        if (event.getSide() == Side.CLIENT) {
            ModelResourceLocation resourceLocation = new ModelResourceLocation(MODID + ":" + getItemName(spawnEgg), "inventory");
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(spawnEgg, index, resourceLocation);
        }
    }

    private String getItemName(Item item) {
        return item.getUnlocalizedName().substring(5);
    }
}
