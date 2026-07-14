package net.bored.block;

import net.bored.block.entity.RitualNexusBlockEntity;
import net.bored.content.ModBlockEntities;
import net.bored.content.ModItems;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public final class RitualNexusBlock extends BlockWithEntity {
    private static final VoxelShape SHAPE = VoxelShapes.union(
            createCuboidShape(2, 0, 2, 14, 4, 14),
            createCuboidShape(4, 4, 4, 12, 11, 12));

    public RitualNexusBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RitualNexusBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
                              BlockHitResult hit) {
        ItemStack held = player.getStackInHand(hand);
        if (!held.isEmpty() && !held.isOf(ModItems.RESONANCE_TUNER)) {
            return ActionResult.PASS;
        }
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        if (!(player instanceof ServerPlayerEntity serverPlayer)
                || !(world.getBlockEntity(pos) instanceof RitualNexusBlockEntity nexus)) {
            return ActionResult.CONSUME;
        }
        if (held.isOf(ModItems.RESONANCE_TUNER) && !player.isSneaking()) {
            nexus.tryStart(serverPlayer);
        } else {
            nexus.inspect(serverPlayer);
        }
        return ActionResult.CONSUME;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
                                                                  BlockEntityType<T> type) {
        return world.isClient ? null : checkType(type, ModBlockEntities.RITUAL_NEXUS, RitualNexusBlockEntity::tick);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock()) && world.getBlockEntity(pos) instanceof RitualNexusBlockEntity nexus) {
            nexus.releaseRunes();
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
