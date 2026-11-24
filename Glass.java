package com.premclient;

import net.minecraft.client.gui.DrawContext;

/** Small helpers that draw the "liquid glass" look with plain rectangles. */
public final class Glass {
    private Glass() {}

    /** Frosted translucent panel with a rim and a soft highlight on top. */
    public static void panel(DrawContext c, int x, int y, int w, int h, int tint) {
        int fill = tint;                       // 0xAARRGGBB
        // body with chamfered corners (looks rounded)
        c.fill(x + 1, y, x + w - 1, y + h, fill);
        c.fill(x, y + 1, x + w, y + h - 1, fill);
        // soft highlight on the upper part
        c.fill(x + 2, y + 1, x + w - 2, y + Math.max(2, h / 3), 0x22FFFFFF);
        // rim
        int rim = 0x80FFFFFF;
        c.fill(x + 2, y, x + w - 2, y + 1, rim);              // top
        c.fill(x + 2, y + h - 1, x + w - 2, y + h, 0x40FFFFFF); // bottom
        c.fill(x, y + 2, x + 1, y + h - 2, 0x55FFFFFF);       // left
        c.fill(x + w - 1, y + 2, x + w, y + h - 2, 0x55FFFFFF); // right
    }

    public static void panel(DrawContext c, int x, int y, int w, int h) {
        panel(c, x, y, w, h, 0x7014203A);
    }
}
