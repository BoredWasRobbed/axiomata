package net.bored.client.render;

import net.bored.block.entity.AstralAnchorBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

import java.util.Random;

public final class AstralAnchorRenderer implements BlockEntityRenderer<AstralAnchorBlockEntity> {
    public AstralAnchorRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(AstralAnchorBlockEntity anchor, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider consumers, int light, int overlay) {
        float time = (anchor.getWorld() == null ? 0 : anchor.getWorld().getTime()) + tickDelta;
        float activity = anchor.getActivity(tickDelta);
        float fill = anchor.getFillRatio();
        int color = anchor.getNetworkColor();
        int bright = mix(color, 0xFFFFFF, 0.46f);
        VertexConsumer lines = consumers.getBuffer(RenderLayer.getLines());

        matrices.push();
        matrices.translate(0.5, 0.03, 0.5);

        renderBase(lines, matrices, time, fill, color, bright);
        renderAperture(lines, matrices, time, activity, fill, color, bright);
        renderCapacityCells(lines, matrices, time, anchor.getPageCount(), color, bright);
        renderConstellation(lines, matrices, time, anchor.getVisualSeed(), color, bright);
        if (activity > 0.0f) {
            renderTransfers(lines, matrices, time, activity, color, bright);
        }

        matrices.pop();
    }

