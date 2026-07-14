package net.bored;

import net.bored.content.ModBlockEntities;
import net.bored.content.ModBlocks;
import net.bored.content.ModItems;
import net.bored.ritual.RitualRegistry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Axiomata implements ModInitializer {
    public static final String MOD_ID = "axiomata";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModBlocks.register();
        ModItems.register();
        ModBlockEntities.register();
        RitualRegistry.bootstrap();
        LOGGER.info("Axiomata has inscribed {} ritual axioms", RitualRegistry.all().size());
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}
