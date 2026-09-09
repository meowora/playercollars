package org.jlortiz.playercollars.datagen;

import com.mojang.math.Quadrant;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.color.item.Dye;
import net.minecraft.client.color.item.MapColor;
import net.minecraft.client.data.*;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.client.resources.model.cuboid.ItemModelGenerator;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BedPart;
import org.jlortiz.playercollars.PlayerCollarsMod;
import org.jlortiz.playercollars.block.DogBedBlock;
import org.jlortiz.playercollars.block.DogBowlBlock;
import org.jlortiz.playercollars.item.FootPawsItem;

import java.util.Optional;

public class ModelDataGenerator extends FabricModelProvider {

    public ModelDataGenerator(FabricPackOutput output) {
        super(output);
    }

    MultiVariant variant(Identifier model) {
        return new MultiVariant(WeightedList.of(new Variant(model)));
    }

    public MultiVariant createMutator(MultiVariant varianty, Direction direction) {
        return varianty.with(variant -> variant.withYRot(Quadrant.values()[direction.get2DDataValue()])
            .withXRot(Quadrant.R0));
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
        ModelTemplate baseModel = new ModelTemplate(
            Optional.of(PlayerCollarsMod.id("block/white_dog_bed")),
            Optional.empty(),
            TextureSlot.PARTICLE);
        for (DogBedBlock bed : PlayerCollarsMod.DOG_BEDS) {
            var head = variant(PlayerCollarsMod.id("block/" + bed.getColor().getName() + "_dog_bed"));
            blockModelGenerators.blockStateOutput.accept(MultiVariantGenerator.dispatch(bed)
                .with(PropertyDispatch.initial(BedBlock.FACING, BedBlock.PART)
                    .select(Direction.NORTH, BedPart.FOOT, createMutator(head, Direction.NORTH))
                    .select(Direction.SOUTH, BedPart.FOOT, createMutator(head, Direction.SOUTH))
                    .select(Direction.EAST, BedPart.FOOT, createMutator(head, Direction.EAST))
                    .select(Direction.WEST, BedPart.FOOT, createMutator(head, Direction.WEST))
                    .select(Direction.SOUTH, BedPart.HEAD, createMutator(head, Direction.NORTH))
                    .select(Direction.NORTH, BedPart.HEAD, createMutator(head, Direction.SOUTH))
                    .select(Direction.WEST, BedPart.HEAD, createMutator(head, Direction.EAST))
                    .select(Direction.EAST, BedPart.HEAD, createMutator(head, Direction.WEST))));
            if (bed.getColor() != DyeColor.WHITE) {
                baseModel.create(
                    bed,
                    TextureMapping.particle(DatagenEntrypoint.WOOLS[bed.getColor().ordinal()].getBlock()),
                    blockModelGenerators.modelOutput);
            }
        }

        ModelTemplate[] bowlModels = new ModelTemplate[5];
        for (int i = 0; i < 4; i++) {
            bowlModels[i] = new ModelTemplate(
                Optional.of(PlayerCollarsMod.id("block/red_dog_bowl_" + i)),
                Optional.empty(),
                TextureSlot.PARTICLE);
        }
        bowlModels[4] = new ModelTemplate(
            Optional.of(PlayerCollarsMod.id("block/red_dog_bowl_milk")),
            Optional.empty(),
            TextureSlot.PARTICLE);
        for (DogBowlBlock bowl : PlayerCollarsMod.DOG_BOWLS) {
            blockModelGenerators.blockStateOutput.accept(MultiVariantGenerator.dispatch(bowl)
                .with(PropertyDispatch.initial(DogBowlBlock.MILK, DogBowlBlock.LEVEL)
                    .generate((milk, level) -> variant(PlayerCollarsMod.id(
                        "block/" + bowl.color + "_dog_bowl_" + (milk ? "milk" : level))))));
            if (bowl.color != DyeColor.RED) {
                for (int i = 0; i < 4; i++) {
                    bowlModels[i].create(
                        PlayerCollarsMod.id("block/" + bowl.color.getName() + "_dog_bowl_" + i),
                        TextureMapping.particle(DatagenEntrypoint.TERRACOTTAS[bowl.color.ordinal()].getBlock()),
                        blockModelGenerators.modelOutput);
                }
                bowlModels[4].create(
                    PlayerCollarsMod.id("block/" + bowl.color.getName() + "_dog_bowl_milk"),
                    TextureMapping.particle(DatagenEntrypoint.TERRACOTTAS[bowl.color.ordinal()].getBlock()),
                    blockModelGenerators.modelOutput);
            }
        }

        TextureMapping glassTexture = TextureMapping.cube(Blocks.GLASS);
        Identifier glassPost = ModelTemplates.FENCE_POST.create(
            PlayerCollarsMod.INVISIBLE_FENCE_BLOCK,
            glassTexture,
            blockModelGenerators.modelOutput);
        Identifier glassSide = ModelTemplates.FENCE_SIDE.create(
            PlayerCollarsMod.INVISIBLE_FENCE_BLOCK,
            glassTexture,
            blockModelGenerators.modelOutput);
        blockModelGenerators.blockStateOutput.accept(BlockModelGenerators.createFence(
            PlayerCollarsMod.INVISIBLE_FENCE_BLOCK,
            variant(glassPost),
            variant(glassSide)));
        blockModelGenerators.registerSimpleItemModel(
            PlayerCollarsMod.INVISIBLE_FENCE_BLOCK, ModelTemplates.FENCE_INVENTORY.create(
                ModelLocationUtils.getModelLocation(PlayerCollarsMod.INVISIBLE_FENCE_BLOCK_ITEM),
                TextureMapping.cube(Blocks.GLASS),
                blockModelGenerators.modelOutput));
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {

        Item whiteBed = PlayerCollarsMod.DOG_BED_ITEMS[DyeColor.WHITE.ordinal()];
        Identifier bedItemModel = itemModelGenerators.createFlatItemModel(whiteBed, whiteBed, ModelTemplates.FLAT_ITEM);
        for (int i = 0; i < DyeColor.values().length; i++) {
            ItemModel.Unbaked m =
                ItemModelUtils.tintedModel(bedItemModel, new Constant(DyeColor.values()[i].getFireworkColor()));
            itemModelGenerators.itemModelOutput.accept(PlayerCollarsMod.DOG_BED_ITEMS[i], m);
        }

        Identifier pawsModel = PlayerCollarsMod.id("item/paws");
        for (FootPawsItem i : PlayerCollarsMod.PAWS_ITEMS) {
            itemModelGenerators.itemModelOutput.accept(
                i,
                ItemModelUtils.tintedModel(pawsModel, new Dye(i.color), new MapColor(i.beansColor)));
        }
        pawsModel = PlayerCollarsMod.id("item/foot_paws");
        for (FootPawsItem i : PlayerCollarsMod.FOOT_PAWS_ITEMS) {
            itemModelGenerators.itemModelOutput.accept(
                i,
                ItemModelUtils.tintedModel(pawsModel, new Dye(i.color), new MapColor(i.beansColor)));
        }

        itemModelGenerators.itemModelOutput.accept(PlayerCollarsMod.DEED_OF_OWNERSHIP, ItemModelUtils.plainModel(itemModelGenerators.createFlatItemModel(PlayerCollarsMod.DEED_OF_OWNERSHIP, ModelTemplates.FLAT_ITEM)));
        itemModelGenerators.itemModelOutput.accept(PlayerCollarsMod.DEED_OF_OWNERSHIP_STAMPED, ItemModelUtils.plainModel(itemModelGenerators.createFlatItemModel(PlayerCollarsMod.DEED_OF_OWNERSHIP_STAMPED, ModelTemplates.FLAT_ITEM)));
        itemModelGenerators.itemModelOutput.accept(PlayerCollarsMod.COLLAR_LOCKER_ITEM, ItemModelUtils.plainModel(itemModelGenerators.createFlatItemModel(PlayerCollarsMod.COLLAR_LOCKER_ITEM, ModelTemplates.FLAT_ITEM)));
        itemModelGenerators.itemModelOutput.accept(PlayerCollarsMod.SPATULA_ITEM, ItemModelUtils.plainModel(itemModelGenerators.createFlatItemModel(PlayerCollarsMod.SPATULA_ITEM, ModelTemplates.FLAT_HANDHELD_ITEM)));

        for (DyeColor c : DyeColor.values()) {
            ModelTemplate baseBowl =
                new ModelTemplate(
                    Optional.of(PlayerCollarsMod.id("block/" + c.getName() + "_dog_bowl_3")),
                    Optional.empty());
            itemModelGenerators.itemModelOutput.accept(PlayerCollarsMod.DOG_BOWL_ITEMS[c.ordinal()], ItemModelUtils.plainModel(itemModelGenerators.createFlatItemModel(PlayerCollarsMod.DOG_BOWL_ITEMS[c.ordinal()], baseBowl)));
        }
    }
}
