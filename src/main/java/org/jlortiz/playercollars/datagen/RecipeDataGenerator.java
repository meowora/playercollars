package org.jlortiz.playercollars.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BedItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jlortiz.playercollars.PlayerCollarsMod;
import org.jlortiz.playercollars.item.PawsItem;

import java.util.concurrent.CompletableFuture;

public class RecipeDataGenerator extends FabricRecipeProvider {
    public RecipeDataGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        return new RecipeProvider(registries, output) {
            @Override
            public void buildRecipes() {
                shaped(RecipeCategory.MISC, PlayerCollarsMod.COLLAR_ITEM).pattern(" l ").pattern("lil").pattern(" d ")
                        .define('l', Items.LEATHER)
                        .define('i', ConventionalItemTags.GOLD_INGOTS)
                        .define('d', ConventionalItemTags.DYES)
                        .unlockedBy(getItemName(Items.LEATHER), has(Items.LEATHER))
                        .save(output);
                shaped(RecipeCategory.MISC, PlayerCollarsMod.TAGLESS_COLLAR_ITEM).pattern(" l ").pattern("ldl")
                        .define('l', Items.LEATHER)
                        .define('d', ConventionalItemTags.DYES)
                        .unlockedBy(getItemName(Items.LEATHER), has(Items.LEATHER))
                        .save(output);
                shaped(RecipeCategory.MISC, PlayerCollarsMod.CLICKER_ITEM).pattern(" b ").pattern("pip").pattern(" p ")
                        .define('b', ItemTags.WOODEN_BUTTONS)
                        .define('i', ConventionalItemTags.IRON_INGOTS)
                        .define('p', ItemTags.PLANKS)
                        .unlockedBy(getItemName(Items.IRON_INGOT), has(ConventionalItemTags.IRON_INGOTS))
                        .save(output);
                shapeless(RecipeCategory.TOOLS, PlayerCollarsMod.PAW_CONFIGURATION_ITEM)
                        .requires(ConventionalItemTags.REDSTONE_DUSTS)
                        .requires(ConventionalItemTags.COPPER_INGOTS)
                        .requires(PlayerCollarsMod.COLLAR_LOCKER_ITEM)
                        .unlockedBy("has_paws", has(PlayerCollarsMod.PAWS_TAG))
                        .save(output);
                shapeless(RecipeCategory.TOOLS, PlayerCollarsMod.COLLAR_LOCKER_ITEM)
                        .requires(ConventionalItemTags.CHAINS)
                        .requires(ConventionalItemTags.CHAINS)
                        .requires(ConventionalItemTags.REDSTONE_DUSTS)
                        .requires(Items.IRON_BARS)
                        .unlockedBy(getItemName(PlayerCollarsMod.DEED_OF_OWNERSHIP_STAMPED),
                                has(PlayerCollarsMod.DEED_OF_OWNERSHIP_STAMPED))
                        .save(output);
                shapeless(RecipeCategory.MISC, PlayerCollarsMod.DEED_OF_OWNERSHIP)
                        .requires(Items.PAPER)
                        .requires(Items.LEAD)
                        .requires(Items.INK_SAC)
                        .requires(Items.FEATHER)
                        .unlockedBy(getItemName(Items.PAPER), has(Items.PAPER))
                        .save(output);
                shaped(RecipeCategory.BUILDING_BLOCKS, PlayerCollarsMod.INVISIBLE_FENCE_BLOCK_ITEM, 3).pattern("grg").pattern("srs")
                        .define('r', Items.REDSTONE)
                        .define('g', Items.GLASS_PANE)
                        .define('s', Items.STONE)
                        .unlockedBy(getItemName(Items.REDSTONE), has(Items.REDSTONE))
                        .save(output);
                shaped(RecipeCategory.TOOLS, PlayerCollarsMod.SPATULA_ITEM).pattern("  g").pattern(" g ").pattern("s  ")
                        .define('g', ConventionalItemTags.GOLD_INGOTS)
                        .define('s', Items.STICK)
                        .unlockedBy(getItemName(Items.GOLD_INGOT), has(ConventionalItemTags.GOLD_INGOTS))
                        .save(output);
                for (DyeColor c : DyeColor.values()) {
                    generateBed(output, PlayerCollarsMod.DOG_BED_ITEMS[c.ordinal()], DatagenEntrypoint.WOOLS[c.ordinal()]);
                    generateBowl(output, PlayerCollarsMod.DOG_BOWL_ITEMS[c.ordinal()], DatagenEntrypoint.TERRACOTTAS[c.ordinal()]);
                }
                for (int i = 0; i < PlayerCollarsMod.PAWS_DYE_COLORS.length; i++) {
                    generatePaws(output, PlayerCollarsMod.PAWS_ITEMS[i], DatagenEntrypoint.WOOLS[PlayerCollarsMod.PAWS_DYE_COLORS[i].ordinal()]);
                    generateFootPaws(output, PlayerCollarsMod.FOOT_PAWS_ITEMS[i], DatagenEntrypoint.WOOLS[PlayerCollarsMod.PAWS_DYE_COLORS[i].ordinal()]);
                }
            }

            private void generateBed(RecipeOutput output, BedItem outputItem, Item define) {
                shaped(RecipeCategory.DECORATIONS, outputItem).pattern("w w").pattern("www")
                        .define('w', define)
                        .unlockedBy(getItemName(define), has(define))
                        .group("dog_bed")
                        .save(output);
            }

            private void generatePaws(RecipeOutput output, PawsItem outputItem, Item define) {
                shaped(RecipeCategory.MISC, outputItem).pattern(" w ").pattern("wlw").pattern(" w ")
                        .define('w', define)
                        .define('l', Items.LEATHER)
                        .unlockedBy(getItemName(define), has(define))
                        .group("paws")
                        .save(output);
            }

            private void generateFootPaws(RecipeOutput output, Item outputItem, Item define) {
                shaped(RecipeCategory.MISC, outputItem).pattern(" w ").pattern(" w ").pattern("wlw")
                        .define('w', define)
                        .define('l', Items.LEATHER)
                        .unlockedBy(getItemName(Items.LEATHER), has(Items.LEATHER))
                        .group("foot_paws")
                        .save(output);
            }

            private void generateBowl(RecipeOutput output, Item outputItem, Item define) {
                shaped(RecipeCategory.DECORATIONS, outputItem).pattern("w w").pattern("www")
                        .define('w', define)
                        .unlockedBy(getItemName(define), has(define))
                        .group("dog_bowl")
                        .save(output);
            }
        };
    }

    @Override
    public String getName() {
        return PlayerCollarsMod.MOD_ID + "_recipe_generator";
    }

}
