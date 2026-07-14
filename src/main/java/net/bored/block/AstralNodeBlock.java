package net.bored.block;

import net.bored.block.entity.AstralNodeBlockEntity;
import net.bored.content.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class AstralNodeBlock extends BlockWithEntity {
    public enum Kind {
        CONDUIT,
        PYLON,
        COLLECTOR
    }

    private static final VoxelShape CONDUIT_SHAPE = Block.createCuboidShape(2, 0, 2, 14, 4, 14);
    private static final VoxelShape PYLON_SHAPE = Block.createCuboidShape(3, 0, 3, 13, 16, 13);
    private static final VoxelShape COLLECTOR_SHAPE = Block.createCuboidShape(1, 0, 1, 15, 13, 15);
    private final Kind kind;

    public AstralNodeBlock(Kind kind, Settings settings) {
        super(settings);
        this.kind = kind;
    }

    public Kind getKind() {
        return kind;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AstralNodeBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (kind) {
            case CONDUIT -> CONDUIT_SHAPE;
            case PYLON -> PYLON_SHAPE;
            case COLLECTOR -> COLLECTOR_SHAPE;
        };
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
                                                                  BlockEntityType<T> type) {
        return world.isClient ? null : checkType(type, ModBlockEntities.ASTRAL_NODE,
                AstralNodeBlockEntity::tick);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!(world.getBlockEntity(pos) instanceof AstralNodeBlockEntity node) || !node.isEnergized()) {
            return;
        }
        int chance = kind == Kind.CONDUIT ? 12 : 5;
        if (random.nextInt(chance) == 0) {
            double x = pos.getX() + 0.2 + random.nextDouble() * 0.6;
            double y = pos.getY() + (kind == Kind.CONDUIT ? 0.16 : 0.5 + random.nextDouble());
            double z = pos.getZ() + 0.2 + random.nextDouble() * 0.6;
            world.addParticle(kind == Kind.COLLECTOR ? ParticleTypes.END_ROD : ParticleTypes.REVERSE_PORTAL,
                    x, y, z, 0.0, kind == Kind.COLLECTOR ? -0.018 : 0.012, 0.0);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip,
                              TooltipContext options) {
        String key = switch (kind) {
            case CONDUIT -> "tooltip.axiomata.conduit";
            case PYLON -> "tooltip.axiomata.pylon";
            case COLLECTOR -> "tooltip.axiomata.collector";
        };
        tooltip.add(Text.translatable(key).formatted(Formatting.LIGHT_PURPLE));
    }
}
