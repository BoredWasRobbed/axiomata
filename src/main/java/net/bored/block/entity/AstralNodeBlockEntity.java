package net.bored.block.entity;

import net.bored.content.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class AstralNodeBlockEntity extends SyncedBlockEntity {
    private boolean energized;
    private int networkColor = 0x7766A0;
    private int strength;
    private long lastPulse = Long.MIN_VALUE;

    public AstralNodeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ASTRAL_NODE, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, AstralNodeBlockEntity node) {
        if (node.energized && world.getTime() - node.lastPulse > 45) {
            node.energized = false;
            node.strength = 0;
            node.sync();
        }
    }

    public void pulse(int color, int strength, long time) {
        boolean changed = !energized || networkColor != color || this.strength != strength;
        energized = true;
        networkColor = color;
        this.strength = strength;
        lastPulse = time;
        if (changed) {
            sync();
        } else {
            markDirty();
        }
    }

    public boolean isEnergized() {
        return energized;
    }

    public int getNetworkColor() {
        return networkColor;
    }

    public float getStrength() {
        return strength / 100.0f;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putBoolean("Energized", energized);
        nbt.putInt("Color", networkColor);
        nbt.putInt("Strength", strength);
        nbt.putLong("LastPulse", lastPulse);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        energized = nbt.getBoolean("Energized");
        networkColor = nbt.contains("Color") ? nbt.getInt("Color") : 0x7766A0;
        strength = nbt.getInt("Strength");
        lastPulse = nbt.getLong("LastPulse");
    }
}
