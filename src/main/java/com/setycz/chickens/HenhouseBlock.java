package com.setycz.chickens;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
public final class HenhouseBlock extends BaseEntityBlock {
 public static final net.minecraft.world.level.block.state.properties.EnumProperty<net.minecraft.core.Direction> FACING=HorizontalDirectionalBlock.FACING;
 public HenhouseBlock(Properties p){super(p);registerDefaultState(stateDefinition.any().setValue(FACING,net.minecraft.core.Direction.NORTH));}
 @Override protected void createBlockStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<Block,BlockState> b){b.add(FACING);}
 @Override public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext ctx){return defaultBlockState().setValue(FACING,ctx.getHorizontalDirection().getOpposite());}
 @Override protected BlockState rotate(BlockState state,Rotation rotation){return state.setValue(FACING,rotation.rotate(state.getValue(FACING)));}
 @Override protected BlockState mirror(BlockState state,Mirror mirror){return state.rotate(mirror.getRotation(state.getValue(FACING)));}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new HenhouseBlockEntity(pos,state);}
 @Override protected RenderShape getRenderShape(BlockState s){return RenderShape.MODEL;}
 @Override protected InteractionResult useWithoutItem(BlockState s,Level level,BlockPos pos,Player p,BlockHitResult hit){
  if(!level.isClientSide() && level.getBlockEntity(pos) instanceof HenhouseBlockEntity h) p.openMenu(h);
  return InteractionResult.SUCCESS;
 }
}
