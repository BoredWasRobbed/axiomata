package net.bored.block.entity;

import net.bored.content.ModBlockEntities;
import net.bored.ritual.RitualDefinition;
import net.bored.ritual.RitualRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class RitualNexusBlockEntity extends SyncedBlockEntity {
    public static final BlockPos[] RUNE_OFFSETS = {
            new BlockPos(-1, 0, -1), new BlockPos(1, 0, -1),
            new BlockPos(1, 0, 1), new BlockPos(-1, 0, 1)
    };
    public static final BlockPos[] PLINTH_OFFSETS = {
            new BlockPos(0, 0, -3), new BlockPos(3, 0, 0),
            new BlockPos(0, 0, 3), new BlockPos(-3, 0, 0)
    };

    private String activeRitual = "";
    private int progress;
    private int activeDuration;
    private int activeColor = 0x9B6CFF;
    private int cooldown;
    private UUID casterUuid;

    public RitualNexusBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RITUAL_NEXUS, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, RitualNexusBlockEntity nexus) {
        if (world instanceof ServerWorld serverWorld) {
            nexus.serverTick(serverWorld);
        }
    }

    private void serverTick(ServerWorld world) {
        if (cooldown > 0) {
            cooldown--;
        }
        if (activeRitual.isEmpty()) {
            return;
        }

        RitualDefinition ritual = RitualRegistry.byId(activeRitual);
        if (ritual == null) {
            cancelRitual();
            return;
        }

        progress++;
        if (progress % 4 == 0) {
            double angle = progress * 0.24;
            world.spawnParticles(ParticleTypes.ENCHANT,
                    pos.getX() + 0.5 + Math.cos(angle) * 1.6,
                    pos.getY() + 0.4 + progress / (double) activeDuration * 1.8,
                    pos.getZ() + 0.5 + Math.sin(angle) * 1.6,
                    4, 0.12, 0.12, 0.12, 0.02);
        }
        if (progress % 5 == 0) {
            sync();
        }
        if (progress >= activeDuration) {
            ServerPlayerEntity caster = casterUuid == null ? null
                    : world.getServer().getPlayerManager().getPlayer(casterUuid);
            ritual.effect().apply(world, pos, caster);
            RitualRegistry.completeSound(world, pos);
            if (caster != null) {
                caster.sendMessage(Text.translatable("message.axiomata.ritual_complete", ritual.displayName())
                        .formatted(Formatting.LIGHT_PURPLE), false);
            }
            cancelRitual();
            cooldown = 80;
        }
    }

    public void tryStart(ServerPlayerEntity player) {
        if (isActive()) {
            player.sendMessage(Text.translatable("message.axiomata.already_active").formatted(Formatting.RED), true);
            return;
        }
        if (cooldown > 0) {
            player.sendMessage(Text.translatable("message.axiomata.cooling", (cooldown + 19) / 20)
                    .formatted(Formatting.GRAY), true);
            return;
        }

        Assembly assembly = readAssembly();
        if (!assembly.complete()) {
            player.sendMessage(Text.translatable("message.axiomata.structure_incomplete")
                    .formatted(Formatting.RED), true);
            return;
        }
        Optional<RitualDefinition> match = RitualRegistry.find(assembly.runes(), assembly.offerings());
        if (match.isEmpty()) {
            player.sendMessage(Text.translatable("message.axiomata.no_axiom", runeSequence(assembly.runes()))
                    .formatted(Formatting.RED), false);
            return;
        }

        RitualDefinition ritual = match.get();
        for (OfferingPlinthBlockEntity plinth : assembly.plinths()) {
            plinth.removeStack(0, 1);
        }
        activeRitual = ritual.id().toString();
        activeDuration = ritual.durationTicks();
        activeColor = ritual.color();
        progress = 0;
        casterUuid = player.getUuid();
        chargeRunes(true);
        sync();
        world.playSound(null, pos, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 1.2f, 0.7f);
        player.sendMessage(Text.translatable("message.axiomata.ritual_started", ritual.displayName())
                .formatted(Formatting.LIGHT_PURPLE), false);
    }

    public void inspect(ServerPlayerEntity player) {
        Assembly assembly = readAssembly();
        if (!assembly.complete()) {
            player.sendMessage(Text.translatable("message.axiomata.structure_hint").formatted(Formatting.GRAY), false);
            return;
        }
        String offerings = assembly.offerings().stream()
                .map(stack -> stack.isEmpty() ? "—" : stack.getName().getString())
                .reduce((left, right) -> left + ", " + right).orElse("—");
        player.sendMessage(Text.literal("Runes NW→NE→SE→SW: " + runeSequence(assembly.runes()))
                .formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("Offerings N→E→S→W: " + offerings).formatted(Formatting.GRAY), false);
    }

    public void releaseRunes() {
        chargeRunes(false);
    }

    private Assembly readAssembly() {
        int[] runes = new int[4];
        List<ItemStack> offerings = new ArrayList<>(4);
        List<OfferingPlinthBlockEntity> plinths = new ArrayList<>(4);
        boolean complete = world != null;

        for (int i = 0; i < RUNE_OFFSETS.length && world != null; i++) {
            if (world.getBlockEntity(pos.add(RUNE_OFFSETS[i])) instanceof RuneMarkBlockEntity rune) {
                runes[i] = rune.getRune();
            } else {
                complete = false;
            }
        }
        for (BlockPos offset : PLINTH_OFFSETS) {
            if (world != null && world.getBlockEntity(pos.add(offset)) instanceof OfferingPlinthBlockEntity plinth) {
                plinths.add(plinth);
                offerings.add(plinth.getStack(0));
                if (plinth.isEmpty()) {
                    complete = false;
                }
            } else {
                complete = false;
                offerings.add(ItemStack.EMPTY);
            }
        }
        return new Assembly(runes, offerings, plinths, complete && plinths.size() == 4);
    }

    private void chargeRunes(boolean charged) {
        if (world == null) {
            return;
        }
        for (BlockPos offset : RUNE_OFFSETS) {
            if (world.getBlockEntity(pos.add(offset)) instanceof RuneMarkBlockEntity rune) {
                rune.setCharged(charged);
            }
        }
    }

    private void cancelRitual() {
        releaseRunes();
        activeRitual = "";
        progress = 0;
        activeDuration = 0;
        casterUuid = null;
        sync();
    }

    private static String runeSequence(int[] runes) {
        return Arrays.toString(runes).replace("[", "").replace("]", "");
    }

    public boolean isActive() {
        return !activeRitual.isEmpty();
    }

    public float getVisualProgress(float tickDelta) {
        return activeDuration <= 0 ? 0.0f : Math.min(1.0f, (progress + tickDelta) / activeDuration);
    }

    public int getVisualColor() {
        return isActive() ? activeColor : 0x9B6CFF;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("ActiveRitual", activeRitual);
        nbt.putInt("Progress", progress);
        nbt.putInt("ActiveDuration", activeDuration);
        nbt.putInt("ActiveColor", activeColor);
        nbt.putInt("Cooldown", cooldown);
        if (casterUuid != null) {
            nbt.putUuid("Caster", casterUuid);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        activeRitual = nbt.getString("ActiveRitual");
        progress = nbt.getInt("Progress");
        activeDuration = nbt.getInt("ActiveDuration");
        activeColor = nbt.getInt("ActiveColor");
        cooldown = nbt.getInt("Cooldown");
        casterUuid = nbt.containsUuid("Caster") ? nbt.getUuid("Caster") : null;
    }

    private record Assembly(int[] runes, List<ItemStack> offerings,
                            List<OfferingPlinthBlockEntity> plinths, boolean complete) {
    }
}
