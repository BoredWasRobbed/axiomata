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
        previousButton = addDrawableChild(ButtonWidget.builder(Text.literal("<"), button -> changePage(-1))
                .dimensions(x + 8, y + 6, 18, 16).build());
        nextButton = addDrawableChild(ButtonWidget.builder(Text.literal(">"), button -> changePage(1))
                .dimensions(x + backgroundWidth - 26, y + 6, 18, 16).build());
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
        int deep = 0xFF000000 | shade(color, 0.11f);
        int dark = 0xFF000000 | shade(color, 0.22f);
        int mid = 0xFF000000 | shade(color, 0.43f);
        int bright = 0xFF000000 | shade(color, 0.88f);

        context.fill(x + 4, y + 5, x + backgroundWidth + 5, y + backgroundHeight + 6, 0x99000000);
        context.fill(x, y, x + backgroundWidth, y + backgroundHeight, 0xF1080613);
        context.fill(x + 2, y + 2, x + backgroundWidth - 2, y + 25, deep);
        context.fill(x + 5, y + 26, x + backgroundWidth - 5, y + 137, 0xE80C0918);
        context.fill(x + 5, y + 151, x + backgroundWidth - 5, y + 241, 0xE80C0918);
        cutCornerFrame(context, x, y, backgroundWidth, backgroundHeight, bright);
        cutCornerFrame(context, x + 5, y + 25, backgroundWidth - 10, 113, mid);
        cutCornerFrame(context, x + 5, y + 151, backgroundWidth - 10, 90, dark);

        for (int row = 0; row < 6; row++) {
            for (int column = 0; column < 9; column++) {
                slot(context, x + 16 + column * 18, y + 27 + row * 18, mid);
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                slot(context, x + 16 + column * 18, y + 156 + row * 18, 0xFF302641);
            }
        }
        for (int column = 0; column < 9; column++) {
            slot(context, x + 16 + column * 18, y + 216, 0xFF47305A);
        }

        int barLeft = x + 48;
        int barRight = x + 148;
        context.fill(barLeft, y + 141, barRight, y + 146, 0xFF120E1C);
        int fill = Math.round((barRight - barLeft - 2) * handler.getEnergy()
                / (float) handler.getMaxEnergy());
        context.fill(barLeft + 1, y + 142, barLeft + 1 + fill, y + 145,
                handler.isOnline() ? bright : 0xFF554D60);
        if (fill > 3) {
            long ticks = client != null && client.world != null ? client.world.getTime() : 0;
            int mote = (int) (ticks % Math.max(1, fill - 2));
            context.fill(barLeft + 1 + mote, y + 141, barLeft + 3 + mote, y + 146, 0xFFFFFFFF);
        }

        for (int page = 0; page < handler.getPageCount(); page++) {
            int dotX = x + backgroundWidth / 2 - (handler.getPageCount() * 8) / 2 + page * 8 + 2;
            int dotColor = page == handler.getPage() ? 0xFFFFFFFF : bright;
            diamond(context, dotX, y + 21, page == handler.getPage() ? 2 : 1, dotColor);
        }
        diamond(context, x + 38, y + 143, 3, handler.isOnline() ? bright : 0xFF554D60);
        diamond(context, x + 158, y + 143, 3, handler.isOnline() ? bright : 0xFF554D60);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        int color = handler.getNetworkColor();
        context.drawCenteredTextWithShadow(textRenderer, title, backgroundWidth / 2, 7, 0xFFFFFF);
        Text energy = handler.isOnline()
                ? Text.translatable("screen.axiomata.resonance", handler.getEnergy(), handler.getMaxEnergy())
                : Text.translatable("screen.axiomata.offline");
        context.drawText(textRenderer, energy, 8, 140, handler.isOnline() ? color : 0x756B80, false);
        Text source = Text.translatable(handler.isPortable()
                        ? "screen.axiomata.source_key" : "screen.axiomata.source_anchor",
                AstralColors.shortId(handler.getNetworkId()));
        context.drawText(textRenderer, source, backgroundWidth - 8 - textRenderer.getWidth(source),
                140, 0x9C89B2, false);
    }

    private static void slot(DrawContext context, int left, int top, int border) {
        context.fill(left, top, left + 18, top + 18, border);
        context.fill(left + 1, top + 1, left + 17, top + 17, 0xE20D0A18);
        context.fill(left + 2, top + 2, left + 16, top + 3, 0x25FFFFFF);
    }

    private static void cutCornerFrame(DrawContext context, int left, int top, int width, int height, int color) {
        context.fill(left + 3, top, left + width - 3, top + 1, color);
        context.fill(left + 3, top + height - 1, left + width - 3, top + height, color);
        context.fill(left, top + 3, left + 1, top + height - 3, color);
        context.fill(left + width - 1, top + 3, left + width, top + height - 3, color);
        context.fill(left + 1, top + 1, left + 3, top + 2, color);
        context.fill(left + width - 3, top + 1, left + width - 1, top + 2, color);
        context.fill(left + 1, top + height - 2, left + 3, top + height - 1, color);
        context.fill(left + width - 3, top + height - 2, left + width - 1, top + height - 1, color);
    }

    private static void diamond(DrawContext context, int centerX, int centerY, int radius, int color) {
        for (int dy = -radius; dy <= radius; dy++) {
            int halfWidth = radius - Math.abs(dy);
            context.fill(centerX - halfWidth, centerY + dy, centerX + halfWidth + 1, centerY + dy + 1, color);
        }
    }

    private static int shade(int color, float brightness) {
        int red = Math.min(255, (int) ((color >> 16 & 255) * brightness));
        int green = Math.min(255, (int) ((color >> 8 & 255) * brightness));
        int blue = Math.min(255, (int) ((color & 255) * brightness));
        return red << 16 | green << 8 | blue;
    }
}
