package net.bored.client.render;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

final class RenderPrimitives {
    private static final float[][][] GLYPHS = {
            {{-0.5f, 0.42f, 0.0f, -0.5f}, {0.0f, -0.5f, 0.5f, 0.42f}, {0.5f, 0.42f, -0.5f, 0.42f}},
            {{0.0f, -0.55f, 0.0f, 0.55f}, {-0.45f, 0.1f, 0.0f, -0.35f}, {0.0f, -0.35f, 0.45f, 0.1f}, {0.45f, 0.1f, 0.0f, 0.42f}, {0.0f, 0.42f, -0.45f, 0.1f}},
            {{-0.45f, -0.48f, 0.45f, 0.48f}, {0.45f, -0.48f, -0.45f, 0.48f}, {-0.45f, -0.48f, 0.45f, -0.48f}, {-0.45f, 0.48f, 0.45f, 0.48f}},
            {{-0.5f, 0.3f, 0.0f, -0.5f}, {0.0f, -0.5f, 0.5f, 0.3f}, {0.5f, 0.3f, 0.1f, 0.0f}, {0.1f, 0.0f, -0.18f, 0.23f}, {-0.18f, 0.23f, 0.0f, 0.42f}},
            {{-0.55f, 0.0f, 0.55f, 0.0f}, {0.0f, -0.55f, 0.0f, 0.55f}, {-0.4f, -0.4f, 0.4f, 0.4f}, {0.4f, -0.4f, -0.4f, 0.4f}},
            {{-0.5f, 0.0f, -0.25f, -0.43f}, {-0.25f, -0.43f, 0.25f, -0.43f}, {0.25f, -0.43f, 0.5f, 0.0f}, {0.5f, 0.0f, 0.25f, 0.43f}, {0.25f, 0.43f, -0.25f, 0.43f}, {-0.25f, 0.43f, -0.5f, 0.0f}},
            {{-0.55f, 0.0f, -0.25f, -0.32f}, {-0.25f, -0.32f, 0.25f, -0.32f}, {0.25f, -0.32f, 0.55f, 0.0f}, {0.55f, 0.0f, 0.25f, 0.32f}, {0.25f, 0.32f, -0.25f, 0.32f}, {-0.25f, 0.32f, -0.55f, 0.0f}, {0.0f, -0.2f, 0.0f, 0.2f}},
            {{0.0f, -0.55f, 0.0f, 0.18f}, {0.0f, 0.18f, -0.45f, 0.5f}, {0.0f, 0.18f, 0.45f, 0.5f}, {-0.3f, -0.12f, 0.3f, -0.12f}}
    };

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
        for (int i = 0; i < segments; i++) {
            double a = Math.PI * 2.0 * i / segments;
            double b = Math.PI * 2.0 * (i + 1) / segments;
            line(consumer, entry,
                    centerX + (float) Math.cos(a) * radius, y, centerZ + (float) Math.sin(a) * radius,
                    centerX + (float) Math.cos(b) * radius, y, centerZ + (float) Math.sin(b) * radius,
                    color, alpha);
        }
    }

    static void glyph(VertexConsumer consumer, MatrixStack.Entry entry, int rune, float centerX, float y,
                      float centerZ, float size, int color, int alpha) {
        for (float[] segment : GLYPHS[rune & 7]) {
            line(consumer, entry,
                    centerX + segment[0] * size, y, centerZ + segment[1] * size,
                    centerX + segment[2] * size, y, centerZ + segment[3] * size,
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
}
