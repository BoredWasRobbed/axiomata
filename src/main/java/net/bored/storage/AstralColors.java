package net.bored.storage;

import java.util.UUID;

public final class AstralColors {
    private AstralColors() {
    }

    public static int fromNetwork(UUID id) {
        long value = id.getMostSignificantBits() ^ id.getLeastSignificantBits();
        int red = 112 + (int) (value >>> 16 & 0x7F);
        int green = 112 + (int) (value >>> 32 & 0x7F);
        int blue = 150 + (int) (value >>> 48 & 0x69);
        return red << 16 | green << 8 | Math.min(255, blue);
    }

    public static String shortId(UUID id) {
        return id.toString().substring(0, 8).toUpperCase();
    }
}
