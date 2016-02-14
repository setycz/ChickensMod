package com.setycz.chickens.liquidEgg;

import com.setycz.chickens.LiquidEggRegistry;
import com.setycz.chickens.LiquidEggRegistryItem;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by setyc on 14.02.2016.
 */
public class ItemLiquidEgg extends ItemEgg {
    public ItemLiquidEgg() {
        setHasSubtypes(true);
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for (LiquidEggRegistryItem liquid : LiquidEggRegistry.getAll()) {
            subItems.add(new ItemStack(itemIn, 1, liquid.getId()));
        }
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        return LiquidEggRegistry.findById(stack.getMetadata()).getEggColor();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        Block liquid = LiquidEggRegistry.findById(stack.getMetadata()).getLiquid();
        return StatCollector.translateToLocal(getUnlocalizedName() + "." + liquid.getUnlocalizedName().substring(5) + ".name");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(worldIn, playerIn, false);

        if (movingobjectposition == null) {
            return itemStackIn;
        } else {
            ItemStack ret = net.minecraftforge.event.ForgeEventFactory.onBucketUse(playerIn, worldIn, itemStackIn, movingobjectposition);
            if (ret != null) return ret;

            if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                BlockPos blockpos = movingobjectposition.getBlockPos();

                if (!worldIn.isBlockModifiable(playerIn, blockpos)) {
                    return itemStackIn;
                }

                BlockPos blockpos1 = blockpos.offset(movingobjectposition.sideHit);

                if (!playerIn.canPlayerEdit(blockpos1, movingobjectposition.sideHit, itemStackIn)) {
                    return itemStackIn;
                }

                Block liquid = LiquidEggRegistry.findById(itemStackIn.getMetadata()).getLiquid();
                if (this.tryPlaceContainedLiquid(worldIn, blockpos1, liquid) && !playerIn.capabilities.isCreativeMode) {
                    playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
                    return new ItemStack(itemStackIn.getItem(), itemStackIn.stackSize - 1, itemStackIn.getMetadata());
                }
            }

            return itemStackIn;
        }
    }

    public boolean tryPlaceContainedLiquid(World worldIn, BlockPos pos, Block liquid) {
        Material material = worldIn.getBlockState(pos).getBlock().getMaterial();
        boolean flag = !material.isSolid();

        if (!worldIn.isAirBlock(pos) && !flag) {
            return false;
        } else {
            if (worldIn.provider.doesWaterVaporize() && liquid == Blocks.flowing_water) {
                int i = pos.getX();
                int j = pos.getY();
                int k = pos.getZ();
                worldIn.playSoundEffect((double) ((float) i + 0.5F), (double) ((float) j + 0.5F), (double) ((float) k + 0.5F), "random.fizz", 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

                for (int l = 0; l < 8; ++l) {
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, (double) i + Math.random(), (double) j + Math.random(), (double) k + Math.random(), 0.0D, 0.0D, 0.0D, new int[0]);
                }
            } else {
                if (!worldIn.isRemote && flag && !material.isLiquid()) {
                    worldIn.destroyBlock(pos, true);
                }

                worldIn.setBlockState(pos, liquid.getDefaultState(), 3);
            }

            return true;
        }
    }
}
