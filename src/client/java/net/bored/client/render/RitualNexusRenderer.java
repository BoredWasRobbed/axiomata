package net.bored.client.render;

import net.bored.block.entity.RitualNexusBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

public final class RitualNexusRenderer implements BlockEntityRenderer<RitualNexusBlockEntity> {
    public RitualNexusRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(RitualNexusBlockEntity nexus, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider consumers, int light, int overlay) {
        float time = (nexus.getWorld() == null ? 0 : nexus.getWorld().getTime()) + tickDelta;
        float progress = nexus.getVisualProgress(tickDelta);
        int color = nexus.getVisualColor();
        int alpha = nexus.isActive() ? 235 : 175;
        VertexConsumer lines = consumers.getBuffer(RenderLayer.getLines());

        matrices.push();
        matrices.translate(0.5, 0.02, 0.5);

        RenderPrimitives.box(lines, matrices.peek(), -0.42f, 0.0f, -0.42f, 0.42f, 0.24f, 0.42f,
                0x3D2A66, 220);
        RenderPrimitives.circle(lines, matrices.peek(), 0, 0.04f, 0, 0.58f, 48, color, alpha);
        RenderPrimitives.circle(lines, matrices.peek(), 0, 0.08f, 0, 0.38f, 40, color, alpha);
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4.0;
            RenderPrimitives.line(lines, matrices.peek(),
                    (float) Math.cos(angle) * 0.38f, 0.08f, (float) Math.sin(angle) * 0.38f,
                    (float) Math.cos(angle) * 0.58f, 0.04f, (float) Math.sin(angle) * 0.58f,
                    color, alpha);
        }

        matrices.push();
        matrices.translate(0, 0.65 + progress * 0.45, 0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(time * 1.4f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(62.0f));
        RenderPrimitives.circle(lines, matrices.peek(), 0, 0, 0, 0.52f + progress * 0.18f, 56, color, alpha);
        matrices.pop();

        matrices.push();
        matrices.translate(0, 0.72 + progress * 0.55, 0);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(time * 1.9f));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(67.0f));
        RenderPrimitives.circle(lines, matrices.peek(), 0, 0, 0, 0.4f + progress * 0.24f, 48, color, alpha);
        matrices.pop();

        matrices.push();
        matrices.translate(0, 0.3 + progress * 0.75, 0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-time * 0.8f));
        for (int rune = 0; rune < 8; rune++) {
            double angle = rune * Math.PI / 4.0;
            RenderPrimitives.glyph(lines, matrices.peek(), rune,
                    (float) Math.cos(angle) * 0.86f, 0,
                    (float) Math.sin(angle) * 0.86f, 0.2f, color, alpha);
        }
        matrices.pop();

        if (nexus.isActive()) {
            for (BlockPosVector offset : BlockPosVector.PLINTHS) {
                RenderPrimitives.line(lines, matrices.peek(), 0, 0.22f, 0,
                        offset.x, 0.16f + progress * 0.3f, offset.z, color, 140);
            }
            for (int helix = 0; helix < 2; helix++) {
                float phase = helix * (float) Math.PI;
                for (int i = 0; i < 48; i++) {
                    float a = i / 48.0f;
                    float b = (i + 1) / 48.0f;
                    float angleA = a * 12.0f + time * 0.08f + phase;
                    float angleB = b * 12.0f + time * 0.08f + phase;
                    RenderPrimitives.line(lines, matrices.peek(),
                            (float) Math.cos(angleA) * 0.26f, 0.25f + a * (2.1f + progress),
                            (float) Math.sin(angleA) * 0.26f,
                            (float) Math.cos(angleB) * 0.26f, 0.25f + b * (2.1f + progress),
                            (float) Math.sin(angleB) * 0.26f, color, 205);
                }
            }
        }

        matrices.pop();
    }

    @Override
    public boolean rendersOutsideBoundingBox(RitualNexusBlockEntity blockEntity) {
        return true;
    }

    private record BlockPosVector(float x, float z) {
        private static final BlockPosVector[] PLINTHS = {
                new BlockPosVector(0, -3), new BlockPosVector(3, 0),
                new BlockPosVector(0, 3), new BlockPosVector(-3, 0)
        };
    }
}
