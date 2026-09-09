package org.jlortiz.playercollars.item;

import io.wispforest.accessories.api.core.AccessoryItem;
import io.wispforest.accessories.api.events.DropRule;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.MapItemColor;
import org.jlortiz.playercollars.PlayerCollarsMod;

public class FootPawsItem extends AccessoryItem {
    public final int color, beansColor;

    public FootPawsItem(ResourceKey<Item> key, int color, int beansColor) {
        super(new Item.Properties().stacksTo(1).setId(key)
                .component(DataComponents.DYED_COLOR, new DyedItemColor(color | 0xFF000000))
                .component(DataComponents.MAP_COLOR, new MapItemColor(beansColor))
        );
        this.color = color | 0xFF000000;
        this.beansColor = beansColor;
    }

    public static ResourceKey<Item> getRegistryKey(DyeColor c) {
        return ResourceKey.create(Registries.ITEM, PlayerCollarsMod.id(c.getName() + "_foot_paws"));
    }

    @Override
    public DropRule getDropRule(ItemStack stack, SlotReference reference, DamageSource source) {
        return DropRule.KEEP;
    }
}
