package com.setycz.chickens.henhouse;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by setyc on 01.03.2016.
 */
public class BlockHenhouse extends Block implements ITileEntityProvider{
    public BlockHenhouse() {
        super(Material.wood);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityHenhouse();
    }
}
