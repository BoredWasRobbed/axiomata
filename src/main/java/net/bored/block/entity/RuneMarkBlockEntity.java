package net.bored.block.entity;

import net.bored.content.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public final class RuneMarkBlockEntity extends SyncedBlockEntity {
    private int rune;
    private boolean charged;

    public RuneMarkBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RUNE_MARK, pos, state);
    }

    public int getRune() {
        return rune;
    }

    public boolean isCharged() {
        return charged;
    }

    public void setRune(int rune) {
        this.rune = rune & 7;
        sync();
    }

    public void setCharged(boolean charged) {
        if (this.charged != charged) {
            this.charged = charged;
            sync();
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("Rune", rune);
        nbt.putBoolean("Charged", charged);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        rune = nbt.getInt("Rune") & 7;
        charged = nbt.getBoolean("Charged");
    }
}
