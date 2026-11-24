package com.premclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PremClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PremConfig.load();

        // On-screen modules
        HudElementRegistry.addLast(Identifier.of("premclient", "hud"), PremHud::render);

        // A "PREM" button on the title screen and pause menu (no keyboard needed - good for phones)
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof TitleScreen || screen instanceof GameMenuScreen) {
                Screens.getButtons(screen).add(
                        ButtonWidget.builder(Text.literal("PREM"), b -> client.setScreen(new PremMenuScreen(screen)))
                                .dimensions(6, 6, 56, 24).build());
            }
        });
    }
}
