package net.bored.ritual;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface RitualEffect {
    void apply(ServerWorld world, BlockPos origin, @Nullable ServerPlayerEntity caster);
}
