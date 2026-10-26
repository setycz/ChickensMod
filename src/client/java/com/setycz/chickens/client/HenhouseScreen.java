package com.setycz.chickens.client;

import com.setycz.chickens.*;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public final class HenhouseScreen extends AbstractContainerScreen<HenhouseMenu> {
    private static final Identifier TEXTURE=ChickensMod.id("textures/gui/henhouse.png");
    public HenhouseScreen(HenhouseMenu menu,Inventory inventory,Component title) {
        super(menu,inventory,title,176,166);
    }
    @Override protected void init() { super.init();titleLabelX=(imageWidth-font.width(title))/2; }
    @Override public void extractBackground(GuiGraphicsExtractor graphics,int mouseX,int mouseY,float partialTick) {
        graphics.blit(RenderPipelines.GUI_TEXTURED,TEXTURE,leftPos,topPos,0,0,imageWidth,imageHeight,256,256);
        int height=menu.fuelPixels(),offset=57-height;
        if(height>0)graphics.blit(RenderPipelines.GUI_TEXTURED,TEXTURE,leftPos+75,topPos+14+offset,195,offset,12,height,256,256);
    }
    @Override protected void extractTooltip(GuiGraphicsExtractor graphics,int mouseX,int mouseY) {
        super.extractTooltip(graphics,mouseX,mouseY);
        if(isHovering(75,14,12,57,mouseX,mouseY)) {
            Component text;
            if(menu.fuelRemaining()>0)text=Component.translatable("gui.chickens.fuel.remaining",menu.fuelRemaining());
            else if(menu.compostFull())text=Component.translatable("gui.chickens.fuel.compost_full");
            else if(menu.hayCount()>0)text=Component.translatable("gui.chickens.fuel.ready");
            else text=Component.translatable("gui.chickens.fuel.empty");
            graphics.setTooltipForNextFrame(font,java.util.List.of(text,Component.translatable("gui.chickens.fuel.hay",menu.hayCount())).stream().map(Component::getVisualOrderText).toList(),mouseX,mouseY);
        }
    }
}
