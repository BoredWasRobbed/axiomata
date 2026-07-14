package net.bored.block.entity;

import net.bored.content.ModBlockEntities;
import net.bored.screen.AstralStorageScreenHandler;
import net.bored.storage.AstralColors;
import net.bored.storage.AstralInventory;
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
    private long lastAccessTime = Long.MIN_VALUE;

    public AstralAnchorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ASTRAL_ANCHOR, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, AstralAnchorBlockEntity anchor) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }
        anchor.ensureNetwork();
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
            refreshMetrics(serverWorld);
        }
        markAccess();
    }

    public int tryUpgrade() {
        if (!(world instanceof ServerWorld serverWorld)) {
            return 0;
        }
        UUID id = ensureNetwork();
        AstralStorageState storage = AstralStorageState.get(serverWorld);
        int before = storage.getPages(id);
        int upgraded = storage.upgrade(id);
        refreshMetrics(serverWorld);
        markAccess();
        return upgraded > before ? upgraded : 0;
    }

    public void markAccess() {
        if (world != null) {
            lastAccessTime = world.getTime();
        }
        sync();
    }

    private void refreshMetrics(ServerWorld world) {
        UUID id = ensureNetwork();
        AstralStorageState storage = AstralStorageState.get(world);
        int nextPages = storage.getPages(id);
        int nextUsed = storage.countUsedSlots(id);
        if (nextPages != pageCount || nextUsed != usedSlots) {
            pageCount = nextPages;
            usedSlots = nextUsed;
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

    public float getActivity(float tickDelta) {
        if (world == null || lastAccessTime == Long.MIN_VALUE) {
            return 0.0f;
        }
        return Math.max(0.0f, 1.0f - (world.getTime() + tickDelta - lastAccessTime) / 100.0f);
    }

    public int getNetworkColor() {
        return networkId == null ? 0xA88CFF : AstralColors.fromNetwork(networkId);
    }

    public long getVisualSeed() {
        return networkId == null ? pos.asLong() : networkId.getMostSignificantBits() ^ networkId.getLeastSignificantBits();
    }

    public int getComparatorOutput() {
        if (usedSlots == 0) {
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
        if (!(world instanceof ServerWorld serverWorld)) {
            return null;
        }
        UUID id = ensureNetwork();
        AstralStorageState storage = AstralStorageState.get(serverWorld);
        markAccess();
        return new AstralStorageScreenHandler(syncId, playerInventory, new AstralInventory(storage, id),
                id, storage.getPages(id), ScreenHandlerContext.create(world, pos), false);
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
        nbt.putLong("LastAccess", lastAccessTime);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        networkId = nbt.containsUuid("Network") ? nbt.getUuid("Network") : null;
        usedSlots = nbt.getInt("UsedSlots");
        pageCount = Math.max(1, nbt.getInt("Pages"));
        lastAccessTime = nbt.getLong("LastAccess");
    }
}
