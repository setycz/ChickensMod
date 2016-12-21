package com.setycz.chickens.liquidEgg;

import com.setycz.chickens.IColorSource;
import com.setycz.chickens.LiquidEggRegistry;
import com.setycz.chickens.LiquidEggRegistryItem;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by setyc on 14.02.2016.
 */
public class ItemLiquidEgg extends ItemEgg implements IColorSource {
    public ItemLiquidEgg() {
        setHasSubtypes(true);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        tooltip.add(I18n.translateToLocal("item.liquid_egg.tooltip"));
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
        return I18n.translateToLocal(getUnlocalizedName() + "." + liquid.getUnlocalizedName().substring(5) + ".name");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        RayTraceResult raytraceresult = this.rayTrace(worldIn, playerIn, false);

        //noinspection ConstantConditions
        if (raytraceresult == null) {
            return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
        } else if (raytraceresult.typeOfHit != RayTraceResult.Type.BLOCK) {
            return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
        } else {
            BlockPos blockpos = raytraceresult.getBlockPos();
            if (!worldIn.isBlockModifiable(playerIn, blockpos)) {
                return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStackIn);
            } else {
                boolean flag1 = worldIn.getBlockState(blockpos).getBlock().isReplaceable(worldIn, blockpos);
                BlockPos blockPos1 = flag1 && raytraceresult.sideHit == EnumFacing.UP ? blockpos : blockpos.offset(raytraceresult.sideHit);

                Block liquid = LiquidEggRegistry.findById(itemStackIn.getMetadata()).getLiquid();
                if (!playerIn.canPlayerEdit(blockPos1, raytraceresult.sideHit, itemStackIn)) {
                    return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStackIn);
                } else if (this.tryPlaceContainedLiquid(playerIn, worldIn, blockPos1, liquid)) {
                    //noinspection ConstantConditions
                    playerIn.addStat(StatList.getObjectUseStats(this));
                    return !playerIn.capabilities.isCreativeMode ? new ActionResult<ItemStack>(EnumActionResult.SUCCESS, new ItemStack(itemStackIn.getItem(), itemStackIn.stackSize - 1, itemStackIn.getMetadata())) : new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
                } else {
                    return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStackIn);
                }
            }
        }
    }

    public boolean tryPlaceContainedLiquid(@Nullable EntityPlayer playerIn, World worldIn, BlockPos pos, Block liquid) {
        Material material = worldIn.getBlockState(pos).getMaterial();
        boolean flag = !material.isSolid();

        if (!worldIn.isAirBlock(pos) && !flag) {
            return false;
        } else {
            if (worldIn.provider.doesWaterVaporize() && liquid == Blocks.FLOWING_WATER) {
                int i = pos.getX();
                int j = pos.getY();
                int k = pos.getZ();
                worldIn.playSound(playerIn, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

                for (int l = 0; l < 8; ++l) {
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, (double) i + Math.random(), (double) j + Math.random(), (double) k + Math.random(), 0.0D, 0.0D, 0.0D);
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

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
        return new LiquidEggFluidWrapper(stack);
    }
}
