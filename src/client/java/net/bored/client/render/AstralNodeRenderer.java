package net.bored.client.render;

import net.bored.block.entity.AstralNodeBlockEntity;
import net.bored.content.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public final class AstralNodeRenderer implements BlockEntityRenderer<AstralNodeBlockEntity> {
    public AstralNodeRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(AstralNodeBlockEntity node, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider consumers, int light, int overlay) {
        if (node.getWorld() == null) {
            return;
        }
        float time = node.getWorld().getTime() + tickDelta;
        int color = node.isEnergized() ? node.getNetworkColor() : 0x5D526E;
        int bright = AstralAnchorRenderer.mix(color, 0xFFFFFF, node.isEnergized() ? 0.58f : 0.22f);
        VertexConsumer surfaces = consumers.getBuffer(RenderLayer.getLightning());
        VertexConsumer lines = consumers.getBuffer(RenderLayer.getLines());
        matrices.push();
        matrices.translate(0.5, 0.02, 0.5);
        BlockState state = node.getCachedState();
        if (state.isOf(ModBlocks.LEY_CONDUIT)) {
            renderConduit(node, surfaces, matrices, time, color, bright);
        } else if (state.isOf(ModBlocks.RESONANCE_PYLON)) {
            renderPylon(node, surfaces, lines, matrices, time, color, bright);
        } else if (state.isOf(ModBlocks.STARLIGHT_COLLECTOR)) {
            renderCollector(node, surfaces, lines, matrices, time, color, bright);
        }
        matrices.pop();
    }

    private static void renderConduit(AstralNodeBlockEntity node, VertexConsumer surfaces, MatrixStack matrices,
                                      float time, int color, int bright) {
        float pulse = 0.5f + 0.5f * (float) Math.sin(time * 0.11f + node.getPos().asLong() * 0.01f);
        int alpha = node.isEnergized() ? (int) (105 + pulse * 70) : 42;
        RenderSurfaces.discXZ(surfaces, matrices.peek(), 0.08f, 0.25f, 8, 0x181322, 220);
        RenderSurfaces.ringXZ(surfaces, matrices.peek(), 0.095f, 0.12f, 0.27f, 12, color, alpha);
        for (Direction direction : Direction.Type.HORIZONTAL) {
            if (connects(node, direction)) {
                RenderSurfaces.cardinalRibbon(surfaces, matrices.peek(), 0.09f,
                        direction.getOffsetX(), direction.getOffsetZ(), 0.12f, 0.51f,
                        0.10f, color, alpha);
                RenderSurfaces.cardinalRibbon(surfaces, matrices.peek(), 0.104f,
                        direction.getOffsetX(), direction.getOffsetZ(), 0.18f, 0.51f,
                        0.025f, bright, node.isEnergized() ? 210 : 35);
            }
        }
        if (node.isEnergized()) {
            RenderSurfaces.diamond(surfaces, matrices.peek(), 0, 0.14f + pulse * 0.025f, 0,
                    0.055f, 0.10f, bright, 210);
        }
    }

    private static boolean connects(AstralNodeBlockEntity node, Direction direction) {
        BlockState neighbor = node.getWorld().getBlockState(node.getPos().offset(direction));
        return neighbor.isOf(ModBlocks.LEY_CONDUIT) || neighbor.isOf(ModBlocks.ASTRAL_ANCHOR)
                || neighbor.isOf(ModBlocks.RESONANCE_PYLON) || neighbor.isOf(ModBlocks.STARLIGHT_COLLECTOR);
    }

    private static void renderPylon(AstralNodeBlockEntity node, VertexConsumer surfaces, VertexConsumer lines,
                                    MatrixStack matrices, float time, int color, int bright) {
        float strength = node.isEnergized() ? node.getStrength() : 0.0f;
        float lift = 0.67f + (float) Math.sin(time * 0.055f + node.getPos().asLong()) * 0.035f;
        RenderSurfaces.discXZ(surfaces, matrices.peek(), 0.03f, 0.43f, 8, 0x17111F, 235);
        RenderSurfaces.ringXZ(surfaces, matrices.peek(), 0.055f, 0.24f, 0.44f, 12,
                color, node.isEnergized() ? 125 : 42);
        RenderSurfaces.diamond(surfaces, matrices.peek(), 0, lift, 0, 0.24f,
                0.62f, color, node.isEnergized() ? 180 : 75);
        RenderSurfaces.diamond(surfaces, matrices.peek(), 0, lift + 0.03f, 0, 0.11f,
                0.48f, bright, node.isEnergized() ? 205 : 50);
        for (int i = 0; i < 3; i++) {
            double angle = -time * 0.018 + i * Math.PI * 2.0 / 3.0;
            float radius = 0.38f + strength * 0.08f;
            float x = (float) Math.cos(angle) * radius;
            float z = (float) Math.sin(angle) * radius;
            RenderSurfaces.diamond(surfaces, matrices.peek(), x, 0.55f + i * 0.18f, z,
                    0.045f, 0.11f, i == 0 ? bright : color, node.isEnergized() ? 170 : 30);
        }
        if (node.isEnergized()) {
            matrices.push();
            matrices.translate(0, lift, 0);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(time * 0.58f));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(72.0f));
            RenderPrimitives.circle(lines, matrices.peek(), 0, 0, 0, 0.43f, 36, bright, 150);
            matrices.pop();
        }
    }

    private static void renderCollector(AstralNodeBlockEntity node, VertexConsumer surfaces, VertexConsumer lines,
                                        MatrixStack matrices, float time, int color, int bright) {
        float pulse = 0.5f + 0.5f * (float) Math.sin(time * 0.07f);
        int alpha = node.isEnergized() ? (int) (110 + pulse * 65) : 45;
        RenderSurfaces.discXZ(surfaces, matrices.peek(), 0.03f, 0.46f, 12, 0x17111F, 235);
        matrices.push();
        matrices.translate(0, 0.48, 0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(time * 0.17f));
        for (int i = 0; i < 12; i++) {
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * 30.0f));
            RenderSurfaces.doubleQuad(surfaces, matrices.peek(),
                    -0.075f, 0, 0.10f,
                    -0.19f, 0.14f, 0.54f,
                    0.19f, 0.14f, 0.54f,
                    0.075f, 0, 0.10f,
                    i % 2 == 0 ? color : bright, alpha);
            matrices.pop();
        }
        RenderSurfaces.ringXZ(surfaces, matrices.peek(), 0.145f, 0.46f, 0.56f, 24,
                bright, node.isEnergized() ? 175 : 50);
        matrices.pop();
        RenderSurfaces.diamond(surfaces, matrices.peek(), 0, 0.52f + pulse * 0.04f, 0,
                0.12f, 0.21f, bright, node.isEnergized() ? 225 : 62);
        if (node.isEnergized()) {
            RenderSurfaces.doubleQuad(surfaces, matrices.peek(),
                    -0.035f, 0.70f, 0, -0.012f, 1.48f, 0,
                    0.012f, 1.48f, 0, 0.035f, 0.70f, 0,
                    bright, 62);
            RenderSurfaces.doubleQuad(surfaces, matrices.peek(),
                    0, 0.70f, -0.035f, 0, 1.48f, -0.012f,
                    0, 1.48f, 0.012f, 0, 0.70f, 0.035f,
                    color, 54);
            float fall = 1.42f - (time * 0.022f % 0.75f);
            RenderSurfaces.diamond(surfaces, matrices.peek(), 0, fall, 0,
                    0.045f, 0.10f, 0xFFFFFF, 220);
            RenderPrimitives.circle(lines, matrices.peek(), 0, 0.64f, 0,
                    0.31f + pulse * 0.04f, 32, bright, 105);
        }
    }

    @Override
    public boolean rendersOutsideBoundingBox(AstralNodeBlockEntity blockEntity) {
        return true;
    }
}
