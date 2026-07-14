package net.bored.content;

import net.bored.Axiomata;
import net.bored.item.AstralCellItem;
import net.bored.item.AstralKeyItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Rarity;

public final class ModItems {
    public static final Item ASTRAL_ANCHOR = register("astral_anchor",
            new BlockItem(ModBlocks.ASTRAL_ANCHOR, new Item.Settings().rarity(Rarity.EPIC)));
    public static final Item ASTRAL_KEY = register("astral_key",
            new AstralKeyItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE)));
    public static final Item ASTRAL_CELL = register("astral_cell",
            new AstralCellItem(new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON)));
    public static final Item ASTRAL_THREAD = register("astral_thread",
            new Item(new Item.Settings().rarity(Rarity.UNCOMMON)));
    public static final Item LEY_CONDUIT = register("ley_conduit",
            new BlockItem(ModBlocks.LEY_CONDUIT, new Item.Settings().rarity(Rarity.UNCOMMON)));
    public static final Item RESONANCE_PYLON = register("resonance_pylon",
            new BlockItem(ModBlocks.RESONANCE_PYLON, new Item.Settings().rarity(Rarity.RARE)));
    public static final Item STARLIGHT_COLLECTOR = register("starlight_collector",
            new BlockItem(ModBlocks.STARLIGHT_COLLECTOR, new Item.Settings().rarity(Rarity.RARE)));

    private ModItems() {
    }

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, Axiomata.id(name), item);
    }

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.add(ASTRAL_ANCHOR);
            entries.add(ASTRAL_KEY);
            entries.add(ASTRAL_CELL);
            entries.add(ASTRAL_THREAD);
            entries.add(LEY_CONDUIT);
            entries.add(RESONANCE_PYLON);
            entries.add(STARLIGHT_COLLECTOR);
        });
    }
}
