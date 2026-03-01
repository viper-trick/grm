package com.you.hidegrass;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;

public class HideGrassMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        HideGrassConfig.init();

        // Register /grm gui command to open the custom config screen
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("grm")
                            .then(ClientCommandManager.literal("gui").executes(ctx -> {
                                MinecraftClient mc = MinecraftClient.getInstance();
                                // Use mc.send() to open screens from command context (runs on render thread)
                                mc.send(() -> mc.setScreen(HideGrassConfigScreen.create(null)));
                                return 1;
                            }))
            );
        });
    }
}