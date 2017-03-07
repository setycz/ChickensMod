package com.setycz.chickens.spawnEgg;

import com.setycz.chickens.ChickensMod;
import com.setycz.chickens.ChickensRegistry;
import com.setycz.chickens.ChickensRegistryItem;
import com.setycz.chickens.IColorSource;
import com.setycz.chickens.chicken.EntityChickensChicken;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

/**
 * Created by setyc on 12.02.2016.
 */
public class ItemSpawnEgg extends Item implements IColorSource {
    public ItemSpawnEgg() {
        setHasSubtypes(true);
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
        for (ChickensRegistryItem chicken : ChickensRegistry.getItems()) {
            subItems.add(new ItemStack(itemIn, 1, chicken.getId()));
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        ChickensRegistryItem chickenDescription = ChickensRegistry.getByType(stack.getMetadata());
        return I18n.translateToLocal("entity." + chickenDescription.getEntityName() + ".name");
    }


    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        ChickensRegistryItem chickenDescription = ChickensRegistry.getByType(stack.getMetadata());
        return renderPass == 0 ? chickenDescription.getBgColor() : chickenDescription.getFgColor();
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            ItemStack stack = playerIn.getHeldItem(hand);
            BlockPos correlatedPos = correctPosition(pos, facing);
            activate(stack, worldIn, correlatedPos, stack.getMetadata());
            if (!playerIn.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
        }
        return EnumActionResult.SUCCESS;
    }

    private BlockPos correctPosition(BlockPos pos, EnumFacing side) {
        final int[] offsetsXForSide = new int[]{0, 0, 0, 0, -1, 1};
        final int[] offsetsYForSide = new int[]{-1, 1, 0, 0, 0, 0};
        final int[] offsetsZForSide = new int[]{0, 0, -1, 1, 0, 0};

        int posX = pos.getX() + offsetsXForSide[side.ordinal()];
        int posY = pos.getY() + offsetsYForSide[side.ordinal()];
        int posZ = pos.getZ() + offsetsZForSide[side.ordinal()];

        return new BlockPos(posX, posY, posZ);
    }

    private void activate(ItemStack stack, World worldIn, BlockPos pos, int metadata) {
        ResourceLocation entityName = new ResourceLocation(ChickensMod.MODID, ChickensMod.CHICKEN);
        EntityChickensChicken entity = (EntityChickensChicken) EntityList.createEntityByIDFromName(entityName, worldIn);
        if (entity == null) {
            return;
        }
        entity.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        entity.onInitialSpawn(worldIn.getDifficultyForLocation(pos), null);
        entity.setChickenType(metadata);

        NBTTagCompound stackNBT = stack.getTagCompound();
        if (stackNBT != null) {
            NBTTagCompound entityNBT = entity.writeToNBT(new NBTTagCompound());
            entityNBT.merge(stackNBT);
            entity.readEntityFromNBT(entityNBT);
        }

        worldIn.spawnEntity(entity);
    }
}
