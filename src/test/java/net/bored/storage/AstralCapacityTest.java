package net.bored.storage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class AstralCapacityTest {
    @Test
    void invalidSavedValuesAreClamped() {
        assertEquals(1, AstralCapacity.normalizePages(0));
        assertEquals(4, AstralCapacity.normalizePages(99));
    }

    @Test
    void upgradesStopAtFourPages() {
        assertEquals(2, AstralCapacity.upgradePages(1));
        assertEquals(4, AstralCapacity.upgradePages(4));
    }

    @Test
    void fourPagesExposeTwoHundredSixteenSlots() {
        assertEquals(216, AstralCapacity.slotsForPages(4));
    }
}
