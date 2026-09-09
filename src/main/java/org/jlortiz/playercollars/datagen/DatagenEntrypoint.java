package org.jlortiz.playercollars.datagen;

import io.wispforest.accessories.Accessories;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.references.BlockItemIds;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BedPart;
import org.jlortiz.playercollars.PlayerCollarsMod;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import org.jlortiz.playercollars.block.InvisibleFenceBlock;
import org.jlortiz.playercollars.item.CollarItem;
import org.jspecify.annotations.Nullable;

public class DatagenEntrypoint implements DataGeneratorEntrypoint {
    public static final BlockItem[] WOOLS = new BlockItem[DyeColor.values().length];
    public static final BlockItem[] TERRACOTTAS = new BlockItem[DyeColor.values().length];

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        for (DyeColor c : DyeColor.values()) {
            WOOLS[c.ordinal()] =
                (BlockItem) BuiltInRegistries.ITEM.getValue(Identifier.withDefaultNamespace(c.getName() + "_wool"));
            TERRACOTTAS[c.ordinal()] = (BlockItem) BuiltInRegistries.ITEM.getValue(Identifier.withDefaultNamespace(
                c.getName() + "_terracotta"));
        }

        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(RecipeDataGenerator::new);
        pack.addProvider(ModelDataGenerator::new);
        pack.addProvider(LootTableGenerator::new);
        pack.addProvider(ItemTagGenerator::new);
        pack.addProvider(BlockTagGenerator::new);
        pack.addProvider(EnglishLangProvider::new);
    }

    private static class LootTableGenerator extends FabricBlockLootSubProvider {

        protected LootTableGenerator(
            FabricPackOutput packOutput,
            CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(packOutput, registriesFuture);
        }

        @Override
        public void generate() {
            for (int i = 0; i < PlayerCollarsMod.DOG_BEDS.length; i++) {
                add(
                    PlayerCollarsMod.DOG_BEDS[i],
                    createSinglePropConditionTable(PlayerCollarsMod.DOG_BEDS[i], BedBlock.PART, BedPart.HEAD));
            }
            for (int i = 0; i < PlayerCollarsMod.DOG_BOWLS.length; i++) {
                dropOther(PlayerCollarsMod.DOG_BOWLS[i], PlayerCollarsMod.DOG_BOWL_ITEMS[i]);
            }
            dropSelf(PlayerCollarsMod.INVISIBLE_FENCE_BLOCK);
        }
    }

    private static class ItemTagGenerator extends FabricTagsProvider.ItemTagsProvider {

        public ItemTagGenerator(
            FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> registryLookupFuture,
            @Nullable BlockTagsProvider blockTagsProvider) {
            super(output, registryLookupFuture, blockTagsProvider);
        }

        public ItemTagGenerator(
            FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
            super(output, registryLookupFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider registries) {
            builder(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Accessories.MODID, "necklace"))).add(
                CollarItem.REGISTRY_KEY).add(CollarItem.TAGLESS_REGISTRY_KEY);
            var hand =
                builder(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Accessories.MODID, "hand")));
            var paws = builder(PlayerCollarsMod.PAWS_TAG);
            for (var pawsItem : PlayerCollarsMod.PAWS_ITEMS) {
                hand.add(BuiltInRegistries.ITEM.getResourceKey(pawsItem).orElseThrow());
                paws.add(BuiltInRegistries.ITEM.getResourceKey(pawsItem).orElseThrow());
            }

            var shoes =
                builder(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Accessories.MODID, "shoes")));
            var footPaws = builder(PlayerCollarsMod.FOOT_PAWS_TAG);
            for (var pawsItem : PlayerCollarsMod.FOOT_PAWS_ITEMS) {
                shoes.add(BuiltInRegistries.ITEM.getResourceKey(pawsItem).orElseThrow());
                footPaws.add(BuiltInRegistries.ITEM.getResourceKey(pawsItem).orElseThrow());
            }
            //copy(BlockTags.BUTTONS, PlayerCollarsMod.BUTTONS);

            builder(PlayerCollarsMod.COLLAR_TAG).add(CollarItem.REGISTRY_KEY).add(CollarItem.TAGLESS_REGISTRY_KEY);

        }
    }

    private static class BlockTagGenerator extends FabricTagsProvider.BlockTagsProvider {

        public BlockTagGenerator(
            FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
            super(output, registryLookupFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider registries) {
            var beds = builder(BlockTags.BEDS);
            for (var bed : PlayerCollarsMod.DOG_BEDS) {
                beds.add(BuiltInRegistries.BLOCK.getResourceKey(bed).orElseThrow());
            }

            builder(BlockTags.FENCES).add(InvisibleFenceBlock.REGISTRY_KEY);


            //builder(PlayerCollarsMod.PAWS_ALLOW_INTERACT)
            //    .addTag(BlockTags.BUTTONS)
            //    .add(BlockItemIds.LEVER)
            //    .addTag(BlockTags.CROPS)
            //    .addTag(BlockTags.BEDS)
            //    .addTag(BlockTags.GEODE_INVALID_BLOCKS)
            //    .addTag(BlockTags.CAULDRONS);
        }
    }

    private static class EnglishLangProvider extends FabricLanguageProvider {

        protected EnglishLangProvider(
            FabricPackOutput packOutput,
            CompletableFuture<HolderLookup.Provider> registryLookup) {
            super(packOutput, registryLookup);
        }

        private static void generateColorNames(
            TranslationBuilder translationBuilder,
            String suffix,
            Function<Integer, DyeColor> getColor,
            Item... items) {
            String[] keys = new String[items.length];
            for (int i = 0; i < items.length; i++) {
                keys[i] = items[i].getDescriptionId();
            }
            generateColorNames(translationBuilder, suffix, getColor, keys);
        }

        private static void generateColorNames(
            TranslationBuilder translationBuilder,
            String suffix,
            Function<Integer, DyeColor> getColor,
            Block... blocks) {
            String[] keys = new String[blocks.length];
            for (int i = 0; i < blocks.length; i++) {
                keys[i] = blocks[i].getDescriptionId();
            }
            generateColorNames(translationBuilder, suffix, getColor, keys);
        }

        private static void generateColorNames(
            TranslationBuilder translationBuilder,
            String suffix,
            Function<Integer, DyeColor> getColor,
            String... keys) {
            for (int i = 0; i < keys.length; i++) {
                String pre = getColor.apply(i).getName();
                char[] buf = new char[pre.length() + suffix.length()];
                boolean newWord = true;
                for (int j = 0; j < pre.length(); j++) {
                    char c = pre.charAt(j);
                    if (c == '_') {
                        c = ' ';
                        newWord = true;
                    } else if (newWord) {
                        c = Character.toUpperCase(c);
                        newWord = false;
                    }
                    buf[j] = c;
                }
                suffix.getChars(0, buf.length - pre.length(), buf, pre.length());
                translationBuilder.add(keys[i], String.valueOf(buf));
            }
        }

        @Override
        public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
            generateColorNames(
                translationBuilder,
                " Human-Sized Dog Bed",
                DyeColor::byId,
                PlayerCollarsMod.DOG_BED_ITEMS);
            generateColorNames(translationBuilder, " Human-Sized Dog Bed", DyeColor::byId, PlayerCollarsMod.DOG_BEDS);
            generateColorNames(
                translationBuilder,
                " Paws",
                (i) -> PlayerCollarsMod.PAWS_DYE_COLORS[i],
                PlayerCollarsMod.PAWS_ITEMS);
            generateColorNames(
                translationBuilder,
                " Foot Paws",
                (i) -> PlayerCollarsMod.PAWS_DYE_COLORS[i],
                PlayerCollarsMod.FOOT_PAWS_ITEMS);
            generateColorNames(translationBuilder, " Dog Bowl", DyeColor::byId, PlayerCollarsMod.DOG_BOWL_ITEMS);
            generateColorNames(translationBuilder, " Dog Bowl", DyeColor::byId, PlayerCollarsMod.DOG_BOWLS);

            try {
                Path existingFilePath = packOutput.getModContainer()
                    .findPath("assets/" + PlayerCollarsMod.MOD_ID + "/lang/en_us.existing.json")
                    .get();
                translationBuilder.add(existingFilePath);
            } catch (Exception e) {
                throw new RuntimeException("Failed to add existing language file!", e);
            }
        }
    }
}
