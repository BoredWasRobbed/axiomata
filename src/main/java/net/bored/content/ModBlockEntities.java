package net.bored.content;

import net.bored.Axiomata;
import net.bored.block.entity.AstralAnchorBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModBlockEntities {
    public static final BlockEntityType<AstralAnchorBlockEntity> ASTRAL_ANCHOR = Registry.register(
            Registries.BLOCK_ENTITY_TYPE, Axiomata.id("astral_anchor"),
            FabricBlockEntityTypeBuilder.create(AstralAnchorBlockEntity::new, ModBlocks.ASTRAL_ANCHOR).build());

    private ModBlockEntities() {
    }

    public static void register() {
        // Class loading performs registration.
    }
}
