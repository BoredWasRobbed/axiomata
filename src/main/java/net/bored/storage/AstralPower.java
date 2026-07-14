package net.bored.storage;

import net.minecraft.server.world.ServerWorld;

public final class AstralPower {
    public static final int ANCHOR_OPEN_COST = 5;
    public static final int KEY_OPEN_COST = 20;
    public static final int STORAGE_ACTION_COST = 1;
    public static final int CELL_UPGRADE_COST = 250;
    public static final int ONLINE_GRACE_TICKS = 60;

    private static final int BASE_CAPACITY = 1200;
    private static final int CAPACITY_PER_EXTRA_PAGE = 600;

    private AstralPower() {
    }

    public static int capacityForPages(int pages) {
        return BASE_CAPACITY + (Math.max(1, pages) - 1) * CAPACITY_PER_EXTRA_PAGE;
    }

    public static long networkTime(ServerWorld world) {
        return world.getServer().getOverworld().getTime();
    }
}
