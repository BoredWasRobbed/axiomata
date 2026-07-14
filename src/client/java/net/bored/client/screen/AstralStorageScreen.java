package net.bored.client.screen;

import net.bored.screen.AstralStorageScreenHandler;
import net.bored.storage.AstralColors;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public final class AstralStorageScreen extends HandledScreen<AstralStorageScreenHandler> {
    private ButtonWidget previousButton;
    private ButtonWidget nextButton;

    public AstralStorageScreen(AstralStorageScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundWidth = 196;
        backgroundHeight = 244;
        playerInventoryTitleY = 145;
    }

    @Override
    protected void init() {
        super.init();
        previousButton = addDrawableChild(ButtonWidget.builder(Text.literal("‹"), button -> changePage(-1))
                .dimensions(x + 8, y + 7, 18, 16).build());
        nextButton = addDrawableChild(ButtonWidget.builder(Text.literal("›"), button -> changePage(1))
                .dimensions(x + backgroundWidth - 26, y + 7, 18, 16).build());
    }

    private void changePage(int direction) {
        int requested = handler.getPage() + direction;
        if (requested < 0 || requested >= handler.getPageCount() || client == null
                || client.interactionManager == null) {
            return;
        }
        handler.setClientPage(requested);
        client.interactionManager.clickButton(handler.syncId, requested);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        previousButton.active = handler.getPage() > 0;
        nextButton.active = handler.getPage() + 1 < handler.getPageCount();
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int color = handler.getNetworkColor();
        int dark = 0xFF000000 | shade(color, 0.16f);
        int mid = 0xFF000000 | shade(color, 0.36f);
        int bright = 0xFF000000 | shade(color, 0.84f);

        context.fill(x + 3, y + 4, x + backgroundWidth + 4, y + backgroundHeight + 5, 0x99000000);
        context.fill(x, y, x + backgroundWidth, y + backgroundHeight, 0xF2090718);
        context.fill(x + 3, y + 3, x + backgroundWidth - 3, y + 25, dark);
        outline(context, x, y, backgroundWidth, backgroundHeight, bright);
        outline(context, x + 5, y + 25, backgroundWidth - 10, 113, mid);
        outline(context, x + 5, y + 151, backgroundWidth - 10, 90, mid);

        long seed = handler.getNetworkId().getMostSignificantBits()
                ^ handler.getNetworkId().getLeastSignificantBits();
        int[] starX = new int[30];
        int[] starY = new int[30];
        for (int i = 0; i < starX.length; i++) {
            seed = seed * 6364136223846793005L + 1442695040888963407L;
            starX[i] = x + 7 + (int) Math.floorMod(seed, backgroundWidth - 14);
            seed = seed * 6364136223846793005L + 1442695040888963407L;
            starY[i] = y + 27 + (int) Math.floorMod(seed, 110);
            int starColor = i % 5 == 0 ? 0xFFFFFFFF : bright;
            context.fill(starX[i], starY[i], starX[i] + 1 + i % 2, starY[i] + 1 + i % 2, starColor);
        }
        for (int i = 0; i < 10; i++) {
            pixelLine(context, starX[i], starY[i], starX[(i * 7 + 11) % starX.length],
                    starY[(i * 7 + 11) % starY.length], 0x33000000 | color);
        }

        for (int row = 0; row < 6; row++) {
            for (int column = 0; column < 9; column++) {
                slot(context, x + 16 + column * 18, y + 27 + row * 18, mid);
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                slot(context, x + 16 + column * 18, y + 156 + row * 18, 0xFF33284F);
            }
        }
        for (int column = 0; column < 9; column++) {
            slot(context, x + 16 + column * 18, y + 216, 0xFF493264);
        }

        for (int page = 0; page < handler.getPageCount(); page++) {
            int dotX = x + backgroundWidth / 2 - (handler.getPageCount() * 7) / 2 + page * 7;
            int dotColor = page == handler.getPage() ? 0xFFFFFFFF : bright;
            context.fill(dotX, y + 20, dotX + 4, y + 22, dotColor);
        }
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        int color = handler.getNetworkColor();
        context.drawCenteredTextWithShadow(textRenderer, title, backgroundWidth / 2, 8, 0xFFFFFF);
        context.drawText(textRenderer,
                Text.translatable("screen.axiomata.page", handler.getPage() + 1, handler.getPageCount()),
                8, 140, color, false);
        Text source = Text.translatable(handler.isPortable()
                        ? "screen.axiomata.source_key" : "screen.axiomata.source_anchor",
                AstralColors.shortId(handler.getNetworkId()));
        context.drawText(textRenderer, source, backgroundWidth - 8 - textRenderer.getWidth(source),
                140, 0x8F7AAA, false);
    }

    private static void slot(DrawContext context, int left, int top, int border) {
        context.fill(left, top, left + 18, top + 18, border);
        context.fill(left + 1, top + 1, left + 17, top + 17, 0xDC100D20);
    }

    private static void outline(DrawContext context, int left, int top, int width, int height, int color) {
        context.fill(left, top, left + width, top + 1, color);
        context.fill(left, top + height - 1, left + width, top + height, color);
        context.fill(left, top, left + 1, top + height, color);
        context.fill(left + width - 1, top, left + width, top + height, color);
    }

    private static void pixelLine(DrawContext context, int x0, int y0, int x1, int y1, int color) {
        int steps = Math.max(Math.abs(x1 - x0), Math.abs(y1 - y0));
        if (steps == 0) {
            return;
        }
        for (int step = 0; step <= steps; step += 3) {
            int x = x0 + (x1 - x0) * step / steps;
            int y = y0 + (y1 - y0) * step / steps;
            context.fill(x, y, x + 1, y + 1, color);
        }
    }

    private static int shade(int color, float brightness) {
        int red = Math.min(255, (int) ((color >> 16 & 255) * brightness));
        int green = Math.min(255, (int) ((color >> 8 & 255) * brightness));
        int blue = Math.min(255, (int) ((color & 255) * brightness));
        return red << 16 | green << 8 | blue;
    }
}
