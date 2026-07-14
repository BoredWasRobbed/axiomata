package net.bored.block;

import net.bored.block.entity.AstralAnchorBlockEntity;
import net.bored.content.ModBlockEntities;
import net.bored.content.ModItems;
import net.bored.item.AstralKeyItem;
import net.bored.storage.AstralColors;
import net.minecraft.block.Block;
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
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.UUID;

public final class AstralAnchorBlock extends BlockWithEntity {
    private static final VoxelShape SHAPE = VoxelShapes.union(
            Block.createCuboidShape(1, 0, 1, 15, 3, 15),
            Block.createCuboidShape(4, 3, 4, 12, 13, 12));

    public AstralAnchorBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AstralAnchorBlockEntity(pos, state);
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
        if (!(world.getBlockEntity(pos) instanceof AstralAnchorBlockEntity anchor)) {
            return ActionResult.PASS;
        }
        ItemStack held = player.getStackInHand(hand);
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return ActionResult.CONSUME;
        }

        if (held.isOf(ModItems.ASTRAL_KEY)) {
            UUID anchorNetwork = anchor.ensureNetwork();
            if (player.isSneaking() && AstralKeyItem.hasNetwork(held)) {
                UUID keyNetwork = AstralKeyItem.getNetwork(held);
                anchor.setNetwork(keyNetwork);
                player.sendMessage(Text.translatable("message.axiomata.anchor_rebound",
                        AstralColors.shortId(keyNetwork)).formatted(Formatting.LIGHT_PURPLE), false);
            } else {
                AstralKeyItem.setNetwork(held, anchorNetwork);
                player.sendMessage(Text.translatable("message.axiomata.key_attuned",
                        AstralColors.shortId(anchorNetwork)).formatted(Formatting.AQUA), false);
                anchor.markAccess();
            }
            world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME,
                    SoundCategory.BLOCKS, 1.0f, 1.4f);
            return ActionResult.CONSUME;
        }

        if (held.isOf(ModItems.ASTRAL_CELL)) {
            int after = anchor.tryUpgrade();
            if (after > 0) {
                if (!player.getAbilities().creativeMode) {
                    held.decrement(1);
                }
                player.sendMessage(Text.translatable("message.axiomata.cell_absorbed",
                        after * 54).formatted(Formatting.LIGHT_PURPLE), false);
                world.playSound(null, pos, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL,
                        SoundCategory.BLOCKS, 1.1f, 0.8f + after * 0.12f);
            } else {
                player.sendMessage(Text.translatable("message.axiomata.capacity_max")
                        .formatted(Formatting.GRAY), true);
            }
            return ActionResult.CONSUME;
        }

        anchor.markAccess();
        serverPlayer.openHandledScreen(anchor);
        world.playSound(null, pos, SoundEvents.BLOCK_ENDER_CHEST_OPEN,
                SoundCategory.BLOCKS, 0.8f, 1.25f);
        return ActionResult.CONSUME;
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return world.getBlockEntity(pos) instanceof AstralAnchorBlockEntity anchor
                ? anchor.getComparatorOutput() : 0;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
                                                                  BlockEntityType<T> type) {
        return world.isClient ? null : checkType(type, ModBlockEntities.ASTRAL_ANCHOR,
                AstralAnchorBlockEntity::tick);
    }
}
