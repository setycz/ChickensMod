package com.setycz.chickens;

import com.setycz.chickens.coloredEgg.EntityColoredEgg;
import com.setycz.chickens.coloredEgg.ItemColoredEgg;
import com.setycz.chickens.liquidEgg.ItemLiquidEgg;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ChickensMod.liquidEgg, new DispenseLiquidEgg());
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ChickensMod.coloredEgg, new DispenseColorEgg());
    }


    class DispenseColorEgg extends BehaviorProjectileDispense {
        @Override
        protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
            EntityColoredEgg entityColoredEgg = new EntityColoredEgg(worldIn, position.getX(), position.getY(), position.getZ());
            entityColoredEgg.setChickenType(((ItemColoredEgg) stackIn.getItem()).getChickenType(stackIn));
            return entityColoredEgg;
        }
    }

    class DispenseLiquidEgg extends BehaviorDefaultDispenseItem {
        @Override
        protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            ItemLiquidEgg itemLiquidEgg = (ItemLiquidEgg) stack.getItem();
            BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().getValue(BlockDispenser.FACING));
            Block liquid = LiquidEggRegistry.findById(stack.getMetadata()).getLiquid();
            if (!itemLiquidEgg.tryPlaceContainedLiquid(null, source.getWorld(), blockpos, liquid)) {
                return super.dispenseStack(source, stack);
            }
            stack.stackSize--;
            return stack;
        }
    }
}
