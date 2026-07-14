package net.bored.client.render;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

final class RenderPrimitives {
    private RenderPrimitives() {
    }

    static void line(VertexConsumer consumer, MatrixStack.Entry entry,
                     float x1, float y1, float z1, float x2, float y2, float z2,
                     int color, int alpha) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float dz = z2 - z1;
        float length = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length < 0.0001f) {
            return;
        }
        dx /= length;
        dy /= length;
        dz /= length;
        int red = color >> 16 & 255;
        int green = color >> 8 & 255;
        int blue = color & 255;
        consumer.vertex(entry.getPositionMatrix(), x1, y1, z1).color(red, green, blue, alpha)
                .normal(entry.getNormalMatrix(), dx, dy, dz).next();
        consumer.vertex(entry.getPositionMatrix(), x2, y2, z2).color(red, green, blue, alpha)
                .normal(entry.getNormalMatrix(), dx, dy, dz).next();
    }

    static void circle(VertexConsumer consumer, MatrixStack.Entry entry, float centerX, float y, float centerZ,
                       float radius, int segments, int color, int alpha) {
        arc(consumer, entry, centerX, y, centerZ, radius, segments, 0.0f, 1.0f, color, alpha);
    }

    static void arc(VertexConsumer consumer, MatrixStack.Entry entry, float centerX, float y, float centerZ,
                    float radius, int segments, float start, float length, int color, int alpha) {
        int drawn = Math.max(1, (int) Math.ceil(segments * Math.max(0.0f, Math.min(1.0f, length))));
        for (int i = 0; i < drawn; i++) {
            double a = Math.PI * 2.0 * (start + i / (double) segments);
            double b = Math.PI * 2.0 * (start + (i + 1) / (double) segments);
            line(consumer, entry,
                    centerX + (float) Math.cos(a) * radius, y, centerZ + (float) Math.sin(a) * radius,
                    centerX + (float) Math.cos(b) * radius, y, centerZ + (float) Math.sin(b) * radius,
                    color, alpha);
        }
    }

    static void box(VertexConsumer consumer, MatrixStack.Entry entry,
                    float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
                    int color, int alpha) {
        float[][] points = {
                {minX, minY, minZ}, {maxX, minY, minZ}, {maxX, minY, maxZ}, {minX, minY, maxZ},
                {minX, maxY, minZ}, {maxX, maxY, minZ}, {maxX, maxY, maxZ}, {minX, maxY, maxZ}
        };
        int[][] edges = {
                {0, 1}, {1, 2}, {2, 3}, {3, 0}, {4, 5}, {5, 6}, {6, 7}, {7, 4},
                {0, 4}, {1, 5}, {2, 6}, {3, 7}
        };
        for (int[] edge : edges) {
            float[] a = points[edge[0]];
            float[] b = points[edge[1]];
            line(consumer, entry, a[0], a[1], a[2], b[0], b[1], b[2], color, alpha);
        }
    }

    static void diamond(VertexConsumer consumer, MatrixStack.Entry entry, float x, float y, float z,
                        float radius, float height, int color, int alpha) {
        float[][] ring = {{x + radius, y, z}, {x, y, z + radius}, {x - radius, y, z}, {x, y, z - radius}};
        for (int i = 0; i < ring.length; i++) {
            float[] a = ring[i];
            float[] b = ring[(i + 1) % ring.length];
            line(consumer, entry, a[0], a[1], a[2], b[0], b[1], b[2], color, alpha);
            line(consumer, entry, a[0], a[1], a[2], x, y + height, z, color, alpha);
            line(consumer, entry, a[0], a[1], a[2], x, y - height, z, color, alpha);
        }
    }

    static void star(VertexConsumer consumer, MatrixStack.Entry entry, float x, float y, float z,
                     float size, int color, int alpha) {
        line(consumer, entry, x - size, y, z, x + size, y, z, color, alpha);
        line(consumer, entry, x, y - size, z, x, y + size, z, color, alpha);
        line(consumer, entry, x, y, z - size, x, y, z + size, color, alpha);
    }

    static void bezier(VertexConsumer consumer, MatrixStack.Entry entry,
                       float x0, float y0, float z0, float x1, float y1, float z1,
                       float x2, float y2, float z2, float x3, float y3, float z3,
                       int segments, int color, int alpha) {
        float previousX = x0;
        float previousY = y0;
        float previousZ = z0;
        for (int i = 1; i <= segments; i++) {
            float t = i / (float) segments;
            float inverse = 1.0f - t;
            float x = inverse * inverse * inverse * x0 + 3 * inverse * inverse * t * x1
                    + 3 * inverse * t * t * x2 + t * t * t * x3;
            float y = inverse * inverse * inverse * y0 + 3 * inverse * inverse * t * y1
                    + 3 * inverse * t * t * y2 + t * t * t * y3;
            float z = inverse * inverse * inverse * z0 + 3 * inverse * inverse * t * z1
                    + 3 * inverse * t * t * z2 + t * t * t * z3;
            line(consumer, entry, previousX, previousY, previousZ, x, y, z, color, alpha);
            previousX = x;
            previousY = y;
            previousZ = z;
        }
    }
}
