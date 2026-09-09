package org.jlortiz.playercollars.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeSerializers;
import net.minecraft.world.level.Level;
import org.jlortiz.playercollars.OwnerComponent;
import org.jlortiz.playercollars.PlayerCollarsMod;

import java.util.List;

public class OwnershipCraftingRecipe extends CustomRecipe {
    private final CraftingBookCategory category;
    private PlacementInfo ingredientPlacement;
    private final Ingredient base;

    public OwnershipCraftingRecipe(CraftingBookCategory category, Ingredient base) {
        super();
        this.category = category;
        this.base = base;
    }

    @Override
    public CraftingBookCategory category() {
        return this.category;
    }

    private Ingredient getBase() {
        return base;
    }

    @Override
    public PlacementInfo placementInfo() {
        if (ingredientPlacement == null) {
            ingredientPlacement =
                PlacementInfo.create(List.of(base, Ingredient.of(PlayerCollarsMod.DEED_OF_OWNERSHIP_STAMPED)));
        }

        return ingredientPlacement;
    }

    public static RecipeSerializer<OwnershipCraftingRecipe> SERIALIZER = new RecipeSerializer<>(
        RecordCodecBuilder.mapCodec((builder) -> builder.group(
                CraftingBookCategory.CODEC.fieldOf("category")
                    .orElse(CraftingBookCategory.MISC)
                    .forGetter(OwnershipCraftingRecipe::category),
                Ingredient.CODEC.fieldOf("base").forGetter(OwnershipCraftingRecipe::getBase))
            .apply(builder, OwnershipCraftingRecipe::new)), StreamCodec.composite(
        CraftingBookCategory.STREAM_CODEC,
        OwnershipCraftingRecipe::category,
        Ingredient.CONTENTS_STREAM_CODEC,
        OwnershipCraftingRecipe::getBase,
        OwnershipCraftingRecipe::new));


    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (!input.stackedContents().canCraft(this, null)) {
            return false;
        }

        for (int i = 0; i < input.size(); i++) {
            ItemStack is = input.getItem(i);
            if (base.test(is) && is.get(PlayerCollarsMod.OWNER_COMPONENT_TYPE) != null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        ItemStack output = ItemStack.EMPTY;
        OwnerComponent owner = null;

        for (int j = 0; j < input.size(); j++) {
            ItemStack is = input.getItem(j);
            if (!is.isEmpty()) {
                if (is.is(PlayerCollarsMod.DEED_OF_OWNERSHIP_STAMPED)) {
                    owner = is.get(PlayerCollarsMod.OWNER_COMPONENT_TYPE);
                } else if (base.test(is)) {
                    output = is.copy();
                }
            }
        }

        if (owner == null || output.isEmpty()) {
            return ItemStack.EMPTY;
        }
        output.set(PlayerCollarsMod.OWNER_COMPONENT_TYPE, owner);
        return output;
    }

    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return SERIALIZER;
    }


}
