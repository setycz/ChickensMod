package com.timwoodcreates.roost.client;
import com.timwoodcreates.roost.*;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
public final class MachineScreen extends AbstractContainerScreen<MachineMenu> {
 public MachineScreen(MachineMenu menu,Inventory inv,Component title){super(menu,inv,title,176,menu.kind==2?166:133);}
 @Override public void extractBackground(GuiGraphicsExtractor g,int mx,int my,float partial){
  Identifier texture=menu.kind==2?Identifier.withDefaultNamespace("textures/gui/container/generic_54.png"):RoostMod.id("textures/gui/"+RoostMod.NAMES.get(menu.kind)+".png");
  if(menu.kind==2){
   g.blit(RenderPipelines.GUI_TEXTURED,texture,leftPos,topPos,0,0,176,71,256,256);
   g.blit(RenderPipelines.GUI_TEXTURED,texture,leftPos,topPos+71,0,126,176,96,256,256);
  }else{
   g.blit(RenderPipelines.GUI_TEXTURED,texture,leftPos,topPos,0,0,176,133,256,256);
   int x=menu.kind==0?48:84,y=menu.kind==0?20:22,height=menu.kind==0?16:12;
   int width=menu.progress()==0?0:1+menu.progress()*25/1000;
   if(width>0)g.blit(RenderPipelines.GUI_TEXTURED,texture,leftPos+x,topPos+y,176,0,width,height,256,256);
  }
 }
 @Override protected void extractTooltip(GuiGraphicsExtractor g,int mx,int my){
  super.extractTooltip(g,mx,my);if(menu.kind==2)return;
  int x=menu.kind==0?48:84,y=menu.kind==0?20:22;
  if(isHovering(x,y,26,16,mx,my)){
   String state=switch(menu.status()){case 0->"Insert captured chickens";case 2->"Output full — production paused";case 3->"Requires two seeds per chick";case 4->"Incompatible or disabled breeds";default->"Working";};
   g.setTooltipForNextFrame(font,java.util.List.of(Component.literal(menu.progress()/10.0+"% — "+state).getVisualOrderText()),mx,my);
  }
 }
}
