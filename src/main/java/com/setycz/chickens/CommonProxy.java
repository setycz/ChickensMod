package com.setycz.chickens;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by setyc on 18.02.2016.
 */
public class CommonProxy {
    public void init() {

    }

    public void registerChicken(ChickensRegistryItem chicken) {
        if (chicken.isDye() && chicken.isEnabled()) {
            GameRegistry.addShapelessRecipe(
                    new ItemStack(ChickensMod.coloredEgg, 1, chicken.getDyeMetadata()),
                    new ItemStack(Items.EGG), new ItemStack(Items.DYE, 1, chicken.getDyeMetadata())
            );
        }
    }

    public void registerLiquidEgg(LiquidEggRegistryItem liquidEgg) {

    }
}