    private static void renderBase(VertexConsumer lines, MatrixStack matrices, float time, float fill,
                                   int color, int bright) {
        RenderPrimitives.circle(lines, matrices.peek(), 0, 0.02f, 0, 0.62f, 8, 0x3C2B62, 240);
        RenderPrimitives.circle(lines, matrices.peek(), 0, 0.11f, 0, 0.5f, 32, color, 190);
        RenderPrimitives.circle(lines, matrices.peek(), 0, 0.15f, 0, 0.34f, 24, bright, 150);
        RenderPrimitives.arc(lines, matrices.peek(), 0, 0.17f, 0, 0.55f, 64,
                (time * 0.002f) % 1.0f, Math.max(0.025f, fill), bright, 245);
        RenderPrimitives.box(lines, matrices.peek(), -0.38f, 0.0f, -0.38f,
                0.38f, 0.24f, 0.38f, 0x4C3A71, 210);
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4.0;
            RenderPrimitives.line(lines, matrices.peek(),
                    (float) Math.cos(angle) * 0.34f, 0.15f, (float) Math.sin(angle) * 0.34f,
                    (float) Math.cos(angle) * 0.62f, 0.02f, (float) Math.sin(angle) * 0.62f,
                    color, 150);
        }
    }

    private static void renderAperture(VertexConsumer lines, MatrixStack matrices, float time, float activity,
                                       float fill, int color, int bright) {
        matrices.push();
        matrices.translate(0, 0.88, 0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(time * 0.22f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
        float breath = 1.0f + (float) Math.sin(time * 0.055f) * 0.035f + activity * 0.08f;
        RenderPrimitives.circle(lines, matrices.peek(), 0, 0, 0, 0.62f * breath, 72, bright, 235);
        RenderPrimitives.circle(lines, matrices.peek(), 0, 0.012f, 0, 0.51f * breath, 64, color, 210);
        RenderPrimitives.circle(lines, matrices.peek(), 0, -0.012f, 0, 0.39f * breath, 48, bright, 145);
        RenderPrimitives.arc(lines, matrices.peek(), 0, 0.025f, 0, 0.565f * breath, 72,
                (-time * 0.004f) % 1.0f, Math.max(0.04f, fill), 0xFFFFFF, 255);
        for (int i = 0; i < 12; i++) {
            double angle = i * Math.PI / 6.0 + time * 0.006;
            float inner = 0.39f * breath;
            float outer = 0.51f * breath;
            RenderPrimitives.line(lines, matrices.peek(),
                    (float) Math.cos(angle) * inner, 0, (float) Math.sin(angle) * inner,
                    (float) Math.cos(angle) * outer, 0, (float) Math.sin(angle) * outer,
                    color, 155);
        }
        matrices.pop();

        matrices.push();
        matrices.translate(0, 0.88, 0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-time * 0.7f));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(64.0f));
        RenderPrimitives.circle(lines, matrices.peek(), 0, 0, 0, 0.72f + activity * 0.08f, 64,
                color, 175);
        matrices.pop();

        matrices.push();
        matrices.translate(0, 0.88, 0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(time * 0.95f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(58.0f));
        RenderPrimitives.circle(lines, matrices.peek(), 0, 0, 0, 0.77f + activity * 0.05f, 64,
                bright, 140);
        matrices.pop();
    }

    private static void renderCapacityCells(VertexConsumer lines, MatrixStack matrices, float time, int pages,
                                            int color, int bright) {
        for (int i = 0; i < pages; i++) {
            double angle = time * (0.018 + i * 0.0018) + i * Math.PI * 2.0 / pages;
            float radius = 0.92f + i * 0.055f;
            float x = (float) Math.cos(angle) * radius;
            float z = (float) Math.sin(angle) * radius;
            float y = 0.77f + (float) Math.sin(angle * 1.7) * 0.22f;
            RenderPrimitives.diamond(lines, matrices.peek(), x, y, z, 0.09f, 0.16f,
                    i == pages - 1 ? bright : color, 230);
            RenderPrimitives.line(lines, matrices.peek(), x, y, z, 0, 0.88f, 0, color, 70);
        }
    }

    private static void renderConstellation(VertexConsumer lines, MatrixStack matrices, float time, long seed,
                                            int color, int bright) {
        Random random = new Random(seed);
        float[][] stars = new float[14][3];
        for (int i = 0; i < stars.length; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            float radius = 0.78f + random.nextFloat() * 0.68f;
            stars[i][0] = (float) Math.cos(angle) * radius;
            stars[i][1] = 0.45f + random.nextFloat() * 1.25f;
            stars[i][2] = (float) Math.sin(angle) * radius;
            float pulse = 0.018f + 0.012f * (0.5f + 0.5f * (float) Math.sin(time * 0.08f + i));
            RenderPrimitives.star(lines, matrices.peek(), stars[i][0], stars[i][1], stars[i][2],
                    pulse, i % 3 == 0 ? bright : color, 150 + i % 4 * 20);
        }
        for (int i = 0; i < stars.length; i++) {
            int next = (i * 5 + 3) % stars.length;
            if (i == next) {
                continue;
            }
            RenderPrimitives.line(lines, matrices.peek(), stars[i][0], stars[i][1], stars[i][2],
                    stars[next][0], stars[next][1], stars[next][2], color, 46);
        }
    }

    private static void renderTransfers(VertexConsumer lines, MatrixStack matrices, float time, float activity,
                                        int color, int bright) {
        for (int i = 0; i < 6; i++) {
            double angle = i * Math.PI / 3.0;
            float startX = (float) Math.cos(angle) * 1.25f;
            float startZ = (float) Math.sin(angle) * 1.25f;
            RenderPrimitives.bezier(lines, matrices.peek(), startX, 0.25f, startZ,
                    startX * 0.75f, 1.45f, startZ * 0.75f,
                    -startZ * 0.25f, 1.3f, startX * 0.25f,
                    0, 0.88f, 0, 18, color, (int) (40 + activity * 80));
            float t = (time * 0.03f + i * 0.17f) % 1.0f;
            float inverse = 1.0f - t;
            float x = inverse * inverse * inverse * startX
                    + 3 * inverse * inverse * t * startX * 0.75f
                    + 3 * inverse * t * t * (-startZ * 0.25f);
            float y = inverse * inverse * inverse * 0.25f
                    + 3 * inverse * inverse * t * 1.45f
                    + 3 * inverse * t * t * 1.3f + t * t * t * 0.88f;
            float z = inverse * inverse * inverse * startZ
                    + 3 * inverse * inverse * t * startZ * 0.75f
                    + 3 * inverse * t * t * (startX * 0.25f);
            RenderPrimitives.star(lines, matrices.peek(), x, y, z, 0.045f + activity * 0.02f,
                    bright, 255);
        }
    }

    private static int mix(int first, int second, float amount) {
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
