package com.timwoodcreates.roost;

import java.util.*;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.*;

public final class Machine extends BaseContainerBlockEntity implements WorldlyContainer {
    private NonNullList<ItemStack> items;
    private int elapsed,duration,status,search;
    private ItemStack pending=ItemStack.EMPTY;
    private ItemStack left=ItemStack.EMPTY,right=ItemStack.EMPTY;
    public Machine(BlockPos p,BlockState s) {
        super(RoostMod.ENTITIES.get(((MachineBlock)s.getBlock()).kind),p,s);
        items=NonNullList.withSize(size(),ItemStack.EMPTY);
    }
    public int kind(){return ((MachineBlock)getBlockState().getBlock()).kind;}
    private int size(){return kind()==0?5:kind()==1?6:27;}
    public int firstOutput(){return kind()==0?1:kind()==1?3:0;}
    public int progress(){return duration==0?0:(int)Math.clamp((long)elapsed*1000/duration,0,1000);}
    public int status(){return status;}
    @Override public int getContainerSize(){return items.size();}
    @Override protected NonNullList<ItemStack> getItems(){return items;}
    @Override protected void setItems(NonNullList<ItemStack> i){items=i;}
    @Override protected Component getDefaultName(){return Component.translatable("block.roost."+RoostMod.NAMES.get(kind()));}
    @Override protected AbstractContainerMenu createMenu(int id,Inventory inv){return new MachineMenu(id,inv,this);}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){
        if(kind()==2)return true;
        if(slot<firstOutput() && (kind()==0 || slot<2))return ChickenItem.bird(stack)!=null;
        return kind()==1 && slot==2 && stack.is(net.minecraft.tags.ItemTags.CHICKEN_FOOD);
    }
    @Override public int[] getSlotsForFace(Direction side){
        if(kind()==2)return java.util.stream.IntStream.range(0,size()).toArray();
        return side==Direction.UP?java.util.stream.IntStream.range(0,firstOutput()).toArray():java.util.stream.IntStream.range(firstOutput(),size()).toArray();
    }
    @Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return (kind()==2 || side==Direction.UP) && canPlaceItem(slot,stack);}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return kind()==2 || side!=Direction.UP && slot>=firstOutput();}
    public int insertBird(ItemStack stack){
        if(kind()==2 || ChickenItem.bird(stack)==null)return 0;
        int count=stack.getCount(),end=kind()==0?1:2;
        for(int i=0;i<end && !stack.isEmpty();i++){
            var at=items.get(i);if(!at.isEmpty()&&!ItemStack.isSameItemSameComponents(at,stack))continue;
            int n=Math.min(stack.getCount(),16-(at.isEmpty()?0:at.getCount()));
            if(n<=0)continue;
            if(at.isEmpty())items.set(i,stack.split(n));else{at.grow(n);stack.shrink(n);}
        }
        if(count!=stack.getCount())setChanged();return count-stack.getCount();
    }
    /** Simulates insertion first. Failed insertion never partially changes inventory. */
    public boolean offer(ItemStack stack,boolean commit){
        int remaining=stack.getCount();
        for(int i=firstOutput();i<size() && remaining>0;i++){
            var at=items.get(i);if(at.isEmpty() || ItemStack.isSameItemSameComponents(at,stack))remaining-=Math.min(remaining,stack.getMaxStackSize()-(at.isEmpty()?0:at.getCount()));
        }
        if(remaining>0)return false;if(!commit)return true;
        ItemStack rest=stack.copy();
        for(int i=firstOutput();i<size() && !rest.isEmpty();i++){
            var at=items.get(i);if(!at.isEmpty()&&!ItemStack.isSameItemSameComponents(at,rest))continue;
            int n=Math.min(rest.getCount(),rest.getMaxStackSize()-(at.isEmpty()?0:at.getCount()));
            if(n<=0)continue;if(at.isEmpty())items.set(i,rest.split(n));else{at.grow(n);rest.shrink(n);}
        }
        setChanged();return true;
    }
    private boolean same(ItemStack a,ItemStack b){return a.isEmpty()&&b.isEmpty() || ItemStack.isSameItemSameComponents(a,b);}
    public void tick(ServerLevel server){
        if(kind()==2){collect(server);return;}
        var a=items.get(0);var b=kind()==1?items.get(1):ItemStack.EMPTY;
        if(!same(left,a)||!same(right,b)){elapsed=0;duration=0;pending=ItemStack.EMPTY;left=a.copy();right=b.copy();setChanged();}
        var ca=ChickenItem.bird(a);var cb=ChickenItem.bird(b);
        if(ca==null || kind()==1&&cb==null){status=0;visual(false);return;}
        if((ca.breed!=null&&!ca.breed.enabled()) || kind()==1&&cb.breed!=null&&!cb.breed.enabled()){status=4;visual(false);return;}
        if(kind()==1 && (ca.breed==null)!=(cb.breed==null)){status=4;visual(false);return;}
        if(kind()==1 && (!items.get(2).is(net.minecraft.tags.ItemTags.CHICKEN_FOOD)||items.get(2).getCount()<2)){status=3;visual(false);return;}
        if(duration<=0)duration=Math.max(ca.layTime(a,server.getRandom()),kind()==1?cb.layTime(b,server.getRandom()):1);
        if(pending.isEmpty()){
            int speed=ChickenItem.stat(a,"Growth")*a.getCount();
            if(kind()==1)speed=Math.min(speed,ChickenItem.stat(b,"Growth")*b.getCount());
            elapsed=Math.min(duration,elapsed+Math.max(1,(int)Math.round(speed*(kind()==0?RoostConfig.roostSpeed:RoostConfig.breederSpeed))));
            if(elapsed>=duration)pending=kind()==0?ca.production(a,server.getRandom()):ChickenItem.child(a,b,server);
            setChanged();
        }
        if(!pending.isEmpty()){
            if(!offer(pending,true)){status=2;visual(false);return;}
            if(kind()==1)items.get(2).shrink(2);
            pending=ItemStack.EMPTY;elapsed=0;duration=0;setChanged();
        }
        status=1;visual(true);
    }
    private void visual(boolean active){
        if(level==null)return;
        int bird=0;if(kind()==0 && ChickenItem.bird(items.get(0))!=null)bird=RoostMod.ORDER.indexOf(ChickenItem.bird(items.get(0)))+1;
        var state=getBlockState();var next=state.setValue(MachineBlock.BIRD,Math.max(0,bird)).setValue(MachineBlock.ACTIVE,active);
        if(!state.equals(next))level.setBlock(worldPosition,next,3);
    }
    private void collect(ServerLevel server){
        search=(search+1)%27;int y=search/9,z=search%9-4;
        for(int x=-4;x<=4;x++){
            var pos=worldPosition.offset(x,y,z);if(!server.hasChunkAt(pos))continue;
            if(!(server.getBlockEntity(pos) instanceof Machine source)||source.kind()!=0)continue;
            for(int slot=source.firstOutput();slot<source.size();slot++){
                var stack=source.getItem(slot);if(stack.isEmpty())continue;
                var one=stack.copyWithCount(1);if(offer(one,true)){stack.shrink(1);source.setChanged();break;}
            }
        }
    }
    @Override protected void saveAdditional(ValueOutput out){super.saveAdditional(out);ContainerHelper.saveAllItems(out,items);out.putInt("Elapsed",elapsed);out.putInt("Duration",duration);out.store("Pending",ItemStack.OPTIONAL_CODEC,pending);out.store("Left",ItemStack.OPTIONAL_CODEC,left);out.store("Right",ItemStack.OPTIONAL_CODEC,right);}
    @Override protected void loadAdditional(ValueInput in){super.loadAdditional(in);items=NonNullList.withSize(size(),ItemStack.EMPTY);ContainerHelper.loadAllItems(in,items);elapsed=Math.clamp(in.getIntOr("Elapsed",0),0,200000000);duration=Math.clamp(in.getIntOr("Duration",0),0,200000000);pending=in.read("Pending",ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);left=in.read("Left",ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);right=in.read("Right",ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);}
}
