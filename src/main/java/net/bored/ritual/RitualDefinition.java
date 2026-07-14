package net.bored.ritual;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;

public record RitualDefinition(Identifier id, int[] runes, List<Item> offerings, int durationTicks, int color,
                               RitualEffect effect) {
    public RitualDefinition {
        runes = Arrays.copyOf(runes, runes.length);
        offerings = List.copyOf(offerings);
        if (runes.length != 4 || offerings.size() != 4) {
            throw new IllegalArgumentException("A ritual requires exactly four runes and four offerings");
        }
    }

    @Override
    public int[] runes() {
        return Arrays.copyOf(runes, runes.length);
    }

    public Text displayName() {
        return Text.translatable("ritual.axiomata." + id.getPath());
    }

    public boolean matches(int[] foundRunes, List<ItemStack> foundOfferings) {
        if (!Arrays.equals(runes, foundRunes) || foundOfferings.size() != offerings.size()) {
            return false;
        }
        boolean[] claimed = new boolean[foundOfferings.size()];
        for (Item required : offerings) {
            boolean matched = false;
            for (int i = 0; i < foundOfferings.size(); i++) {
                if (!claimed[i] && foundOfferings.get(i).isOf(required)) {
                    claimed[i] = true;
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                return false;
            }
        }
        return true;
    }
}
