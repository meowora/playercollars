package org.jlortiz.playercollars.item;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import io.wispforest.accessories.api.core.AccessoryItem;
import io.wispforest.accessories.api.attributes.AccessoryAttributeBuilder;
import io.wispforest.accessories.api.events.DropRule;
import io.wispforest.accessories.api.slot.SlotReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.MapItemColor;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.MapColor;
import org.jlortiz.playercollars.OwnerComponent;
import org.jlortiz.playercollars.PlayerCollarsMod;
import org.jlortiz.playercollars.client.screen.CollarDyeScreen;

import java.util.List;

public class CollarItem extends AccessoryItem {
    public static final ResourceKey<Item>
        REGISTRY_KEY = ResourceKey.create(Registries.ITEM, PlayerCollarsMod.id("collar"));
    public static final ResourceKey<Item> TAGLESS_REGISTRY_KEY = ResourceKey.create(Registries.ITEM, PlayerCollarsMod.id("tagless_collar"));
    public final boolean tagless;

    public CollarItem(boolean tagless) {
        super(new Item.Properties().stacksTo(1).setId(tagless ? TAGLESS_REGISTRY_KEY : REGISTRY_KEY)
                .component(DataComponents.ENCHANTABLE, new Enchantable(100))
                .component(DataComponents.DYED_COLOR, new DyedItemColor(MapColor.COLOR_RED.col))
                .component(DataComponents.MAP_COLOR, new MapItemColor(MapColor.COLOR_BLUE.col)));
        this.tagless = tagless;
    }

    public static int getColor(ItemStack itemStack) {
        var $$1 = itemStack.get(DataComponents.DYED_COLOR);
        return $$1 != null ? $$1.rgb() : MapColor.COLOR_RED.col | 0xFF000000;
    }

    public static int getPawColor(ItemStack itemStack) {
        var $$1 = itemStack.get(DataComponents.MAP_COLOR);
        return $$1 != null ? $$1.rgb() : MapColor.COLOR_BLUE.col;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack is = player.getItemInHand(hand);
        if (player.isCrouching() && level.isClientSide()) {
            Minecraft.getInstance().gui.setScreen(new CollarDyeScreen(is, player.getUUID()));
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public Component getName(ItemStack stack) {
        OwnerComponent owner = stack.get(PlayerCollarsMod.OWNER_COMPONENT_TYPE);
        if (owner != null && owner.ownedName().isPresent())
            return Component.translatable("item.playercollars.collar.named", owner.ownedName().get());
        return super.getName(stack);
    }

    @Override
    public void getDynamicModifiers(ItemStack stack, SlotReference reference, AccessoryAttributeBuilder builder) {
        EnchantmentHelper.forEachModifier(stack, EquipmentSlotGroup.ANY, builder::addExclusive);
    }

    @Override
    public DropRule getDropRule(ItemStack stack, SlotReference reference, DamageSource source) {
        return DropRule.KEEP;
    }
}
