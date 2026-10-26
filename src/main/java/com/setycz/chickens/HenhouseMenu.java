package com.setycz.chickens;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
/** Uses the vanilla chest screen, with server-enforced input/output slots. */
public final class HenhouseMenu extends ChestMenu {
 public HenhouseMenu(int id,Inventory inventory,HenhouseBlockEntity house){
  super(MenuType.GENERIC_9x3,id,inventory,house,3);
  for(int i=0;i<27;i++){
   final int slot=i;Slot old=slots.get(i);
   Slot replacement=new Slot(house,i,old.x,old.y){
    @Override public boolean mayPlace(ItemStack s){return slot==0 && s.is(Items.HAY_BLOCK);}
    @Override public boolean mayPickup(Player p){return slot<=10;}
    @Override public boolean isActive(){return slot<=10;}
   };
   replacement.index=old.index;slots.set(i,replacement);
  }
 }
}
