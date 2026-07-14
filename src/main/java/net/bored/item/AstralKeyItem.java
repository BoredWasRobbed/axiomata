package net.bored.item;

import net.bored.screen.AstralStorageScreenHandler;
import net.bored.storage.AstralColors;
import net.bored.storage.AstralInventory;
import net.bored.storage.AstralStorageState;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public final class AstralKeyItem extends Item {
    private static final String NETWORK_TAG = "AstralNetwork";

    public AstralKeyItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (user.isSneaking()) {
            if (!world.isClient && hasNetwork(stack)) {
                clearNetwork(stack);
                user.sendMessage(Text.translatable("message.axiomata.key_cleared")
                        .formatted(Formatting.GRAY), true);
            }
            return TypedActionResult.success(stack, world.isClient);
        }
        if (!hasNetwork(stack)) {
            if (!world.isClient) {
                user.sendMessage(Text.translatable("message.axiomata.key_unbound")
                        .formatted(Formatting.GRAY), true);
            }
            return TypedActionResult.success(stack, world.isClient);
        }
        if (!world.isClient && user instanceof ServerPlayerEntity serverPlayer
                && world instanceof ServerWorld serverWorld) {
            UUID networkId = getNetwork(stack);
            AstralStorageState storage = AstralStorageState.get(serverWorld);
            storage.ensureVault(networkId);
            serverPlayer.openHandledScreen(new PortableFactory(serverWorld, networkId));
        }
        return TypedActionResult.success(stack, world.isClient);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return hasNetwork(stack) || super.hasGlint(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip,
                              TooltipContext context) {
        if (hasNetwork(stack)) {
            tooltip.add(Text.translatable("tooltip.axiomata.key_network",
                    AstralColors.shortId(getNetwork(stack))).formatted(Formatting.LIGHT_PURPLE));
            tooltip.add(Text.translatable("tooltip.axiomata.key_use").formatted(Formatting.GRAY));
        } else {
            tooltip.add(Text.translatable("tooltip.axiomata.key_unbound").formatted(Formatting.DARK_GRAY));
        }
    }

    public static boolean hasNetwork(ItemStack stack) {
        return stack.hasNbt() && stack.getNbt().containsUuid(NETWORK_TAG);
    }

    public static UUID getNetwork(ItemStack stack) {
        return stack.getNbt().getUuid(NETWORK_TAG);
    }

    public static void setNetwork(ItemStack stack, UUID networkId) {
        stack.getOrCreateNbt().putUuid(NETWORK_TAG, networkId);
    }

    public static void clearNetwork(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt != null) {
            nbt.remove(NETWORK_TAG);
        }
    }

    private static final class PortableFactory implements ExtendedScreenHandlerFactory {
        private final ServerWorld world;
        private final UUID networkId;

        private PortableFactory(ServerWorld world, UUID networkId) {
            this.world = world;
            this.networkId = networkId;
        }

        @Override
        public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buffer) {
            buffer.writeUuid(networkId);
            buffer.writeVarInt(AstralStorageState.get(world).getPages(networkId));
            buffer.writeBoolean(true);
        }

        @Override
        public Text getDisplayName() {
            return Text.translatable("screen.axiomata.astral_archive");
        }

        @Nullable
        @Override
        public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
            AstralStorageState storage = AstralStorageState.get(world);
            return new AstralStorageScreenHandler(syncId, playerInventory,
                    new AstralInventory(storage, networkId), networkId, storage.getPages(networkId),
                    ScreenHandlerContext.EMPTY, true);
        }
    }
}
