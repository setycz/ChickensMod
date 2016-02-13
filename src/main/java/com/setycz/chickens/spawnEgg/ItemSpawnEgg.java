package com.setycz.chickens.spawnEgg;

import com.setycz.chickens.ChickensMod;
import com.setycz.chickens.ChickensRegistry;
import com.setycz.chickens.ChickensRegistryItem;
import com.setycz.chickens.chicken.EntityChickensChicken;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by setyc on 12.02.2016.
 */
public class ItemSpawnEgg extends Item {
    public ItemSpawnEgg() {
        setHasSubtypes(true);
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        List<ChickensRegistryItem> chickens = ChickensRegistry.getItems();
        for (int chickenIndex=0; chickenIndex < chickens.size(); chickenIndex++) {
            subItems.add(new ItemStack(itemIn, 1, chickenIndex));
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        ChickensRegistryItem chickenDescription = ChickensRegistry.getByIndex(stack.getMetadata());
        return StatCollector.translateToLocal("entity." + ChickensMod.MODID + "." + chickenDescription.getEntityName() + ".name");
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        ChickensRegistryItem chickenDescription = ChickensRegistry.getByIndex(stack.getMetadata());
        return renderPass == 0 ? chickenDescription.getBgColor() : chickenDescription.getFgColor();
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            BlockPos corelatedPos = correctPosition(pos, side);
            activate(worldIn, corelatedPos, stack.getMetadata());
            if (!playerIn.capabilities.isCreativeMode) {
                stack.stackSize--;
            }
        }
        return true;
    }

    private BlockPos correctPosition(BlockPos pos, EnumFacing side) {
        final int[] offsetsXForSide = new int[] { 0, 0, 0, 0, -1, 1 };
        final int[] offsetsYForSide = new int[] { -1, 1, 0, 0, 0, 0 };
        final int[] offsetsZForSide = new int[] { 0, 0, -1, 1, 0, 0 };

        int posX = pos.getX() + offsetsXForSide[side.ordinal()];
        int posY = pos.getY() + offsetsYForSide[side.ordinal()];
        int posZ = pos.getZ() + offsetsZForSide[side.ordinal()];

        return new BlockPos(posX, posY, posZ);
    }

    private void activate(World worldIn, BlockPos pos, int metadata) {
        String entityName = ChickensMod.MODID + "." + ChickensMod.CHICKEN;
        EntityChickensChicken entity = (EntityChickensChicken)EntityList.createEntityByName(entityName, worldIn);
        if (!worldIn.isRemote) {
            entity.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            entity.onInitialSpawn(worldIn.getDifficultyForLocation(pos), null);
            entity.setChickenType(metadata);
            worldIn.spawnEntityInWorld(entity);
        }
    }
}
