package net.bored.storage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AstralPowerTest {
    @Test
    void energyCapacityGrowsWithArchivePages() {
        assertEquals(1200, AstralPower.capacityForPages(1));
        assertEquals(1800, AstralPower.capacityForPages(2));
        assertEquals(3000, AstralPower.capacityForPages(4));
    }

    @Test
    void invalidPageCountsNormalizeToFirstTier() {
        assertEquals(AstralPower.capacityForPages(1), AstralPower.capacityForPages(0));
    }
}
