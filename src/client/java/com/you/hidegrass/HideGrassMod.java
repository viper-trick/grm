package com.you.hidegrass;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.text.Text;

import com.you.hidegrass.model.HidePlantModel;

public class HideGrassMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        // Make tall grass invisible when config is enabled
        ModelLoadingPlugin.register(context ->
                context.modifyBlockModelAfterBake().register((BlockStateModel model,
                                                              net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier.AfterBakeBlock.Context ctx) -> {
                    if (ctx.state().isOf(Blocks.TALL_GRASS) || ctx.state().isOf(Blocks.LARGE_FERN)) {
                        return new HidePlantModel(model);
                    }
                    return model;
                })
        );

        // Register client commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("grm")
                            .then(ClientCommandManager.literal("on").executes(ctx -> {
                                HideGrassConfig.setEnabled(true);
                                refresh();
                                send("HideGrass enabled");
                                return 1;
                            }))
                            .then(ClientCommandManager.literal("off").executes(ctx -> {
                                HideGrassConfig.setEnabled(false);
                                refresh();
                                send("HideGrass disabled");
                                return 1;
                            }))
                            .then(ClientCommandManager.literal("toggle").executes(ctx -> {
                                HideGrassConfig.toggle();
                                refresh();
                                send("HideGrass toggled: " + (HideGrassConfig.isEnabled() ? "on" : "off"));
                                return 1;
                            }))
                            .then(ClientCommandManager.literal("status").executes(ctx -> {
                                send("HideGrass is " + (HideGrassConfig.isEnabled() ? "enabled" : "disabled"));
                                return 1;
                            }))
                            .then(ClientCommandManager.literal("gui").executes(ctx -> {
                                MinecraftClient client = MinecraftClient.getInstance();
                                client.execute(() -> {
                                    try {
                                        client.setScreen(HideGrassConfigScreen.create(null));
                                    } catch (Throwable t) {
                                        // הודעה מינימלית לשחקן בלבד
                                        if (client.player != null) {
                                            client.player.sendMessage(Text.literal("[grm] Failed to open config"), false);
                                        }
                                        // לא מדפיסים stacktrace בפרודקשן
                                    }
                                });
                                return 1;
                            }))


            );
        });
    }

    // Refresh world rendering after config change
    public static void refresh() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.worldRenderer != null) {
            client.worldRenderer.reload();
        }
    }

    // Send chat message
    private static void send(String msg) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(Text.literal("[grm] " + msg), false);
        }
    }
}
