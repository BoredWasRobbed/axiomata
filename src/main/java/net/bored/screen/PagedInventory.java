package net.bored.screen;

import net.bored.storage.AstralStorageState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

final class PagedInventory implements Inventory {
    private final Inventory backing;
    private int page;

    PagedInventory(Inventory backing) {
        this.backing = backing;
    }

    void setPage(int page) {
        this.page = page;
    }

    private int absolute(int slot) {
        return page * AstralStorageState.SLOTS_PER_PAGE + slot;
    }

    @Override
    public int size() {
        return AstralStorageState.SLOTS_PER_PAGE;
    }

    @Override
    public boolean isEmpty() {
        for (int slot = 0; slot < size(); slot++) {
            if (!getStack(slot).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return backing.getStack(absolute(slot));
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return backing.removeStack(absolute(slot), amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return backing.removeStack(absolute(slot));
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        backing.setStack(absolute(slot), stack);
    }

    @Override
    public void markDirty() {
        backing.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return backing.canPlayerUse(player);
    }

    @Override
    public void onOpen(PlayerEntity player) {
        backing.onOpen(player);
    }

    @Override
    public void onClose(PlayerEntity player) {
        backing.onClose(player);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return backing.isValid(absolute(slot), stack);
    }

    @Override
    public void clear() {
        for (int slot = 0; slot < size(); slot++) {
            backing.setStack(absolute(slot), ItemStack.EMPTY);
        }
        backing.markDirty();
    }
}
