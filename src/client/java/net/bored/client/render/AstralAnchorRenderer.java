package net.bored.client.render;

import net.bored.block.entity.AstralAnchorBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

public final class AstralAnchorRenderer implements BlockEntityRenderer<AstralAnchorBlockEntity> {
    public AstralAnchorRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(AstralAnchorBlockEntity anchor, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider consumers, int light, int overlay) {
        float time = (anchor.getWorld() == null ? 0 : anchor.getWorld().getTime()) + tickDelta;
        int color = anchor.getNetworkColor();
        int bright = mix(color, 0xFFFFFF, 0.55f);
        int dormant = mix(color, 0x241D35, 0.72f);
        VertexConsumer surfaces = consumers.getBuffer(RenderLayer.getLightning());
        VertexConsumer lines = consumers.getBuffer(RenderLayer.getLines());

        matrices.push();
        matrices.translate(0.5, 0.03, 0.5);
        renderPedestal(surfaces, lines, matrices, time, anchor, color, bright, dormant);
        if (anchor.isOperational()) {
            renderOpenIris(surfaces, lines, matrices, time, anchor, color, bright);
            renderCapacityCells(surfaces, matrices, time, anchor.getPageCount(), color, bright);
            renderMatterFlares(surfaces, matrices, time, anchor.getActivity(tickDelta), bright);
        } else {
            renderDormantIris(surfaces, matrices, time, anchor.isFrameComplete(), dormant, color);
        }
        matrices.pop();
    }

    private static void renderPedestal(VertexConsumer surfaces, VertexConsumer lines, MatrixStack matrices,
                                       float time, AstralAnchorBlockEntity anchor, int color, int bright,
                                       int dormant) {
        int activeColor = anchor.isOperational() ? color : dormant;
        RenderSurfaces.discXZ(surfaces, matrices.peek(), 0.025f, 0.55f, 12, 0x171020, 235);
        RenderSurfaces.ringXZ(surfaces, matrices.peek(), 0.045f, 0.34f, 0.54f, 16, activeColor, 125);
        RenderSurfaces.ringXZ(surfaces, matrices.peek(), 0.065f, 0.47f,
                0.47f + anchor.getEnergyRatio() * 0.07f, 24, bright, anchor.isOperational() ? 185 : 35);
        for (int i = 0; i < 4; i++) {
            double angle = i * Math.PI / 2.0;
            int dx = (int) Math.round(Math.cos(angle));
            int dz = (int) Math.round(Math.sin(angle));
            RenderSurfaces.cardinalRibbon(surfaces, matrices.peek(), 0.075f, dx, dz,
                    0.30f, 0.72f, 0.055f, activeColor, anchor.isOperational() ? 175 : 52);
        }
        if (anchor.isOperational()) {
            RenderPrimitives.arc(lines, matrices.peek(), 0, 0.085f, 0, 0.58f, 48,
                    (time * 0.002f) % 1.0f, Math.max(0.035f, anchor.getFillRatio()), bright, 235);
        }
    }

    private static void renderOpenIris(VertexConsumer surfaces, VertexConsumer lines, MatrixStack matrices,
                                       float time, AstralAnchorBlockEntity anchor, int color, int bright) {
        float breath = 1.0f + (float) Math.sin(time * 0.045f) * 0.035f;
        matrices.push();
        matrices.translate(0, 0.91, 0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(time * 0.28f));
        RenderSurfaces.discXY(surfaces, matrices.peek(), 0, 0.39f * breath, 24,
                mix(color, 0x090617, 0.48f), 138);
        RenderSurfaces.ringXY(surfaces, matrices.peek(), 0.006f, 0.40f * breath, 0.55f * breath,
                32, color, 155);
        RenderSurfaces.ringXY(surfaces, matrices.peek(), -0.006f, 0.515f * breath, 0.57f * breath,
                32, bright, 210);
        for (int i = 0; i < 7; i++) {
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i * (360.0f / 7.0f) - time * 0.16f));
            float sweep = 0.055f + 0.018f * (float) Math.sin(time * 0.06f + i);
            RenderSurfaces.petalXY(surfaces, matrices.peek(), 0.12f, 0.50f, 0.105f, sweep,
                    i % 2 == 0 ? bright : color, 118);
            matrices.pop();
        }
        RenderSurfaces.diamond(surfaces, matrices.peek(), 0, 0, 0.025f, 0.105f,
                0.18f + anchor.getActivity(0) * 0.06f, 0xFFFFFF, 230);
        matrices.pop();

        matrices.push();
        matrices.translate(0, 0.91, 0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-time * 0.48f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(62.0f));
        RenderPrimitives.circle(lines, matrices.peek(), 0, 0, 0, 0.69f, 56, color, 125);
        matrices.pop();
    }

    private static void renderDormantIris(VertexConsumer surfaces, MatrixStack matrices, float time,
                                          boolean frameComplete, int dormant, int color) {
        matrices.push();
        matrices.translate(0, 0.72, 0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(time * 0.08f));
        int alpha = frameComplete ? 95 : 48;
        RenderSurfaces.diamond(surfaces, matrices.peek(), 0, 0, 0, 0.22f, 0.44f,
                frameComplete ? color : dormant, alpha);
        for (int i = 0; i < 4; i++) {
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i * 90.0f + 45.0f));
            RenderSurfaces.petalXY(surfaces, matrices.peek(), 0.08f, 0.38f, 0.07f, 0.02f,
                    dormant, alpha);
            matrices.pop();
        }
        matrices.pop();
    }

    private static void renderCapacityCells(VertexConsumer surfaces, MatrixStack matrices, float time,
                                            int pages, int color, int bright) {
        for (int i = 0; i < pages; i++) {
            double angle = time * 0.014 + i * Math.PI * 2.0 / pages;
            float radius = 0.76f;
            float x = (float) Math.cos(angle) * radius;
            float z = (float) Math.sin(angle) * radius;
            float y = 0.73f + (float) Math.sin(angle * 2.0) * 0.12f;
            RenderSurfaces.diamond(surfaces, matrices.peek(), x, y, z, 0.065f, 0.13f,
                    i == pages - 1 ? bright : color, 185);
        }
    }

    private static void renderMatterFlares(VertexConsumer surfaces, MatrixStack matrices, float time,
                                           float activity, int bright) {
        if (activity <= 0.0f) {
            return;
        }
        for (int i = 0; i < 3; i++) {
            float progress = (time * 0.025f + i / 3.0f) % 1.0f;
            double angle = i * Math.PI * 2.0 / 3.0 + time * 0.018;
            float radius = 1.15f * (1.0f - progress);
            float x = (float) Math.cos(angle) * radius;
            float z = (float) Math.sin(angle) * radius;
            float y = 0.28f + progress * 0.62f + (float) Math.sin(progress * Math.PI) * 0.42f;
            RenderSurfaces.diamond(surfaces, matrices.peek(), x, y, z,
                    0.025f + activity * 0.025f, 0.07f + activity * 0.06f, bright,
                    (int) (90 + activity * 150));
        }
    }

    static int mix(int first, int second, float amount) {
        int red = (int) ((first >> 16 & 255) * (1.0f - amount) + (second >> 16 & 255) * amount);
        int green = (int) ((first >> 8 & 255) * (1.0f - amount) + (second >> 8 & 255) * amount);
        int blue = (int) ((first & 255) * (1.0f - amount) + (second & 255) * amount);
        return red << 16 | green << 8 | blue;
    }

    @Override
    public boolean rendersOutsideBoundingBox(AstralAnchorBlockEntity blockEntity) {
        return true;
    }
}
