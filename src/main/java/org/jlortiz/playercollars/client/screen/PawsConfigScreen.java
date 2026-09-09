package org.jlortiz.playercollars.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jlortiz.playercollars.PlayerCollarsMod;
import org.jlortiz.playercollars.network.PawsConfigScreenHandler;

public class PawsConfigScreen<T extends ItemLike> extends AbstractContainerScreen<PawsConfigScreenHandler<T>> {
    private static final Identifier TEXTURE = PlayerCollarsMod.id("textures/gui/paw_controller.png");
    private static final Identifier WIDGETS_TEXTURE = PlayerCollarsMod.id("textures/gui/paw_controller_widgets.png");
    private TagLikeListWidget<T> listWidget;
    private ItemStack stack;

    public PawsConfigScreen(PawsConfigScreenHandler<T> screenHandler, Inventory playerInventory, Component text) {
        super(screenHandler, playerInventory, text, 174, 222);
        stack = ItemStack.EMPTY;
    }

    @Override
    protected void init() {
        inventoryLabelY = imageHeight - 94;
        super.init();
        listWidget = addRenderableWidget(new TagLikeListWidget<>(160, 106, leftPos + 7, topPos + 18,
                minecraft.font.lineHeight, menu.getRegistryKey(), this::handleButtonClick));
        listWidget.setList(menu.listToDisplay);
    }

    private void handleButtonClick(int id) {
        // Call the local function first to prevent a race where the list could be cleared before we update the payload.
        menu.clickMenuButton(minecraft.player, id);
        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id);
        menu.getSlot(0).set(ItemStack.EMPTY);
        if (stack.isEmpty())
            listWidget.setList(menu.listToDisplay);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if (!ItemStack.isSameItem(menu.getSlot(0).getItem(), stack)) {
            stack = menu.getSlot(0).getItem();
            listWidget.setList(menu.listToDisplay);
        }
        super.extractRenderState(graphics, mouseX, mouseY, a);
        extractTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int x = (width - imageWidth - 50) / 2;
        int y = (height - imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth + 50, imageHeight, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, WIDGETS_TEXTURE, x + 7, y + 108, stack.isEmpty() ? 16 : 0, 0, 16, 16, 32, 16);
    }
}
