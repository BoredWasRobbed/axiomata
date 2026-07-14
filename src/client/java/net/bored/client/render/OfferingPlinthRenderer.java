package net.bored.client.render;

import net.bored.block.entity.OfferingPlinthBlockEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

public final class OfferingPlinthRenderer implements BlockEntityRenderer<OfferingPlinthBlockEntity> {
    private final ItemRenderer itemRenderer;

    public OfferingPlinthRenderer(BlockEntityRendererFactory.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(OfferingPlinthBlockEntity plinth, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider consumers, int light, int overlay) {
        float time = (plinth.getWorld() == null ? 0 : plinth.getWorld().getTime()) + tickDelta;
        boolean occupied = !plinth.isEmpty();
        int color = occupied ? 0xD8A6FF : 0x665080;
        VertexConsumer lines = consumers.getBuffer(RenderLayer.getLines());

        matrices.push();
        matrices.translate(0.5, 0, 0.5);
        RenderPrimitives.box(lines, matrices.peek(), -0.38f, 0.02f, -0.38f, 0.38f, 0.16f, 0.38f,
                color, 210);
        RenderPrimitives.box(lines, matrices.peek(), -0.24f, 0.16f, -0.24f, 0.24f, 0.62f, 0.24f,
                color, 185);
        RenderPrimitives.box(lines, matrices.peek(), -0.42f, 0.62f, -0.42f, 0.42f, 0.76f, 0.42f,
                color, 225);
        RenderPrimitives.circle(lines, matrices.peek(), 0, 0.79f, 0, 0.34f, 36, color, 210);
        if (occupied) {
            float pulse = 0.5f + 0.5f * (float) Math.sin(time * 0.14f);
            RenderPrimitives.circle(lines, matrices.peek(), 0, 0.88f + pulse * 0.04f, 0,
                    0.25f + pulse * 0.025f, 32, 0xF2D7FF, 190);
        }
        matrices.pop();

        ItemStack stack = plinth.getStack(0);
        if (!stack.isEmpty()) {
            matrices.push();
            matrices.translate(0.5, 1.03 + Math.sin(time * 0.12f) * 0.07, 0.5);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(time * 2.0f));
            matrices.scale(0.58f, 0.58f, 0.58f);
            itemRenderer.renderItem(stack, ModelTransformationMode.GROUND, light, OverlayTexture.DEFAULT_UV,
                    matrices, consumers, plinth.getWorld(), (int) plinth.getPos().asLong());
            matrices.pop();
        }
    }
}
