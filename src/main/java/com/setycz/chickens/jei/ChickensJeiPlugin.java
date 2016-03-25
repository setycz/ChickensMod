package com.setycz.chickens.jei;

import com.setycz.chickens.ChickensMod;
import com.setycz.chickens.ChickensRegistry;
import com.setycz.chickens.ChickensRegistryItem;
import com.setycz.chickens.jei.breeding.BreedingRecipeCategory;
import com.setycz.chickens.jei.breeding.BreedingRecipeHandler;
import com.setycz.chickens.jei.breeding.BreedingRecipeWrapper;
import com.setycz.chickens.jei.laying.LayingRecipeCategory;
import com.setycz.chickens.jei.laying.LayingRecipeHandler;
import com.setycz.chickens.jei.laying.LayingRecipeWrapper;
import mezz.jei.api.*;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by setyc on 21.02.2016.
 */
@JEIPlugin
public class ChickensJeiPlugin implements IModPlugin {

    @Override
    public void register(IModRegistry registry) {
        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        registry.addRecipeCategories(
                new LayingRecipeCategory(jeiHelpers.getGuiHelper()),
                new BreedingRecipeCategory(jeiHelpers.getGuiHelper())
        );
        registry.addRecipeHandlers(
                new LayingRecipeHandler(),
                new BreedingRecipeHandler()
        );
        registry.addRecipes(getLayingRecipes());
        registry.addRecipes(getBreedingRecipes());
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {

    }

    private List<LayingRecipeWrapper> getLayingRecipes() {
        List<LayingRecipeWrapper> result = new ArrayList<LayingRecipeWrapper>();
        for (ChickensRegistryItem chicken : ChickensRegistry.getItems()) {
            result.add(new LayingRecipeWrapper(
                    new ItemStack(ChickensMod.spawnEgg, 1, chicken.getId()),
                    chicken.createLayItem(),
                    chicken.getMinLayTime(), chicken.getMaxLayTime()
            ));
        }
        return result;
    }

    private List<BreedingRecipeWrapper> getBreedingRecipes() {
        List<BreedingRecipeWrapper> result = new ArrayList<BreedingRecipeWrapper>();
        for (ChickensRegistryItem chicken : ChickensRegistry.getItems()) {
            if (chicken.getTier() > 1) {
                result.add(new BreedingRecipeWrapper(
                        new ItemStack(ChickensMod.spawnEgg, 1, chicken.getParent1().getId()),
                        new ItemStack(ChickensMod.spawnEgg, 1, chicken.getParent2().getId()),
                        new ItemStack(ChickensMod.spawnEgg, 1, chicken.getId()),
                        ChickensRegistry.getChildChance(chicken)
                ));
            }
        }
        return result;
    }
}
