package net.bored.content;

import net.bored.Axiomata;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Rarity;

public final class ModItems {
    public static final Item RITUAL_NEXUS = register("ritual_nexus",
            new BlockItem(ModBlocks.RITUAL_NEXUS, new Item.Settings().rarity(Rarity.EPIC)));
    public static final Item OFFERING_PLINTH = register("offering_plinth",
            new BlockItem(ModBlocks.OFFERING_PLINTH, new Item.Settings().rarity(Rarity.UNCOMMON)));
    public static final Item RUNE_MARK = register("rune_mark",
            new BlockItem(ModBlocks.RUNE_MARK, new Item.Settings()));
    public static final Item RESONANCE_TUNER = register("resonance_tuner",
            new Item(new Item.Settings().maxCount(1).rarity(Rarity.RARE)));

    private ModItems() {
    }

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, Axiomata.id(name), item);
    }

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.add(RITUAL_NEXUS);
            entries.add(OFFERING_PLINTH);
            entries.add(RUNE_MARK);
            entries.add(RESONANCE_TUNER);
        });
    }
}
