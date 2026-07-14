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
        });
    }
}
