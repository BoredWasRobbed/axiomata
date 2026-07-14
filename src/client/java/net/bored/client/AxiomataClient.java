package net.bored.client;

import net.bored.client.render.AstralAnchorRenderer;
import net.bored.client.screen.AstralStorageScreen;
import net.bored.content.ModBlockEntities;
import net.bored.content.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public final class AxiomataClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(ModBlockEntities.ASTRAL_ANCHOR, AstralAnchorRenderer::new);
        HandledScreens.register(ModScreenHandlers.ASTRAL_STORAGE, AstralStorageScreen::new);
    }
}
