package com.setycz.chickens;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.*;
public final class LiquidEggItem extends BucketItem {
 public LiquidEggItem(Fluid fluid,Properties p){super(fluid,p);}
 @Override public InteractionResult use(Level level,Player p,InteractionHand hand){
  var stack=p.getItemInHand(hand);
  var hit=getPlayerPOVHitResult(level,p,ClipContext.Fluid.NONE);
  if(hit.getType()!=HitResult.Type.BLOCK)return InteractionResult.PASS;
  BlockPos target=hit.getBlockPos().relative(hit.getDirection());
  if(!level.mayInteract(p,hit.getBlockPos()) || !p.mayUseItemAt(target,hit.getDirection(),stack))return InteractionResult.FAIL;
  if(!level.isClientSide()){
   if(!emptyContents(p,level,target,hit))return InteractionResult.FAIL;
   checkExtraContent(p,level,stack,target);stack.consume(1,p);
  }
  return InteractionResult.SUCCESS;
 }
}
