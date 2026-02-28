package com.you.hidegrass.mixin;

import com.you.hidegrass.HideGrassConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.state.OutlineRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(WorldRenderer.class)
public class BlockOutlineMixin {

    @Inject(method = "drawBlockOutline", at = @At("HEAD"), cancellable = true)
    private void hideGrassOutline(
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
        // אם המוד כבוי — לא עושים כלום
        if (!HideGrassConfig.isEnabled()) return;

        // בדיקות בטיחות
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null) return;
        if (mc.world == null) return;

        // קבל את ה־hitResult מה‑crosshair (אם יש)
        HitResult hr = mc.crosshairTarget;
        if (!(hr instanceof BlockHitResult)) return;
        BlockHitResult hit = (BlockHitResult) hr;
        if (hit == null) return;

        BlockPos pos = hit.getBlockPos();
        if (pos == null) return;

        BlockState state = mc.world.getBlockState(pos);
        if (state == null) return;

        if (state.isOf(Blocks.TALL_GRASS) || state.isOf(Blocks.LARGE_FERN)) {
            ci.cancel();
        }
    }
}
