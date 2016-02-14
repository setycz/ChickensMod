package com.setycz.chickens;

import com.setycz.chickens.chicken.EntityChickensChicken;
import com.setycz.chickens.chicken.ModelChickensChicken;
import com.setycz.chickens.chicken.RenderChickensChicken;
import com.setycz.chickens.coloredEgg.EntityColoredEgg;
import com.setycz.chickens.coloredEgg.ItemColoredEgg;
import com.setycz.chickens.spawnEgg.ItemSpawnEgg;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
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

    private static final Item coloredEgg = new ItemColoredEgg().setUnlocalizedName("colored_egg").setCreativeTab(tab);

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        ChickensRegistryItem gunpowderChicken = new ChickensRegistryItem(
                "GunpowderChicken", new ResourceLocation("chickens", "textures/entity/GunpowderChicken.png"),
                new ItemStack(Items.gunpowder),
                0x999999, 0x404040);
        ChickensRegistry.register(gunpowderChicken);

        ChickensRegistry.register(new ChickensRegistryItem(
                "GlowstoneChicken", new ResourceLocation("chickens", "textures/entity/GlowstoneChicken.png"),
                new ItemStack(Items.glowstone_dust),
                0xffff66, 0xffff00));

        ChickensRegistry.register(new ChickensRegistryItem(
                "BlackChicken", new ResourceLocation("chickens", "textures/entity/BlackChicken.png"),
                new ItemStack(Items.dye, 1, EnumDyeColor.BLACK.getDyeDamage()),
                0x666666, 0x333333));

        ChickensRegistry.register(new ChickensRegistryItem(
                "BlueChicken", new ResourceLocation("chickens", "textures/entity/BlueChicken.png"),
                new ItemStack(Items.dye, 1, EnumDyeColor.BLUE.getDyeDamage()),
                0x000066, 0x000033));

        ChickensRegistry.register(new ChickensRegistryItem(
                "GreenChicken", new ResourceLocation("chickens", "textures/entity/GreenChicken.png"),
                new ItemStack(Items.dye, 1, EnumDyeColor.GREEN.getDyeDamage()),
                0x006600, 0x003300));

        ChickensRegistry.register(new ChickensRegistryItem(
                "YellowChicken", new ResourceLocation("chickens", "textures/entity/YellowChicken.png"),
                new ItemStack(Items.dye, 1, EnumDyeColor.YELLOW.getDyeDamage()),
                0xffff00, 0xcccc00));

        ChickensRegistryItem redChicken = new ChickensRegistryItem(
                "RedChicken", new ResourceLocation("chickens", "textures/entity/RedChicken.png"),
                new ItemStack(Items.dye, 1, EnumDyeColor.RED.getDyeDamage()),
                0x660000, 0x330000);
        ChickensRegistry.register(redChicken);

        ChickensRegistry.register(new ChickensRegistryItem(
                "RedstoneChicken", new ResourceLocation("chickens", "textures/entity/RedstoneChicken.png"),
                new ItemStack(Items.redstone),
                0xe60000, 0x800000,
                redChicken, gunpowderChicken
                ));

        // item registration
        GameRegistry.registerItem(coloredEgg, getItemName(coloredEgg));
        GameRegistry.registerItem(spawnEgg, getItemName(spawnEgg));

        // chicken entity registration
        EntityRegistry.registerModEntity(EntityChickensChicken.class, CHICKEN, 30000, this, 64, 3, true);
        if (event.getSide() == Side.CLIENT) {
            RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
            renderManager.entityRenderMap.put(EntityChickensChicken.class, new RenderChickensChicken(renderManager, new ModelChickensChicken(), 0.3F));
        }

        // chicken entity spawning
        EntityRegistry.addSpawn(EntityChickensChicken.class, 100, 4, 4, EnumCreatureType.CREATURE,
                BiomeGenBase.plains, BiomeGenBase.forest, BiomeGenBase.birchForest, BiomeGenBase.coldTaiga, BiomeGenBase.jungle);

        // register all chickens to Minecraft
        List<ChickensRegistryItem> chickens = ChickensRegistry.getItems();
        for (int chickenIndex=0; chickenIndex < chickens.size(); chickenIndex++) {
            registerChicken(chickenIndex, chickens.get(chickenIndex), event);
        }
    }

    private void registerChicken(int index, ChickensRegistryItem chicken, FMLInitializationEvent event) {
        if (event.getSide() == Side.CLIENT) {
            ModelResourceLocation spawnResourceLocation = new ModelResourceLocation(MODID + ":" + getItemName(spawnEgg), "inventory");
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(spawnEgg, index, spawnResourceLocation);

            if (chicken.isDye()) {
                ModelResourceLocation eggResourceLocation = new ModelResourceLocation(MODID + ":" + getItemName(coloredEgg), "inventory");
                Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(coloredEgg, chicken.getDyeMetadata(), eggResourceLocation);
            }
        }

        if (chicken.isDye()) {
            GameRegistry.addShapelessRecipe(
                    new ItemStack(coloredEgg, 1, chicken.getDyeMetadata()),
                    new ItemStack(Items.egg), new ItemStack(Items.dye, 1, chicken.getDyeMetadata())
            );
        }
    }

    private String getItemName(Item item) {
        return item.getUnlocalizedName().substring(5);
    }
}
