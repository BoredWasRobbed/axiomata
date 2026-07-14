package net.bored.ritual;

import net.bored.Axiomata;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class RitualRegistry {
    private static final Map<String, RitualDefinition> RITUALS = new LinkedHashMap<>();

    private RitualRegistry() {
    }

    public static void bootstrap() {
        if (!RITUALS.isEmpty()) {
            return;
        }

        register(new RitualDefinition(Axiomata.id("material_concordance"), new int[]{0, 2, 4, 6},
                List.of(Items.IRON_INGOT, Items.COPPER_INGOT, Items.REDSTONE, Items.QUARTZ),
                100, 0xFFB52E, (world, origin, caster) -> {
            ItemEntity result = new ItemEntity(world, origin.getX() + 0.5, origin.getY() + 1.4,
                    origin.getZ() + 0.5, new ItemStack(Items.GOLD_INGOT, 4));
            result.setVelocity(0.0, 0.2, 0.0);
            world.spawnEntity(result);
            world.spawnParticles(ParticleTypes.FIREWORK, origin.getX() + 0.5, origin.getY() + 1.1,
                    origin.getZ() + 0.5, 70, 1.0, 0.7, 1.0, 0.08);
        }));

        register(new RitualDefinition(Axiomata.id("verdant_recursion"), new int[]{1, 3, 5, 7},
                List.of(Items.WHEAT_SEEDS, Items.BONE_MEAL, Items.GLOW_BERRIES, Items.EMERALD),
                130, 0x5CFF72, (world, origin, caster) -> {
            int grown = 0;
            for (BlockPos target : BlockPos.iterate(origin.add(-7, -2, -7), origin.add(7, 3, 7))) {
                BlockState state = world.getBlockState(target);
                if (state.getBlock() instanceof Fertilizable fertilizable
                        && fertilizable.isFertilizable(world, target, state, false)
                        && world.getRandom().nextFloat() < 0.55f) {
                    fertilizable.grow(world, world.getRandom(), target, state);
                    grown++;
                }
            }
            world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, origin.getX() + 0.5, origin.getY() + 1.0,
                    origin.getZ() + 0.5, Math.min(160, 40 + grown), 6.0, 2.0, 6.0, 0.04);
        }));

        register(new RitualDefinition(Axiomata.id("aegis_equation"), new int[]{7, 0, 7, 0},
                List.of(Items.SHIELD, Items.IRON_INGOT, Items.AMETHYST_SHARD, Items.GHAST_TEAR),
                120, 0x63C7FF, (world, origin, caster) -> {
            for (PlayerEntity player : world.getEntitiesByClass(PlayerEntity.class,
                    new Box(origin).expand(12.0), player -> !player.isSpectator())) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20 * 150, 1));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 20 * 150, 2));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 20 * 150, 0));
            }
            world.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING, origin.getX() + 0.5, origin.getY() + 1.0,
                    origin.getZ() + 0.5, 90, 5.0, 1.5, 5.0, 0.08);
        }));

        register(new RitualDefinition(Axiomata.id("tempest_postulate"), new int[]{3, 6, 1, 4},
                List.of(Items.COPPER_INGOT, Items.FEATHER, Items.REDSTONE, Items.PRISMARINE_SHARD),
                150, 0xB08CFF, (world, origin, caster) -> {
            world.setWeather(0, 20 * 90, true, true);
            List<HostileEntity> targets = world.getEntitiesByClass(HostileEntity.class,
                    new Box(origin).expand(18.0), entity -> entity.isAlive());
            Collections.shuffle(targets);
            for (HostileEntity target : targets.subList(0, Math.min(6, targets.size()))) {
                LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(world);
                if (lightning != null) {
                    lightning.refreshPositionAfterTeleport(target.getX(), target.getY(), target.getZ());
                    world.spawnEntity(lightning);
                }
            }
        }));

        register(new RitualDefinition(Axiomata.id("astral_translation"), new int[]{6, 6, 2, 2},
                List.of(Items.ENDER_PEARL, Items.CHORUS_FRUIT, Items.COMPASS, Items.AMETHYST_SHARD),
                110, 0xF46CFF, (world, origin, caster) -> {
            if (caster == null) {
                return;
            }
            Direction facing = caster.getHorizontalFacing();
            BlockPos column = origin.offset(facing, 48);
            BlockPos destination = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, column);
            if (!world.getWorldBorder().contains(destination)) {
                return;
            }
            world.spawnParticles(ParticleTypes.PORTAL, caster.getX(), caster.getBodyY(0.5), caster.getZ(),
                    100, 0.6, 1.0, 0.6, 0.2);
            caster.teleport(world, destination.getX() + 0.5, destination.getY() + 0.1,
                    destination.getZ() + 0.5, caster.getYaw(), caster.getPitch());
            caster.fallDistance = 0.0f;
            world.spawnParticles(ParticleTypes.REVERSE_PORTAL, destination.getX() + 0.5,
                    destination.getY() + 0.8, destination.getZ() + 0.5, 100, 0.6, 1.0, 0.6, 0.15);
        }));
    }

    private static void register(RitualDefinition definition) {
        RITUALS.put(definition.id().toString(), definition);
    }

    public static List<RitualDefinition> all() {
        return List.copyOf(RITUALS.values());
    }

    public static Optional<RitualDefinition> find(int[] runes, List<ItemStack> offerings) {
        return RITUALS.values().stream().filter(ritual -> ritual.matches(runes, offerings)).findFirst();
    }

    public static RitualDefinition byId(String id) {
        return RITUALS.get(id);
    }

    public static void completeSound(net.minecraft.server.world.ServerWorld world, BlockPos origin) {
        world.playSound(null, origin, SoundEvents.BLOCK_BEACON_POWER_SELECT, SoundCategory.BLOCKS, 1.4f, 1.35f);
    }
}
