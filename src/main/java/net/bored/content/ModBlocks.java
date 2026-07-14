package net.bored.content;

import net.bored.Axiomata;
import net.bored.block.AstralAnchorBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModBlocks {
    public static final Block ASTRAL_ANCHOR = register("astral_anchor", new AstralAnchorBlock(
            AbstractBlock.Settings.copy(Blocks.OBSIDIAN).strength(5.0f, 12.0f)
                    .luminance(state -> 11).nonOpaque()));

    private ModBlocks() {
    }

    private static Block register(String name, Block block) {
        return Registry.register(Registries.BLOCK, Axiomata.id(name), block);
    }

    public static void register() {
        // Class loading performs registration.
    }
}
