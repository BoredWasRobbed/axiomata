package net.bored.block.entity;

import net.bored.content.ModBlockEntities;
import net.bored.network.AstralNetworkScanner;
import net.bored.screen.AstralStorageScreenHandler;
import net.bored.storage.AstralColors;
import net.bored.storage.AstralInventory;
import net.bored.storage.AstralPower;
import net.bored.storage.AstralStorageState;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class AstralAnchorBlockEntity extends SyncedBlockEntity implements ExtendedScreenHandlerFactory {
    private UUID networkId;
    private int usedSlots;
    private int pageCount = 1;
    private int energy;
    private int maxEnergy = AstralPower.capacityForPages(1);
    private int pylons;
    private int frameConduits;
    private int collectors;
    private int poweredCollectors;
    private int generation;
    private boolean frameComplete;
    private boolean operational;
    private long lastAccessTime = Long.MIN_VALUE;
    private long lastGenerationTime = Long.MIN_VALUE;

    public AstralAnchorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ASTRAL_ANCHOR, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, AstralAnchorBlockEntity anchor) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }
        anchor.ensureNetwork();
        if (world.getTime() % 20 == 0) {
            anchor.refreshRitual(serverWorld);
        }
        if (world.getTime() % 10 == 0) {
            anchor.refreshMetrics(serverWorld);
        }
    }

    public UUID ensureNetwork() {
        if (networkId == null) {
            networkId = UUID.randomUUID();
            if (world instanceof ServerWorld serverWorld) {
                AstralStorageState.get(serverWorld).ensureVault(networkId);
            }
            sync();
        }
        return networkId;
    }

    public void setNetwork(UUID networkId) {
        this.networkId = networkId;
        if (world instanceof ServerWorld serverWorld) {
            AstralStorageState.get(serverWorld).ensureVault(networkId);
            refreshRitual(serverWorld);
            refreshMetrics(serverWorld);
        }
        markAccess();
    }

    public boolean prepareInteraction() {
        if (!(world instanceof ServerWorld serverWorld)) {
            return false;
        }
        refreshRitual(serverWorld);
        refreshMetrics(serverWorld);
        return operational;
    }

    public boolean consumeResonance(int amount) {
        if (!(world instanceof ServerWorld serverWorld) || !prepareInteraction()) {
            return false;
        }
        AstralStorageState storage = AstralStorageState.get(serverWorld);
        boolean consumed = storage.consume(ensureNetwork(), amount, AstralPower.networkTime(serverWorld));
        refreshMetrics(serverWorld);
        if (consumed) {
            markAccess();
        }
        return consumed;
    }

    public int tryUpgrade() {
        if (!(world instanceof ServerWorld serverWorld) || !prepareInteraction()) {
            return -1;
        }
        UUID id = ensureNetwork();
        AstralStorageState storage = AstralStorageState.get(serverWorld);
        int before = storage.getPages(id);
        if (before >= AstralStorageState.MAX_PAGES) {
            return 0;
        }
        if (!storage.consume(id, AstralPower.CELL_UPGRADE_COST, AstralPower.networkTime(serverWorld))) {
            refreshMetrics(serverWorld);
            return -1;
        }
        int upgraded = storage.upgrade(id);
        refreshMetrics(serverWorld);
        markAccess();
        return upgraded > before ? upgraded : 0;
    }

    public Text getRitualStatus() {
        if (pylons < AstralNetworkScanner.REQUIRED_PYLONS) {
            return Text.translatable("message.axiomata.ritual_pylons", pylons,
                    AstralNetworkScanner.REQUIRED_PYLONS);
        }
        if (frameConduits < AstralNetworkScanner.REQUIRED_FRAME_CONDUITS) {
            return Text.translatable("message.axiomata.ritual_conduits", frameConduits,
                    AstralNetworkScanner.REQUIRED_FRAME_CONDUITS);
        }
        if (collectors == 0) {
            return Text.translatable("message.axiomata.ritual_collector");
        }
        if (poweredCollectors == 0) {
            return Text.translatable("message.axiomata.ritual_sky");
        }
        return Text.translatable("message.axiomata.ritual_resonance", energy, maxEnergy);
    }

    public void markAccess() {
        if (world != null) {
            lastAccessTime = world.getTime();
        }
        sync();
    }

    private void refreshRitual(ServerWorld serverWorld) {
        AstralNetworkScanner.ScanResult result = AstralNetworkScanner.scan(serverWorld, pos);
        boolean nextOperational = result.operational();
        UUID id = ensureNetwork();
        AstralStorageState storage = AstralStorageState.get(serverWorld);
        long time = AstralPower.networkTime(serverWorld);
        if (nextOperational && (lastGenerationTime == Long.MIN_VALUE || time - lastGenerationTime >= 20)) {
            storage.charge(id, result.generation(), time);
            lastGenerationTime = time;
        }
        int nextEnergy = storage.getEnergy(id);
        int nextMaxEnergy = storage.getMaxEnergy(id);
        boolean changed = pylons != result.pylons() || frameConduits != result.frameConduits()
                || collectors != result.collectors() || poweredCollectors != result.poweredCollectors()
                || generation != result.generation() || frameComplete != result.frameComplete()
                || operational != nextOperational || energy != nextEnergy || maxEnergy != nextMaxEnergy;
        pylons = result.pylons();
        frameConduits = result.frameConduits();
        collectors = result.collectors();
        poweredCollectors = result.poweredCollectors();
        generation = result.generation();
        frameComplete = result.frameComplete();
        operational = nextOperational;
        energy = nextEnergy;
        maxEnergy = nextMaxEnergy;

        if (nextOperational) {
            int strength = Math.min(100, 35 + Math.round(65.0f * energy / Math.max(1, maxEnergy)));
            int color = getNetworkColor();
            for (BlockPos nodePos : result.ritualNodes()) {
                if (serverWorld.getBlockEntity(nodePos) instanceof AstralNodeBlockEntity node) {
                    node.pulse(color, strength, serverWorld.getTime());
                }
            }
        }
        if (changed) {
            sync();
        }
    }

    private void refreshMetrics(ServerWorld world) {
        UUID id = ensureNetwork();
        AstralStorageState storage = AstralStorageState.get(world);
        int nextPages = storage.getPages(id);
        int nextUsed = storage.countUsedSlots(id);
        int nextEnergy = storage.getEnergy(id);
        int nextMax = storage.getMaxEnergy(id);
        if (nextPages != pageCount || nextUsed != usedSlots || nextEnergy != energy || nextMax != maxEnergy) {
            pageCount = nextPages;
            usedSlots = nextUsed;
            energy = nextEnergy;
            maxEnergy = nextMax;
            sync();
        }
    }

    public int getPageCount() {
        return pageCount;
    }

    public int getUsedSlots() {
        return usedSlots;
    }

    public float getFillRatio() {
        return usedSlots / (float) Math.max(1, pageCount * AstralStorageState.SLOTS_PER_PAGE);
    }

    public float getEnergyRatio() {
        return energy / (float) Math.max(1, maxEnergy);
    }

    public int getEnergy() {
        return energy;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public int getGeneration() {
        return generation;
    }

    public boolean isFrameComplete() {
        return frameComplete;
    }

    public boolean isOperational() {
        return operational;
    }

    public float getActivity(float tickDelta) {
        if (world == null || lastAccessTime == Long.MIN_VALUE) {
            return 0.0f;
        }
        return Math.max(0.0f, 1.0f - (world.getTime() + tickDelta - lastAccessTime) / 100.0f);
    }

    public int getNetworkColor() {
        return networkId == null ? 0xA88CFF : AstralColors.fromNetwork(networkId);
    }

    public int getComparatorOutput() {
        if (!operational || usedSlots == 0) {
            return 0;
        }
        return 1 + Math.min(14, (int) Math.floor(getFillRatio() * 14.0f));
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("screen.axiomata.astral_archive");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        if (!(world instanceof ServerWorld serverWorld) || !operational) {
            return null;
        }
        UUID id = ensureNetwork();
        AstralStorageState storage = AstralStorageState.get(serverWorld);
        markAccess();
        return new AstralStorageScreenHandler(syncId, playerInventory, new AstralInventory(storage, id),
                id, storage.getPages(id), ScreenHandlerContext.create(world, pos), false,
                storage, serverWorld);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buffer) {
        UUID id = ensureNetwork();
        int pages = world instanceof ServerWorld serverWorld
                ? AstralStorageState.get(serverWorld).getPages(id) : pageCount;
        buffer.writeUuid(id);
        buffer.writeVarInt(pages);
        buffer.writeBoolean(false);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (networkId != null) {
            nbt.putUuid("Network", networkId);
        }
        nbt.putInt("UsedSlots", usedSlots);
        nbt.putInt("Pages", pageCount);
        nbt.putInt("Energy", energy);
        nbt.putInt("MaxEnergy", maxEnergy);
        nbt.putInt("Pylons", pylons);
        nbt.putInt("FrameConduits", frameConduits);
        nbt.putInt("Collectors", collectors);
        nbt.putInt("PoweredCollectors", poweredCollectors);
        nbt.putInt("Generation", generation);
        nbt.putBoolean("FrameComplete", frameComplete);
        nbt.putBoolean("Operational", operational);
        nbt.putLong("LastAccess", lastAccessTime);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        networkId = nbt.containsUuid("Network") ? nbt.getUuid("Network") : null;
        usedSlots = nbt.getInt("UsedSlots");
        pageCount = Math.max(1, nbt.getInt("Pages"));
        energy = nbt.getInt("Energy");
        maxEnergy = Math.max(AstralPower.capacityForPages(1), nbt.getInt("MaxEnergy"));
        pylons = nbt.getInt("Pylons");
        frameConduits = nbt.getInt("FrameConduits");
        collectors = nbt.getInt("Collectors");
        poweredCollectors = nbt.getInt("PoweredCollectors");
        generation = nbt.getInt("Generation");
        frameComplete = nbt.getBoolean("FrameComplete");
        operational = nbt.getBoolean("Operational");
        lastAccessTime = nbt.getLong("LastAccess");
    }
}
