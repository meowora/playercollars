package org.jlortiz.playercollars.block;

import io.wispforest.accessories.api.AccessoriesCapability;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jlortiz.playercollars.PlayerCollarsMod;

import java.util.List;
import java.util.Optional;

public class InvisibleFenceBlock extends FenceBlock {
    public static final ResourceKey<Block>
        REGISTRY_KEY = ResourceKey.create(Registries.BLOCK, PlayerCollarsMod.id("invisible_fence"));
    public static final ResourceKey<Item> ITEM_REGISTRY_KEY = ResourceKey.create(Registries.ITEM,  PlayerCollarsMod.id("invisible_fence"));
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public InvisibleFenceBlock(BlockBehaviour.Properties settings) {
        super(settings.setId(REGISTRY_KEY));
        registerDefaultState(this.getStateDefinition().any().setValue(POWERED, false).setValue(WATERLOGGED, false));
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }

    @Override
    protected void updateIndirectNeighbourShapes(
        BlockState state,
        LevelAccessor level,
        BlockPos pos,
        @UpdateFlags int updateFlags,
        int updateLimit) {
        super.updateIndirectNeighbourShapes(state, level, pos, updateFlags, updateLimit);
    }

    @Override
    protected BlockState updateShape(
        BlockState state,
        LevelReader level,
        ScheduledTickAccess ticks,
        BlockPos pos,
        Direction directionToNeighbour,
        BlockPos neighbourPos,
        BlockState neighbourState,
        RandomSource random) {
        state = super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
        if (neighbourState.is(this) && neighbourState.getValue(POWERED) != state.getValue(POWERED)) {
            state = state.setValue(POWERED, neighbourState.getValue(POWERED));
        }
        return state;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        var state =  super.getStateForPlacement(ctx);
        BlockState neighbor = ctx.getLevel().getBlockState(ctx.getClickedPos().north());
        boolean shouldPower = neighbor.is(this) && neighbor.getValue(POWERED);
        if (!shouldPower) {
            neighbor =  ctx.getLevel().getBlockState(ctx.getClickedPos().east());
            shouldPower = neighbor.is(this) && neighbor.getValue(POWERED);
        }
        if (!shouldPower) {
            neighbor =  ctx.getLevel().getBlockState(ctx.getClickedPos().south());
            shouldPower = neighbor.is(this) && neighbor.getValue(POWERED);
        }
        if (!shouldPower) {
            neighbor =  ctx.getLevel().getBlockState(ctx.getClickedPos().west());
            shouldPower = neighbor.is(this) && neighbor.getValue(POWERED);
        }
        if (shouldPower) state = state.setValue(POWERED, true);
        return state;
    }

    @Override
    protected VoxelShape getCollisionShape(
        BlockState state,
        BlockGetter level,
        BlockPos pos,
        CollisionContext context) {
        if (context instanceof EntityCollisionContext e) {
            if (state.getValue(POWERED) && e.getEntity() instanceof LivingEntity livingEntity) {
                AccessoriesCapability cap = AccessoriesCapability.get(livingEntity);
                if (cap == null) return Shapes.empty();

                return cap.getEquipped((y) -> y.is(PlayerCollarsMod.COLLAR_TAG)).isEmpty() ?
                    Shapes.empty() : super.getCollisionShape(state, level, pos, context);
            }
            // Vertical collision is cached using EntityShapeContext.ABSENT.
            // This will be re-checked if something actually lands on the fence, so this is safe for players.
            // It can cause unusual behaviour if something tries to pathfind through it, so that is left disabled.
            if (e.getEntity() == null) return super.getCollisionShape(state, level, pos, context);
        }
        return Shapes.empty();
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (state.getValue(POWERED) && random.nextFloat() < 0.25) {
            ParticleUtils.spawnParticles(level, pos, 1, 0.5, 0.5, true, DustParticleOptions.REDSTONE);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        BlockHitResult hitResult) {
        if (level.isClientSide()) return InteractionResult.PASS;
        if (!Optional.ofNullable(AccessoriesCapability.get(player)).map((x) -> x.getEquipped((y) -> y.is(PlayerCollarsMod.COLLAR_TAG)))
            .map(List::isEmpty).orElse(true)) {
            player.sendOverlayMessage(Component.translatable("block.playercollars.invisible_fence.toggle_fail").withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }
        state = state.setValue(POWERED, !state.getValue(POWERED));
        level.setBlock(pos, state, Block.UPDATE_NEIGHBORS | Block.UPDATE_INVISIBLE | Block.UPDATE_CLIENTS);
        player.sendOverlayMessage(Component.translatable(
                state.getValue(POWERED) ? "block.playercollars.invisible_fence.toggle_on"
                    : "block.playercollars.invisible_fence.toggle_off")
            .withStyle(ChatFormatting.GREEN));
        return InteractionResult.SUCCESS;
    }
}