package net.bored.client.render;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

final class RenderSurfaces {
    private RenderSurfaces() {
    }

    static void quad(VertexConsumer consumer, MatrixStack.Entry entry,
                     float ax, float ay, float az, float bx, float by, float bz,
                     float cx, float cy, float cz, float dx, float dy, float dz,
                     int color, int alpha) {
        vertex(consumer, entry, ax, ay, az, color, alpha);
        vertex(consumer, entry, bx, by, bz, color, alpha);
        vertex(consumer, entry, cx, cy, cz, color, alpha);
        vertex(consumer, entry, dx, dy, dz, color, alpha);
    }

    static void doubleQuad(VertexConsumer consumer, MatrixStack.Entry entry,
                           float ax, float ay, float az, float bx, float by, float bz,
                           float cx, float cy, float cz, float dx, float dy, float dz,
                           int color, int alpha) {
        quad(consumer, entry, ax, ay, az, bx, by, bz, cx, cy, cz, dx, dy, dz, color, alpha);
        quad(consumer, entry, dx, dy, dz, cx, cy, cz, bx, by, bz, ax, ay, az, color, alpha);
    }

    static void discXZ(VertexConsumer consumer, MatrixStack.Entry entry, float y, float radius,
                       int segments, int color, int alpha) {
        for (int i = 0; i < segments; i++) {
            double a = Math.PI * 2.0 * i / segments;
            double b = Math.PI * 2.0 * (i + 1) / segments;
            doubleQuad(consumer, entry, 0, y, 0,
                    (float) Math.cos(a) * radius, y, (float) Math.sin(a) * radius,
                    (float) Math.cos(b) * radius, y, (float) Math.sin(b) * radius,
                    0, y, 0, color, alpha);
        }
    }

    static void ringXZ(VertexConsumer consumer, MatrixStack.Entry entry, float y, float inner, float outer,
                       int segments, int color, int alpha) {
        for (int i = 0; i < segments; i++) {
            double a = Math.PI * 2.0 * i / segments;
            double b = Math.PI * 2.0 * (i + 1) / segments;
            doubleQuad(consumer, entry,
                    (float) Math.cos(a) * inner, y, (float) Math.sin(a) * inner,
                    (float) Math.cos(a) * outer, y, (float) Math.sin(a) * outer,
                    (float) Math.cos(b) * outer, y, (float) Math.sin(b) * outer,
                    (float) Math.cos(b) * inner, y, (float) Math.sin(b) * inner,
                    color, alpha);
        }
    }

    static void discXY(VertexConsumer consumer, MatrixStack.Entry entry, float z, float radius,
                       int segments, int color, int alpha) {
        for (int i = 0; i < segments; i++) {
            double a = Math.PI * 2.0 * i / segments;
            double b = Math.PI * 2.0 * (i + 1) / segments;
            doubleQuad(consumer, entry, 0, 0, z,
                    (float) Math.cos(a) * radius, (float) Math.sin(a) * radius, z,
                    (float) Math.cos(b) * radius, (float) Math.sin(b) * radius, z,
                    0, 0, z, color, alpha);
        }
    }

    static void ringXY(VertexConsumer consumer, MatrixStack.Entry entry, float z, float inner, float outer,
                       int segments, int color, int alpha) {
        for (int i = 0; i < segments; i++) {
            double a = Math.PI * 2.0 * i / segments;
            double b = Math.PI * 2.0 * (i + 1) / segments;
            doubleQuad(consumer, entry,
                    (float) Math.cos(a) * inner, (float) Math.sin(a) * inner, z,
                    (float) Math.cos(a) * outer, (float) Math.sin(a) * outer, z,
                    (float) Math.cos(b) * outer, (float) Math.sin(b) * outer, z,
                    (float) Math.cos(b) * inner, (float) Math.sin(b) * inner, z,
                    color, alpha);
        }
    }

    static void petalXY(VertexConsumer consumer, MatrixStack.Entry entry, float inner, float outer,
                        float width, float twist, int color, int alpha) {
        doubleQuad(consumer, entry,
                -width * 0.35f, inner, 0,
                -width, outer * 0.72f, twist,
                0, outer, 0,
                width, outer * 0.72f, -twist,
                color, alpha);
    }

    static void diamond(VertexConsumer consumer, MatrixStack.Entry entry, float x, float y, float z,
                        float radius, float height, int color, int alpha) {
        float[][] ring = {{x + radius, y, z}, {x, y, z + radius}, {x - radius, y, z}, {x, y, z - radius}};
        for (int i = 0; i < ring.length; i++) {
            float[] a = ring[i];
            float[] b = ring[(i + 1) % ring.length];
            doubleQuad(consumer, entry, x, y + height, z,
                    a[0], a[1], a[2], b[0], b[1], b[2], x, y + height, z, color, alpha);
            doubleQuad(consumer, entry, x, y - height, z,
                    b[0], b[1], b[2], a[0], a[1], a[2], x, y - height, z, color, alpha);
        }
    }

    static void cardinalRibbon(VertexConsumer consumer, MatrixStack.Entry entry, float y, int dx, int dz,
                               float start, float end, float width, int color, int alpha) {
        float sideX = dz * width;
        float sideZ = -dx * width;
        doubleQuad(consumer, entry,
                dx * start + sideX, y, dz * start + sideZ,
                dx * end + sideX, y, dz * end + sideZ,
                dx * end - sideX, y, dz * end - sideZ,
                dx * start - sideX, y, dz * start - sideZ,
                color, alpha);
    }

    private static void vertex(VertexConsumer consumer, MatrixStack.Entry entry,
                               float x, float y, float z, int color, int alpha) {
        consumer.vertex(entry.getPositionMatrix(), x, y, z)
                .color(color >> 16 & 255, color >> 8 & 255, color & 255, alpha).next();
    }
}
