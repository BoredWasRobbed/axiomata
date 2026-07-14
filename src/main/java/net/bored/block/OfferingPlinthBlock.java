package net.bored.block;

import net.bored.block.entity.OfferingPlinthBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public final class OfferingPlinthBlock extends BlockWithEntity {
    private static final VoxelShape SHAPE = Block.createCuboidShape(2, 0, 2, 14, 12, 14);

    public OfferingPlinthBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new OfferingPlinthBlockEntity(pos, state);
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
        if (!(world.getBlockEntity(pos) instanceof OfferingPlinthBlockEntity plinth)) {
            return ActionResult.PASS;
        }
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        ItemStack held = player.getStackInHand(hand);
        ItemStack offered = plinth.getStack(0);
        if (offered.isEmpty() && !held.isEmpty()) {
            ItemStack single = held.copy();
            single.setCount(1);
            plinth.setStack(0, single);
            if (!player.getAbilities().creativeMode) {
                held.decrement(1);
            }
            return ActionResult.CONSUME;
        }
        if (!offered.isEmpty() && held.isEmpty()) {
            player.giveItemStack(plinth.removeStack(0));
            return ActionResult.CONSUME;
        }
        return ActionResult.CONSUME;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof OfferingPlinthBlockEntity plinth) {
                ItemScatterer.spawn(world, pos, plinth);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
}
