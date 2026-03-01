package com.you.hidegrass.mixin;

import com.you.hidegrass.HideGrassConfig;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.state.OutlineRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class BlockOutlineMixin {

    // Exact signature from mixin error log on 1.21.11:
    // drawBlockOutline(MatrixStack, VertexConsumer, double, double, double, OutlineRenderState, int, float)
    @Inject(
            method = "drawBlockOutline(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;DDDLnet/minecraft/client/render/state/OutlineRenderState;IF)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void hideOutline(
            MatrixStack matrices,
            VertexConsumer vertexConsumer,
            double camX,
            double camY,
            double camZ,
            OutlineRenderState outlineRenderState,
            int outlineColor,
            float tickDelta,
            CallbackInfo ci
    ) {
        if (!HideGrassConfig.isEnabled()) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return;

        HitResult hr = mc.crosshairTarget;
        if (!(hr instanceof BlockHitResult hit)) return;

        BlockState state = mc.world.getBlockState(hit.getBlockPos());
        String id = Registries.BLOCK.getId(state.getBlock()).toString();

        if (HideGrassConfig.shouldHide(id)) {
            ci.cancel();
        }
    }
}