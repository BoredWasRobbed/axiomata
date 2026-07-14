package net.bored.content;

import net.bored.Axiomata;
import net.bored.block.AstralAnchorBlock;
import net.bored.block.AstralNodeBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModBlocks {
    public static final Block ASTRAL_ANCHOR = register("astral_anchor", new AstralAnchorBlock(
            AbstractBlock.Settings.copy(Blocks.OBSIDIAN).strength(5.0f, 12.0f)
                    .luminance(state -> 11).nonOpaque()));
    public static final Block LEY_CONDUIT = register("ley_conduit", new AstralNodeBlock(
            AstralNodeBlock.Kind.CONDUIT, AbstractBlock.Settings.copy(Blocks.AMETHYST_BLOCK)
                    .strength(1.8f).luminance(state -> 7).nonOpaque()));
    public static final Block RESONANCE_PYLON = register("resonance_pylon", new AstralNodeBlock(
            AstralNodeBlock.Kind.PYLON, AbstractBlock.Settings.copy(Blocks.CRYING_OBSIDIAN)
                    .strength(3.5f, 8.0f).luminance(state -> 9).nonOpaque()));
    public static final Block STARLIGHT_COLLECTOR = register("starlight_collector", new AstralNodeBlock(
            AstralNodeBlock.Kind.COLLECTOR, AbstractBlock.Settings.copy(Blocks.OBSIDIAN)
                    .strength(3.0f, 7.0f).luminance(state -> 10).nonOpaque()));

    private ModBlocks() {
    }

    private static Block register(String name, Block block) {
        return Registry.register(Registries.BLOCK, Axiomata.id(name), block);
    }

    public static void register() {
        // Class loading performs registration.
    }
}
