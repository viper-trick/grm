package com.you.hidegrass;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.model.BlockStateModel;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import com.you.hidegrass.model.HidePlantModel;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class HideGrassMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        // Model override: make tall grass invisible when config is enabled.
        ModelLoadingPlugin.register(context ->
                context.modifyBlockModelAfterBake().register((BlockStateModel model,
                                                              net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier.AfterBakeBlock.Context ctx) -> {
                    if (ctx.state() != null && (ctx.state().isOf(Blocks.TALL_GRASS) || ctx.state().isOf(Blocks.LARGE_FERN))) {
                        return new HidePlantModel(model);
                    }
                    return model;
                })
        );

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            var root = literal("grm")
                    .then(literal("on").executes(ctx -> {
                        HideGrassConfig.setEnabled(true);
                        reloadWorld();
                        send("HideGrass enabled");
                        return 1;
                    }))
                    .then(literal("off").executes(ctx -> {
                        HideGrassConfig.setEnabled(false);
                        reloadWorld();
                        send("HideGrass disabled");
                        return 1;
                    }))
                    .then(literal("toggle").executes(ctx -> {
                        HideGrassConfig.toggle();
                        reloadWorld();
                        send("HideGrass toggled: " + (HideGrassConfig.isEnabled() ? "on" : "off"));
                        return 1;
                    }))
                    .then(literal("status").executes(ctx -> {
                        send("HideGrass is " + (HideGrassConfig.isEnabled() ? "enabled" : "disabled"));
                        return 1;
                    }));

            dispatcher.register(root);
        });
    }

    private static void reloadWorld() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.worldRenderer != null) {
            client.worldRenderer.reload();
        }
    }

    private static void send(String msg) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(Text.literal("[grm] " + msg), false);
        } else {
            System.out.println("[grm] " + msg);
        }
    }
}
