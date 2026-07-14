package net.bored.content;

import net.bored.Axiomata;
import net.bored.block.OfferingPlinthBlock;
import net.bored.block.RitualNexusBlock;
import net.bored.block.RuneMarkBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;

public final class ModBlocks {
    public static final Block RITUAL_NEXUS = register("ritual_nexus", new RitualNexusBlock(
            AbstractBlock.Settings.copy(Blocks.OBSIDIAN).strength(5.0f, 12.0f).luminance(state -> 9).nonOpaque()));
    public static final Block OFFERING_PLINTH = register("offering_plinth", new OfferingPlinthBlock(
            AbstractBlock.Settings.copy(Blocks.DEEPSLATE_TILES).strength(3.5f).luminance(state -> 5).nonOpaque()));
    public static final Block RUNE_MARK = register("rune_mark", new RuneMarkBlock(
            AbstractBlock.Settings.create().strength(0.25f).noCollision().nonOpaque()
                    .luminance(state -> 8).sounds(BlockSoundGroup.AMETHYST_CLUSTER)));

    private ModBlocks() {
    }

    private static Block register(String name, Block block) {
        return Registry.register(Registries.BLOCK, Axiomata.id(name), block);
    }

    public static void register() {
        // Class loading performs registration.
    }
}
