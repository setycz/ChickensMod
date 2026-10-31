package com.timwoodcreates.roost;
import net.minecraft.core.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;
public final class MachineBlock extends BaseEntityBlock {
 public static final EnumProperty<Direction> FACING=HorizontalDirectionalBlock.FACING;
 public static final IntegerProperty BIRD=IntegerProperty.create("bird",0,43);
 public static final BooleanProperty ACTIVE=BooleanProperty.create("active");
 public final int kind;
 public MachineBlock(Properties p,int kind){super(p);this.kind=kind;registerDefaultState(stateDefinition.any().setValue(FACING,Direction.NORTH).setValue(BIRD,0).setValue(ACTIVE,false));}
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b){b.add(FACING,BIRD,ACTIVE);}
 @Override public BlockState getStateForPlacement(BlockPlaceContext c){return defaultBlockState().setValue(FACING,c.getHorizontalDirection().getOpposite());}
 @Override protected BlockState rotate(BlockState s,Rotation r){return s.setValue(FACING,r.rotate(s.getValue(FACING)));}
 @Override protected BlockState mirror(BlockState s,Mirror m){return s.rotate(m.getRotation(s.getValue(FACING)));}
 @Override protected RenderShape getRenderShape(BlockState s){return RenderShape.MODEL;}
 @Override public BlockEntity newBlockEntity(BlockPos p,BlockState s){return new Machine(p,s);}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState s,BlockEntityType<T> t){return level.isClientSide()?null:createTickerHelper(t,RoostMod.ENTITIES.get(kind),(l,p,state,be)->be.tick((ServerLevel)l));}
 @Override protected InteractionResult useItemOn(net.minecraft.world.item.ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){
  if(ChickenItem.bird(stack)!=null && kind!=2){
   if(!level.mayInteract(player,pos))return InteractionResult.FAIL;
   if(!level.isClientSide() && level.getBlockEntity(pos) instanceof Machine machine){
    var offered=stack.copy();int accepted=machine.insertBird(offered);if(accepted==0)return InteractionResult.FAIL;stack.shrink(accepted);
   }
   return InteractionResult.SUCCESS;
  }
  return super.useItemOn(stack,state,level,pos,player,hand,hit);
 }
 @Override protected InteractionResult useWithoutItem(BlockState s,Level l,BlockPos p,Player player,BlockHitResult hit){if(!l.isClientSide()&&l.getBlockEntity(p) instanceof Machine m)player.openMenu(m);return InteractionResult.SUCCESS;}
}
