package net.bored.storage;

import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class AstralStorageState extends PersistentState {
    public static final int SLOTS_PER_PAGE = 54;
    public static final int MAX_PAGES = 4;
    public static final int MAX_SLOTS = SLOTS_PER_PAGE * MAX_PAGES;
    private static final String SAVE_KEY = "axiomata_astral_storage";

    private final Map<UUID, VaultData> vaults = new HashMap<>();

    public static AstralStorageState get(ServerWorld world) {
        ServerWorld overworld = world.getServer().getOverworld();
        return overworld.getPersistentStateManager().getOrCreate(
                AstralStorageState::fromNbt, AstralStorageState::new, SAVE_KEY);
    }

    public static AstralStorageState fromNbt(NbtCompound nbt) {
        AstralStorageState state = new AstralStorageState();
        NbtList vaultList = nbt.getList("Vaults", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < vaultList.size(); i++) {
            NbtCompound vaultNbt = vaultList.getCompound(i);
            if (!vaultNbt.containsUuid("Id")) {
                continue;
            }
            UUID id = vaultNbt.getUuid("Id");
            VaultData data = new VaultData();
            data.pages = AstralCapacity.normalizePages(vaultNbt.getInt("Pages"));
            Inventories.readNbt(vaultNbt, data.stacks);
            state.vaults.put(id, data);
        }
        return state;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList vaultList = new NbtList();
        for (Map.Entry<UUID, VaultData> entry : vaults.entrySet()) {
            NbtCompound vaultNbt = new NbtCompound();
            vaultNbt.putUuid("Id", entry.getKey());
            vaultNbt.putInt("Pages", entry.getValue().pages);
            Inventories.writeNbt(vaultNbt, entry.getValue().stacks);
            vaultList.add(vaultNbt);
        }
        nbt.put("Vaults", vaultList);
        return nbt;
    }

    public void ensureVault(UUID id) {
        if (!vaults.containsKey(id)) {
            vaults.put(id, new VaultData());
            markDirty();
        }
    }

    DefaultedList<ItemStack> getStacks(UUID id) {
        ensureVault(id);
        return vaults.get(id).stacks;
    }

    public int getPages(UUID id) {
        ensureVault(id);
        return vaults.get(id).pages;
    }

    public int upgrade(UUID id) {
        ensureVault(id);
        VaultData data = vaults.get(id);
        int upgraded = AstralCapacity.upgradePages(data.pages);
        if (upgraded != data.pages) {
            data.pages = upgraded;
            markDirty();
        }
        return data.pages;
    }

    public int countUsedSlots(UUID id) {
        ensureVault(id);
        int used = 0;
        int capacity = AstralCapacity.slotsForPages(getPages(id));
        for (int i = 0; i < capacity; i++) {
            if (!vaults.get(id).stacks.get(i).isEmpty()) {
                used++;
            }
        }
        return used;
    }

    private static final class VaultData {
        private final DefaultedList<ItemStack> stacks = DefaultedList.ofSize(MAX_SLOTS, ItemStack.EMPTY);
        private int pages = 1;
    }
}
