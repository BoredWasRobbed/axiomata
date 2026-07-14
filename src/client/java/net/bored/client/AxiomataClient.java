package net.bored.client;

import net.bored.client.render.OfferingPlinthRenderer;
import net.bored.client.render.RitualNexusRenderer;
import net.bored.client.render.RuneMarkRenderer;
import net.bored.content.ModBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

public final class AxiomataClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(ModBlockEntities.RITUAL_NEXUS, RitualNexusRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.OFFERING_PLINTH, OfferingPlinthRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.RUNE_MARK, RuneMarkRenderer::new);
    }
}
