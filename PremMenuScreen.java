package com.premclient;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Touch-friendly module menu. Scrolling works THREE ways so it never "gets stuck":
 * mouse wheel, the big up/down buttons, and the list simply re-lays itself out every change.
 */
public class PremMenuScreen extends Screen {
    private static final int ROW = 28;      // tall rows = easy to tap
    private static final int GAP = 4;

    private final Screen parent;
    private final List<ButtonWidget> rows = new ArrayList<>();
    private int scroll = 0;                 // pixels
    private int panelX, panelY, panelW, panelH, listTop, listBottom;

    public PremMenuScreen(Screen parent) {
        super(Text.literal("PREM Client"));
        this.parent = parent;
    }

    private int contentHeight() {
        return (PremConfig.Module.values().length + 1) * (ROW + GAP);
    }

    private int maxScroll() {
        return Math.max(0, contentHeight() - (listBottom - listTop));
    }

    @Override
    protected void init() {
        rows.clear();
        panelW = Math.min(this.width - 24, 280);
        panelH = this.height - 24;
        panelX = (this.width - panelW) / 2;
        panelY = 12;
        listTop = panelY + 34;
        listBottom = panelY + panelH - 40;
        int bw = panelW - 24 - 30;           // leave room for the scroll buttons
        int bx = panelX + 12;

        for (PremConfig.Module m : PremConfig.Module.values()) {
            ButtonWidget b = ButtonWidget.builder(label(m), btn -> {
                PremConfig.toggle(m);
                btn.setMessage(label(m));
            }).dimensions(bx, listTop, bw, ROW).build();
            rows.add(b);
            this.addDrawableChild(b);
        }
        ButtonWidget panels = ButtonWidget.builder(panelLabel(), btn -> {
            PremConfig.togglePanels();
            btn.setMessage(panelLabel());
        }).dimensions(bx, listTop, bw, ROW).build();
        rows.add(panels);
        this.addDrawableChild(panels);

        int sx = panelX + panelW - 12 - 26;
        this.addDrawableChild(ButtonWidget.builder(Text.literal("^"), b -> scrollBy(-(ROW + GAP) * 2))
                .dimensions(sx, listTop, 26, 40).build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("v"), b -> scrollBy((ROW + GAP) * 2))
                .dimensions(sx, listBottom - 40, 26, 40).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> this.close())
                .dimensions(panelX + 12, panelY + panelH - 32, panelW - 24, 24).build());

        layout();
    }

    private Text label(PremConfig.Module m) {
        return Text.literal(m.label + ": " + (PremConfig.on(m) ? "ON" : "OFF"));
    }

    private Text panelLabel() {
        return Text.literal("Glass panels: " + (PremConfig.panels() ? "ON" : "OFF"));
    }

    private void scrollBy(int d) {
        scroll = Math.max(0, Math.min(maxScroll(), scroll + d));
        layout();
    }

    /** Re-position every row; hide the ones outside the visible window. */
    private void layout() {
        int y = listTop - scroll;
        for (ButtonWidget b : rows) {
            b.setY(y);
            boolean visible = y >= listTop - 1 && y + ROW <= listBottom + 1;
            b.visible = visible;
            b.active = visible;
            y += ROW + GAP;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollBy((int) (-verticalAmount * 20));
        return true;
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        Glass.panel(context, panelX, panelY, panelW, panelH, 0x90182640);
        int tw = this.textRenderer.getWidth("PREM CLIENT");
        context.drawTextWithShadow(this.textRenderer, "PREM CLIENT", panelX + (panelW - tw) / 2, panelY + 12, 0xFFFFFFFF);
    }

    @Override
    public void close() {
        if (this.client != null) this.client.setScreen(parent);
    }
}
