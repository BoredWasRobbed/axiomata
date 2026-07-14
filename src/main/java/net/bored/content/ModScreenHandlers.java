package net.bored.content;

import net.bored.Axiomata;
import net.bored.screen.AstralStorageScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;

public final class ModScreenHandlers {
    public static final ScreenHandlerType<AstralStorageScreenHandler> ASTRAL_STORAGE =
            ScreenHandlerRegistry.registerExtended(Axiomata.id("astral_storage"),
                    AstralStorageScreenHandler::new);

    private ModScreenHandlers() {
    }

    public static void register() {
        // Class loading performs registration.
    }
}
