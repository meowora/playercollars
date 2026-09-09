package org.jlortiz.playercollars.client.screen;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.datafixers.util.Either;
import net.fabricmc.fabric.api.tag.FabricTagKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ItemLike;

public class TagLikeListWidget<T extends ItemLike> extends AbstractSelectionList<TagLikeListWidget<T>.TagEntry> {
    private final ResourceKey<Registry<T>> registryKey;
    private final Consumer<Integer> handleClick;

    public TagLikeListWidget(int width, int height, int x, int y, int itemHeight, ResourceKey<Registry<T>> registryKey, Consumer<Integer> handleClick) {
        super(Minecraft.getInstance(), width, height, y, itemHeight);
        this.registryKey = registryKey;
        this.handleClick = handleClick;
        setX(x);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {

    }

    @Override
    public int getRowWidth() {
        return width - 5;
    }

    @Override
    protected int scrollBarX() {
        return getX() + getRowWidth();
    }

    public void setList(List<Either<TagKey<T>, ResourceKey<T>>> stream) {
        List<TagEntry> entries = new ArrayList<>(stream.size());
        for (int i = 0; i < stream.size(); i++)
            entries.add(i, new TagEntry(i, stream.get(i)));
        replaceEntries(entries);
        setScrollAmount(0);
    }

    @Override
    public int getRowLeft() {
        return getX() + 2;
    }

    private class TransparentButton extends Button {
        protected TransparentButton() {
            super(0, 0, TagLikeListWidget.this.getRowWidth(), TagLikeListWidget.this.defaultEntryHeight,
                    Component.empty(), (x) -> {}, Button.DEFAULT_NARRATION);
        }




        @Override
        protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
            if (this.isHovered())
                graphics.fill(getX(), getY(), getX() + getWidth(), getY() + height, 0x999999 + (142 << 24));
        }
    }

    public class TagEntry extends AbstractSelectionList.Entry<TagEntry> {
        private final Component label;
        private final TransparentButton button;
        private final int index;

        private TagEntry(int index, Either<TagKey<T>, ResourceKey<T>> key) {
            this.label = key.map(FabricTagKey::getName, (x) -> Minecraft.getInstance().level
                    .registryAccess().lookupOrThrow(registryKey).getOptional(x)
                    .map((y) -> y.asItem().getDefaultInstance().getHoverName()).orElse(Component.literal("error!")));
            this.button = new TransparentButton();
            this.index = index;
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
            this.button.setPosition(x(), y() - 2);
            this.button.extractContents(graphics, mouseX, mouseY, a);
            graphics.text(minecraft.font, label, x(), y() - 1, 0xFFFFFF, false);
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            if (this.button.mouseClicked(event, doubleClick)) {
                TagLikeListWidget.this.handleClick.accept(this.index);
                return true;
            }
            return super.mouseClicked(event, doubleClick);
        }

        @Override
        public boolean mouseReleased(MouseButtonEvent event) {
            return this.button.mouseReleased(event);
        }
    }
}
