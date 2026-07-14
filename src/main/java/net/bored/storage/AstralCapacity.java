package net.bored.storage;

public final class AstralCapacity {
    private AstralCapacity() {
    }

    public static int normalizePages(int pages) {
        return Math.max(1, Math.min(AstralStorageState.MAX_PAGES, pages));
    }

    public static int upgradePages(int pages) {
        return Math.min(AstralStorageState.MAX_PAGES, normalizePages(pages) + 1);
    }

    public static int slotsForPages(int pages) {
        return normalizePages(pages) * AstralStorageState.SLOTS_PER_PAGE;
    }
}
