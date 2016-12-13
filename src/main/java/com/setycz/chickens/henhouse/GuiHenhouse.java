package com.setycz.chickens.henhouse;

import com.setycz.chickens.ChickensMod;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by setyc on 06.03.2016.
 */
@SuppressWarnings("WeakerAccess")
@SideOnly(Side.CLIENT)
public class GuiHenhouse extends GuiContainer {
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ChickensMod.MODID, "textures/gui/henhouse.png");
    private final InventoryPlayer playerInv;
    private final TileEntityHenhouse tileEntityHenhouse;

    public GuiHenhouse(InventoryPlayer playerInv, TileEntityHenhouse tileEntityHenhouse) {
        super(new ContainerHenhouse(playerInv, tileEntityHenhouse));
        this.playerInv = playerInv;
        this.tileEntityHenhouse = tileEntityHenhouse;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(GUI_TEXTURE);
        int i = (width - xSize) / 2;
        int j = (height - ySize) / 2;
        drawTexturedModalRect(i, j, 0, 0, xSize, ySize);

        int energy = tileEntityHenhouse.getEnergy();
        final int BAR_HEIGHT = 57;
        int offset = BAR_HEIGHT - (energy * BAR_HEIGHT / TileEntityHenhouse.hayBaleEnergy);
        drawTexturedModalRect(i + 75, j + 14 + offset, 195, offset, 12, BAR_HEIGHT - offset);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        ITextComponent henhouseDisplayName = tileEntityHenhouse.getDisplayName();
        assert henhouseDisplayName != null;
        String henhouseName = henhouseDisplayName.getUnformattedText();
        this.fontRendererObj.drawString(
                henhouseName,
                xSize / 2 - fontRendererObj.getStringWidth(henhouseName) / 2, 6,
                4210752);
        this.fontRendererObj.drawString(
                playerInv.getDisplayName().getUnformattedText(),
                8, ySize - 96 + 2,
                4210752);
    }
}
