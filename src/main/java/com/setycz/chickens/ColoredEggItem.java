package com.setycz.chickens;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
public final class ColoredEggItem extends Item implements ProjectileItem {
 public ColoredEggItem(Properties p){super(p);}
 @Override public InteractionResult use(Level level,Player player,InteractionHand hand){
  var stack=player.getItemInHand(hand);
  if(level instanceof ServerLevel server){
   var egg=new ColoredEgg(ChickensMod.COLORED_EGG_ENTITY,level);egg.setItem(stack);egg.setOwner(player);egg.setPos(player.getX(),player.getEyeY()-.1,player.getZ());
   Projectile.spawnProjectile(egg,server,stack,p->p.shootFromRotation(player,player.getXRot(),player.getYRot(),0,1.5f,1));
  }
  level.playSound(null,player.getX(),player.getY(),player.getZ(),SoundEvents.EGG_THROW,SoundSource.PLAYERS,.5f,1);
  stack.consume(1,player);return InteractionResult.SUCCESS;
 }
 @Override public Projectile asProjectile(Level level,Position pos,ItemStack stack,Direction dir){
  var egg=new ColoredEgg(ChickensMod.COLORED_EGG_ENTITY,level);egg.setPos(pos.x(),pos.y(),pos.z());egg.setItem(stack);return egg;
 }
}
