package net.bored.client.render;

import net.bored.block.entity.RuneMarkBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public final class RuneMarkRenderer implements BlockEntityRenderer<RuneMarkBlockEntity> {
    public RuneMarkRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(RuneMarkBlockEntity rune, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider consumers, int light, int overlay) {
        float time = (rune.getWorld() == null ? 0 : rune.getWorld().getTime()) + tickDelta;
        float pulse = rune.isCharged() ? 0.5f + 0.5f * (float) Math.sin(time * 0.25f) : 0.0f;
        int color = rune.isCharged() ? 0xEAC7FF : 0x9266D9;
        int alpha = rune.isCharged() ? 220 + (int) (35 * pulse) : 155;
        VertexConsumer lines = consumers.getBuffer(RenderLayer.getLines());

        matrices.push();
        matrices.translate(0.5, 0.025 + pulse * 0.018, 0.5);
        RenderPrimitives.circle(lines, matrices.peek(), 0, 0, 0, 0.43f + pulse * 0.025f, 40, color, alpha);
        RenderPrimitives.circle(lines, matrices.peek(), 0, 0, 0, 0.34f, 32, color, alpha - 25);
        RenderPrimitives.glyph(lines, matrices.peek(), rune.getRune(), 0, 0.004f, 0,
                0.52f + pulse * 0.04f, color, alpha);
        matrices.pop();
    }
}
