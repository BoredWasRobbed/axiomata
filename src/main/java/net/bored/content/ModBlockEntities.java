package net.bored.content;

import net.bored.Axiomata;
import net.bored.block.entity.OfferingPlinthBlockEntity;
import net.bored.block.entity.RitualNexusBlockEntity;
import net.bored.block.entity.RuneMarkBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModBlockEntities {
    public static final BlockEntityType<RitualNexusBlockEntity> RITUAL_NEXUS = register("ritual_nexus",
            FabricBlockEntityTypeBuilder.create(RitualNexusBlockEntity::new, ModBlocks.RITUAL_NEXUS).build());
    public static final BlockEntityType<OfferingPlinthBlockEntity> OFFERING_PLINTH = register("offering_plinth",
            FabricBlockEntityTypeBuilder.create(OfferingPlinthBlockEntity::new, ModBlocks.OFFERING_PLINTH).build());
    public static final BlockEntityType<RuneMarkBlockEntity> RUNE_MARK = register("rune_mark",
            FabricBlockEntityTypeBuilder.create(RuneMarkBlockEntity::new, ModBlocks.RUNE_MARK).build());

    private ModBlockEntities() {
    }

    private static <T extends BlockEntityType<?>> T register(String name, T type) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Axiomata.id(name), type);
    }

    public static void register() {
        // Class loading performs registration.
    }
}
