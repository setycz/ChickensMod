package com.timwoodcreates.roost;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
public final class MachineMenu extends AbstractContainerMenu {
 public final int kind;
 private final Container machine;
 private final ContainerData data;
 public MachineMenu(int id,Inventory inv,int kind){this(id,inv,kind,new SimpleContainer(kind==0?5:kind==1?6:27),new SimpleContainerData(2));}
 public MachineMenu(int id,Inventory inv,Machine m){this(id,inv,m.kind(),m,new ContainerData(){public int get(int i){return i==0?m.progress():m.status();}public void set(int i,int v){}public int getCount(){return 2;}});}
 private MachineMenu(int id,Inventory inv,int kind,Container machine,ContainerData data){
  super(RoostMod.MENUS.get(kind),id);this.kind=kind;this.machine=machine;this.data=data;machine.startOpen(inv.player);
  if(kind==2){for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(machine,row*9+col,8+col*18,18+row*18));addStandardInventorySlots(inv,8,84);}
  else{
   int first=kind==0?1:3;
   for(int i=0;i<first;i++){
    final int slot=i;int x=kind==0?26:i==2?8:44+i*18;
    addSlot(new Slot(machine,i,x,20){@Override public boolean mayPlace(ItemStack stack){return slot< (kind==0?1:2)?ChickenItem.bird(stack)!=null:stack.is(net.minecraft.tags.ItemTags.CHICKEN_FOOD);}@Override public int getMaxStackSize(){return slot< (kind==0?1:2)?16:64;}});
   }
   int outputs=kind==0?4:3;
   for(int i=0;i<outputs;i++)addSlot(new Slot(machine,first+i,(kind==0?80:116)+i*18,20){@Override public boolean mayPlace(ItemStack s){return false;}});
   addStandardInventorySlots(inv,8,51);
  }
  addDataSlots(data);
 }
 public int progress(){return Math.clamp(data.get(0),0,1000);}
 public int status(){return data.get(1);}
 @Override public boolean stillValid(Player p){return machine.stillValid(p);}
 @Override public void removed(Player p){super.removed(p);machine.stopOpen(p);}
 @Override public ItemStack quickMoveStack(Player p,int index){
  int size=machine.getContainerSize();if(index<0||index>=slots.size())return ItemStack.EMPTY;
  var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var old=stack.copy();boolean moved;
  if(index<size)moved=moveItemStackTo(stack,size,slots.size(),true);
  else if(kind==2)moved=moveItemStackTo(stack,0,size,false);
  else if(ChickenItem.bird(stack)!=null)moved=moveItemStackTo(stack,0,kind==0?1:2,false);
  else if(kind==1 && stack.is(net.minecraft.tags.ItemTags.CHICKEN_FOOD))moved=moveItemStackTo(stack,2,3,false);
  else if(index<size+27)moved=moveItemStackTo(stack,size+27,slots.size(),false);
  else moved=moveItemStackTo(stack,size,size+27,false);
  if(!moved||old.getCount()==stack.getCount())return ItemStack.EMPTY;
  if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(p,stack);return old;
 }
}
