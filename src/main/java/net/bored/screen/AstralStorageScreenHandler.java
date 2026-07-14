package net.bored.screen;

import net.bored.content.ModBlocks;
import net.bored.content.ModScreenHandlers;
import net.bored.storage.AstralColors;
import net.bored.storage.AstralPower;
import net.bored.storage.AstralStorageState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.UUID;

public final class AstralStorageScreenHandler extends ScreenHandler {
    private static final int STORAGE_SLOTS = AstralStorageState.SLOTS_PER_PAGE;

    private final Inventory storage;
    private final PagedInventory pageView;
    private final ScreenHandlerContext context;
    private final boolean portable;
    private final UUID networkId;
    private final int pageCount;
    private final AstralStorageState storageState;
    private final ServerWorld serverWorld;
    private final PropertyDelegate powerProperties;
    private int page;

    public AstralStorageScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buffer) {
        this(syncId, playerInventory, new SimpleInventory(AstralStorageState.MAX_SLOTS), readData(buffer),
                ScreenHandlerContext.EMPTY, null, null);
    }

    public AstralStorageScreenHandler(int syncId, PlayerInventory playerInventory, Inventory storage,
                                      UUID networkId, int pageCount, ScreenHandlerContext context,
                                      boolean portable, AstralStorageState storageState,
                                      ServerWorld serverWorld) {
        this(syncId, playerInventory, storage, new OpeningData(networkId, pageCount, portable), context,
                storageState, serverWorld);
    }

    private AstralStorageScreenHandler(int syncId, PlayerInventory playerInventory, Inventory storage,
                                       OpeningData data, ScreenHandlerContext context,
                                       AstralStorageState storageState, ServerWorld serverWorld) {
        super(ModScreenHandlers.ASTRAL_STORAGE, syncId);
        checkSize(storage, AstralStorageState.MAX_SLOTS);
        this.storage = storage;
        this.context = context;
        this.portable = data.portable();
        this.networkId = data.networkId();
        this.pageCount = Math.max(1, Math.min(AstralStorageState.MAX_PAGES, data.pageCount()));
        this.storageState = storageState;
        this.serverWorld = serverWorld;
        this.powerProperties = createPowerProperties(storageState, serverWorld, networkId);
        addProperties(powerProperties);
        this.pageView = new PagedInventory(storage);
        this.pageView.onOpen(playerInventory.player);

        for (int row = 0; row < 6; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(pageView, column + row * 9, 17 + column * 18, 28 + row * 18));
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, column + row * 9 + 9,
                        17 + column * 18, 157 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInventory, column, 17 + column * 18, 217));
        }
    }

    private static OpeningData readData(PacketByteBuf buffer) {
        return new OpeningData(buffer.readUuid(), buffer.readVarInt(), buffer.readBoolean());
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        boolean reachable = portable || canUse(context, player, ModBlocks.ASTRAL_ANCHOR);
        return reachable && (storageState == null || serverWorld == null
                || storageState.isOnline(networkId, AstralPower.networkTime(serverWorld)));
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        boolean validSlot = slotIndex >= 0 && slotIndex < slots.size();
        boolean archiveSlotAction = validSlot && slotIndex < STORAGE_SLOTS
                && (slots.get(slotIndex).hasStack() || !getCursorStack().isEmpty()
                || actionType == SlotActionType.SWAP);
        boolean shiftTransfer = validSlot && actionType == SlotActionType.QUICK_MOVE
                && slots.get(slotIndex).hasStack();
        boolean touchesArchive = archiveSlotAction || shiftTransfer;
        if (touchesArchive && storageState != null && serverWorld != null
                && !storageState.consume(networkId, AstralPower.STORAGE_ACTION_COST,
                AstralPower.networkTime(serverWorld))) {
            player.sendMessage(Text.translatable("message.axiomata.resonance_action")
                    .formatted(Formatting.GRAY), true);
            return;
        }
        super.onSlotClick(slotIndex, button, actionType, player);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id >= 0 && id < pageCount) {
            setPage(id);
            return true;
        }
        return super.onButtonClick(player, id);
    }

    public void setClientPage(int requestedPage) {
        setPage(requestedPage);
    }

    private void setPage(int requestedPage) {
        page = Math.max(0, Math.min(pageCount - 1, requestedPage));
        pageView.setPage(page);
        syncState();
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        Slot slot = slots.get(slotIndex);
        if (!slot.hasStack()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getStack();
        ItemStack original = stack.copy();
        if (slotIndex < STORAGE_SLOTS) {
            if (!insertItem(stack, STORAGE_SLOTS, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!insertItem(stack, 0, STORAGE_SLOTS, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }
        return original;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        storage.onClose(player);
    }

    public int getPage() {
        return page;
    }

    public int getPageCount() {
        return pageCount;
    }

    public UUID getNetworkId() {
        return networkId;
    }

    public int getNetworkColor() {
        return AstralColors.fromNetwork(networkId);
    }

    public boolean isPortable() {
        return portable;
    }

    public int getEnergy() {
        return powerProperties.get(0);
    }

    public int getMaxEnergy() {
        return Math.max(1, powerProperties.get(1));
    }

    public boolean isOnline() {
        return powerProperties.get(2) != 0;
    }

    private static PropertyDelegate createPowerProperties(AstralStorageState state, ServerWorld world,
                                                           UUID networkId) {
        if (state == null || world == null) {
            return new ArrayPropertyDelegate(3);
        }
        return new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> state.getEnergy(networkId);
                    case 1 -> state.getMaxEnergy(networkId);
                    case 2 -> state.isOnline(networkId, AstralPower.networkTime(world)) ? 1 : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                // Client mirrors these values through vanilla property synchronization.
            }

            @Override
            public int size() {
                return 3;
            }
        };
    }

    private record OpeningData(UUID networkId, int pageCount, boolean portable) {
    }
}
