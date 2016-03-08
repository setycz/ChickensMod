package com.setycz.chickens.henhouse;

import com.setycz.chickens.ChickensMod;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by setyc on 06.03.2016.
 */
@SideOnly(Side.CLIENT)
public class GuiHenhouse extends GuiContainer {
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ChickensMod.MODID, "textures/gui/henhouse.png");
    private final TileEntityHenhouse tileEntityHenhouse;

    public GuiHenhouse(InventoryPlayer playerInv, TileEntityHenhouse tileEntityHenhouse) {
        super(new ContainerHenhouse(playerInv, tileEntityHenhouse));
        this.tileEntityHenhouse = tileEntityHenhouse;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String energy = String.valueOf(tileEntityHenhouse.getEnergy());
        fontRendererObj.drawString(energy, 10, 10, 4210752);
    }
}
