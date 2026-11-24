package com.premclient;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.Deque;

/** Draws the on-screen modules. */
public final class PremHud {
    private static final Deque<Long> LEFT = new ArrayDeque<>();
    private static final Deque<Long> RIGHT = new ArrayDeque<>();
    private static boolean leftWas, rightWas;

    private PremHud() {}

    private static void pollClicks(MinecraftClient mc) {
        long handle = mc.getWindow().getHandle();
        boolean l = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean r = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        long now = System.currentTimeMillis();
        if (l && !leftWas) LEFT.addLast(now);
        if (r && !rightWas) RIGHT.addLast(now);
        leftWas = l;
        rightWas = r;
        while (!LEFT.isEmpty() && now - LEFT.peekFirst() > 1000) LEFT.pollFirst();
        while (!RIGHT.isEmpty() && now - RIGHT.peekFirst() > 1000) RIGHT.pollFirst();
    }

    private static void line(DrawContext c, MinecraftClient mc, String text, int x, int y) {
        int w = mc.textRenderer.getWidth(text) + 8;
        if (PremConfig.panels()) Glass.panel(c, x, y, w, 14, 0x60101828);
        c.drawTextWithShadow(mc.textRenderer, text, x + 4, y + 3, 0xFFFFFFFF);
    }

    private static void key(DrawContext c, MinecraftClient mc, String label, boolean down, int x, int y, int w, int h) {
        Glass.panel(c, x, y, w, h, down ? 0xB0CFEFFF : 0x60101828);
        int tw = mc.textRenderer.getWidth(label);
        c.drawTextWithShadow(mc.textRenderer, label, x + (w - tw) / 2, y + (h - 8) / 2, down ? 0xFF102030 : 0xFFFFFFFF);
    }

    public static void render(DrawContext c, RenderTickCounter tick) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options.hudHidden) return;
        ClientPlayerEntity p = mc.player;
        pollClicks(mc);

        int x = 4, y = 4;
        if (PremConfig.on(PremConfig.Module.FPS)) { line(c, mc, mc.getCurrentFps() + " FPS", x, y); y += 16; }
        if (PremConfig.on(PremConfig.Module.CPS)) { line(c, mc, LEFT.size() + " | " + RIGHT.size() + " CPS", x, y); y += 16; }
        if (PremConfig.on(PremConfig.Module.PING)) {
            int ping = 0;
            if (mc.getNetworkHandler() != null) {
                PlayerListEntry e = mc.getNetworkHandler().getPlayerListEntry(p.getUuid());
                if (e != null) ping = e.getLatency();
            }
            line(c, mc, ping + " ms", x, y); y += 16;
        }
        if (PremConfig.on(PremConfig.Module.COORDS)) {
            line(c, mc, String.format("XYZ %.1f / %.1f / %.1f", p.getX(), p.getY(), p.getZ()), x, y); y += 16;
        }
        if (PremConfig.on(PremConfig.Module.DIRECTION)) {
            line(c, mc, p.getHorizontalFacing().asString(), x, y); y += 16;
        }

        if (PremConfig.on(PremConfig.Module.KEYSTROKES)) {
            int kx = 4, ky = y + 4, s = 20, g = 2;
            key(c, mc, "W", mc.options.forwardKey.isPressed(), kx + s + g, ky, s, s);
            key(c, mc, "A", mc.options.leftKey.isPressed(), kx, ky + s + g, s, s);
            key(c, mc, "S", mc.options.backKey.isPressed(), kx + s + g, ky + s + g, s, s);
            key(c, mc, "D", mc.options.rightKey.isPressed(), kx + 2 * (s + g), ky + s + g, s, s);
            key(c, mc, "LMB", mc.options.attackKey.isPressed(), kx, ky + 2 * (s + g), (s * 3 + 2 * g) / 2 - 1, s);
            key(c, mc, "RMB", mc.options.useKey.isPressed(), kx + (s * 3 + 2 * g) / 2 + 1, ky + 2 * (s + g), (s * 3 + 2 * g) / 2 - 1, s);
            key(c, mc, "SPACE", mc.options.jumpKey.isPressed(), kx, ky + 3 * (s + g), s * 3 + 2 * g, 14);
        }

        if (PremConfig.on(PremConfig.Module.ARMOR)) {
            EquipmentSlot[] slots = { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };
            int ax = c.getScaledWindowWidth() - 70;
            int ay = c.getScaledWindowHeight() / 2 - 40;
            for (EquipmentSlot slot : slots) {
                ItemStack st = p.getEquippedStack(slot);
                if (!st.isEmpty()) {
                    c.drawItem(st, ax, ay);
                    if (st.isDamageable()) {
                        int left = st.getMaxDamage() - st.getDamage();
                        c.drawTextWithShadow(mc.textRenderer, String.valueOf(left), ax + 20, ay + 4, 0xFFFFFFFF);
                    }
                }
                ay += 20;
            }
        }
    }
}
