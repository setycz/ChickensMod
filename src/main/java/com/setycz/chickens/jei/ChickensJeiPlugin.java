package com.setycz.chickens.jei;

import com.setycz.chickens.ChickensMod;
import com.setycz.chickens.ChickensRegistry;
import com.setycz.chickens.ChickensRegistryItem;
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
    private IJeiHelpers jeiHelpers;

    @Override
    public void onJeiHelpersAvailable(IJeiHelpers jeiHelpers) {
        this.jeiHelpers = jeiHelpers;
    }

    @Override
    public void onItemRegistryAvailable(IItemRegistry itemRegistry) {

    }

    @Override
    public void register(IModRegistry registry) {
        registry.addRecipeCategories(new LayingRecipeCategory(jeiHelpers.getGuiHelper()));
        registry.addRecipeHandlers(new LayingRecipeHandler());
        registry.addRecipes(getLayingRecipes());
    }

    @Override
    public void onRecipeRegistryAvailable(IRecipeRegistry recipeRegistry) {

    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {

    }

    private List<LayingRecipeWrapper> getLayingRecipes() {
        List<LayingRecipeWrapper> result = new ArrayList<LayingRecipeWrapper>();
        for (ChickensRegistryItem chicken : ChickensRegistry.getItems()) {
            result.add(new LayingRecipeWrapper(
                    new ItemStack(ChickensMod.spawnEgg, 1, chicken.getId()),
                    chicken.createLayItem()
            ));
        }
        return result;
    }
}
