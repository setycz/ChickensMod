package com.timwoodcreates.roost;
import com.setycz.chickens.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.storage.*;
import net.minecraft.util.ProblemReporter;
public final class RoostRegression implements ModInitializer {
 static int checks;
 static void check(boolean good,String message){if(!good)throw new AssertionError(message);checks++;System.out.println("ROOST PASS: "+message);}
 @Override public void onInitialize(){ServerLifecycleEvents.SERVER_STARTED.register(server->{try{run(server.overworld());System.out.println("ROOST REGRESSION OK: "+checks+" checks");}catch(Throwable e){e.printStackTrace();System.out.println("ROOST REGRESSION FAILED");}finally{server.halt(false);}});}
 static Machine machine(ServerLevel level,int kind,int x){var p=new BlockPos(x,100,0);level.getChunk(p);level.setBlock(p,RoostMod.BLOCKS.get(kind).defaultBlockState(),3);return (Machine)level.getBlockEntity(p);}
 static void due(Machine m,ServerLevel l){
  var tag=m.saveWithoutMetadata(l.registryAccess());tag.putInt("Duration",1);tag.putInt("Elapsed",1);
  tag.put("Left",tagItem(m.getItem(0),l));if(m.kind()==1)tag.put("Right",tagItem(m.getItem(1),l));
  m.loadCustomOnly(TagValueInput.create(ProblemReporter.DISCARDING,l.registryAccess(),tag));
 }
 static net.minecraft.nbt.CompoundTag tagItem(ItemStack stack,ServerLevel level){return (net.minecraft.nbt.CompoundTag)ItemStack.OPTIONAL_CODEC.encodeStart(level.registryAccess().createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE),stack).getOrThrow();}
 static int count(Machine m,Item item){int c=0;for(int i=m.firstOutput();i<m.getContainerSize();i++)if(m.getItem(i).is(item))c+=m.getItem(i).getCount();return c;}
 static void run(ServerLevel level){
  check(RoostMod.BIRDS.size()==43,"vanilla and all 42 resource breeds registered");
  check(RoostMod.BLOCKS.size()==3 && RoostMod.MENUS.size()==3,"Roost, Breeder and Collector registered");
  var chicken=Breeds.get("iron").entityType.create(level,EntitySpawnReason.COMMAND);chicken.stats(9,10,8);chicken.setCustomName(Component.literal("Iron Annie"));chicken.setHealth(2);chicken.setNoAi(true);chicken.setPos(0,101,0);level.addFreshEntity(chicken);
  var captured=ChickenItem.capture(chicken,level);var roundtrip=ChickenItem.bird(captured).entity(captured,level);
  check(roundtrip instanceof ResourceChicken && ((ResourceChicken)roundtrip).breed()==Breeds.get("iron"),"capture/release retains breed");
  check(((ResourceChicken)roundtrip).growth()==9 && ((ResourceChicken)roundtrip).gain()==10 && ((ResourceChicken)roundtrip).strength()==8,"capture/release retains all stats");
  check(roundtrip.getName().getString().equals("Iron Annie") && roundtrip.getHealth()==2 && roundtrip.isNoAi(),"capture/release retains name, health and AI state");
  check(!roundtrip.getUUID().equals(chicken.getUUID()),"released copies never reuse an entity UUID");
  var player=new Player(level,new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(),"RoostTest")){public net.minecraft.world.level.GameType gameMode(){return net.minecraft.world.level.GameType.SURVIVAL;}};
  check(RoostMod.capture(player,chicken,level) && chicken.isRemoved(),"catcher transfers one adult and removes original");
  int n=0;for(int i=0;i<36;i++)if(ChickenItem.bird(player.getInventory().getItem(i))!=null)n+=player.getInventory().getItem(i).getCount();check(n==1,"catcher creates exactly one captured chicken");
  check(!RoostMod.capture(player,chicken,level),"catching an already removed chicken cannot duplicate it");
  var baby=EntityTypes.CHICKEN.create(level,EntitySpawnReason.COMMAND);baby.setBaby(true);check(!RoostMod.capture(player,baby,level),"catcher rejects babies");
  var full=EntityTypes.CHICKEN.create(level,EntitySpawnReason.COMMAND);full.setPos(1,101,0);level.addFreshEntity(full);
  for(int i=0;i<36;i++)player.getInventory().setItem(i,new ItemStack(Items.DIAMOND,64));
  check(RoostMod.capture(player,full,level) && full.isRemoved(),"full player inventory safely drops captured chicken");
  var roost=machine(level,0,0);roost.setItem(0,captured.copy());due(roost,level);roost.tick(level);
  check(count(roost,Items.IRON_INGOT)==3,"Roost produces Gain-adjusted resource without hay or seeds");
  check(roost.getBlockState().getValue(MachineBlock.BIRD)>0,"occupied Roost selects matching visible chicken model");
  for(int slot=1;slot<=4;slot++)roost.setItem(slot,new ItemStack(Items.IRON_INGOT,64));roost.setItem(1,new ItemStack(Items.IRON_INGOT,63));due(roost,level);roost.tick(level);
  check(count(roost,Items.IRON_INGOT)==255 && roost.status()==2,"partial space pauses whole Gain output without loss");
  var saved=roost.saveWithoutMetadata(level.registryAccess());var restored=new Machine(roost.getBlockPos(),roost.getBlockState());restored.loadCustomOnly(TagValueInput.create(ProblemReporter.DISCARDING,level.registryAccess(),saved));restored.setItem(1,new ItemStack(Items.IRON_INGOT,61));restored.tick(level);
  check(count(restored,Items.IRON_INGOT)==256,"pending resource output survives save/reload and commits exactly once");restored.tick(level);check(count(restored,Items.IRON_INGOT)==256,"completed pending output is not duplicated next tick");
  var breeder=machine(level,1,10);var iron=new ItemStack(RoostMod.BIRDS.get("iron"));breeder.setItem(0,iron.copy());breeder.setItem(1,iron.copy());due(breeder,level);breeder.tick(level);
  check(breeder.status()==3 && breeder.getItem(3).isEmpty(),"breeder needs two seeds");
  breeder.setItem(2,new ItemStack(Items.WHEAT_SEEDS,3));due(breeder,level);breeder.tick(level);var child=breeder.getItem(3);
  check(ChickenItem.bird(child)==RoostMod.BIRDS.get("iron") && ChickenItem.stat(child,"Growth")>=2 && ChickenItem.stat(child,"Gain")>=2,"same-breed automation improves stats");
  check(breeder.getItem(2).getCount()==1 && breeder.getItem(0).getCount()==1 && breeder.getItem(1).getCount()==1,"breeding consumes exactly two seeds and keeps both parents");
  breeder.setItem(2,new ItemStack(Items.WHEAT_SEEDS,64));for(int i=3;i<6;i++)breeder.setItem(i,new ItemStack(Items.DIAMOND,64));due(breeder,level);breeder.tick(level);
  check(breeder.status()==2 && breeder.getItem(2).getCount()==64,"full breeder output consumes no seeds");
  var before=breeder.saveWithoutMetadata(level.registryAccess());breeder.tick(level);var after=breeder.saveWithoutMetadata(level.registryAccess());
  check(before.getCompoundOrEmpty("Pending").equals(after.getCompoundOrEmpty("Pending")),"blocked breeding preserves its chosen child instead of rerolling");
  breeder.setItem(3,ItemStack.EMPTY);breeder.tick(level);check(breeder.getItem(2).getCount()==62 && ChickenItem.bird(breeder.getItem(3))!=null,"unblocking commits the waiting child once");
  breeder.setItem(0,new ItemStack(RoostMod.BIRDS.get("vanilla")));breeder.tick(level);check(breeder.status()==4,"vanilla/resource crossbreeding is rejected without seed loss");
  var random=net.minecraft.util.RandomSource.create(3);boolean pink=false;
  for(int i=0;i<100;i++){var c=ChickenItem.child(new ItemStack(RoostMod.BIRDS.get("red")),new ItemStack(RoostMod.BIRDS.get("white")),level);if(ChickenItem.bird(c)==RoostMod.BIRDS.get("pink"))pink=true;}
  check(pink,"breeder uses Chickens crossbreeding tree");
  var collector=machine(level,2,-3);for(int i=1;i<5;i++)roost.setItem(i,ItemStack.EMPTY);roost.setItem(1,new ItemStack(Items.IRON_INGOT,4));
  for(int i=0;i<120;i++)collector.tick(level);check(count(collector,Items.IRON_INGOT)==4 && roost.getItem(1).isEmpty(),"collector moves nearby Roost output with no duplication");
  check(!roost.getItem(0).isEmpty(),"collector never takes the housed chicken");
  check(roost.canPlaceItemThroughFace(0,iron,Direction.UP) && !roost.canTakeItemThroughFace(0,iron,Direction.DOWN),"hopper rules protect housed chickens");
  player.getInventory().clearContent();var menu=new MachineMenu(1,player.getInventory(),roost);var client=RoostMod.MENUS.get(0).create(2,player.getInventory());
  check(menu.slots.size()==41 && client.kind==0,"registered Roost menu has five machine slots and 36 player slots");
  menu.addSlotListener(new ContainerListener(){public void slotChanged(AbstractContainerMenu m,int i,ItemStack s){}public void dataChanged(AbstractContainerMenu m,int i,int v){client.setData(i,v);}});menu.broadcastChanges();
  check(client.status()==roost.status() && client.progress()==roost.progress(),"status and production progress synchronize to client");
  check(!menu.getSlot(1).mayPlace(new ItemStack(Items.IRON_INGOT)),"GUI output cannot be used as unrestricted input");
  var stack=new ItemStack(RoostMod.BIRDS.get("iron"),16);var remaining=stack.copy();var empty=machine(level,0,20);check(empty.insertBird(remaining)==16&&remaining.isEmpty(),"Roost accepts sixteen identical captured chickens");
  check(empty.insertBird(stack.copy())==0 && empty.getItem(0).getCount()==16,"full Roost cannot overfill or destroy offered chickens");
  var direct=machine(level,0,30);var offered=new ItemStack(RoostMod.BIRDS.get("iron"),2);
  var hit=new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(direct.getBlockPos()),Direction.NORTH,direct.getBlockPos(),false);
  var result=RoostMod.BLOCKS.get(0).useItemOn(offered,direct.getBlockState(),level,direct.getBlockPos(),player,InteractionHand.MAIN_HAND,hit);
  check(result==InteractionResult.SUCCESS && offered.isEmpty() && direct.getItem(0).getCount()==2,"right-clicking Roost with a captured chicken inserts it before GUI interaction");
  var ticker=RoostMod.BLOCKS.get(0).getTicker(level,direct.getBlockState(),RoostMod.ENTITIES.get(0));due(direct,level);ticker.tick(level,direct.getBlockPos(),direct.getBlockState(),direct);
  check(count(direct,Items.IRON_INGOT)==1,"registered block ticker runs real production");
  player.getInventory().clearContent();player.setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(RoostMod.CATCHER));
  var catchTarget=EntityTypes.CHICKEN.create(level,EntitySpawnReason.COMMAND);catchTarget.setPos(35,101,0);level.addFreshEntity(catchTarget);
  var caught=net.fabricmc.fabric.api.event.player.UseEntityCallback.EVENT.invoker().interact(player,level,InteractionHand.MAIN_HAND,catchTarget,null);
  check(caught==InteractionResult.SUCCESS && catchTarget.isRemoved() && player.getMainHandItem().getDamageValue()==1,"actual catcher interaction consumes exactly one durability");
  var releaseItem=new ItemStack(RoostMod.BIRDS.get("iron"));player.setItemSlot(EquipmentSlot.MAINHAND,releaseItem);
  var ground=new BlockPos(40,100,0);level.setBlock(ground,net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(),3);
  var releaseHit=new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(ground),Direction.UP,ground,false);
  var released=releaseItem.getItem().useOn(new net.minecraft.world.item.context.UseOnContext(player,InteractionHand.MAIN_HAND,releaseHit));
  check(released==InteractionResult.SUCCESS && releaseItem.isEmpty() && !level.getEntitiesOfClass(ResourceChicken.class,new net.minecraft.world.phys.AABB(ground).inflate(2)).isEmpty(),"using captured chicken on the ground releases one live chicken");
  var save=empty.saveWithoutMetadata(level.registryAccess());var reload=new Machine(empty.getBlockPos(),empty.getBlockState());reload.loadCustomOnly(TagValueInput.create(ProblemReporter.DISCARDING,level.registryAccess(),save));check(reload.getItem(0).getCount()==16,"housed chicken stack survives block save/reload");
 }
}
