package com.you.hidegrass.mixin;

import com.you.hidegrass.HideGrassConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BlockModelRenderer.class)
public class BlockModelRendererMixin {

    // render(BlockRenderView, List<BlockModelPart>, BlockState, BlockPos, MatrixStack, VertexConsumer, boolean, int)V
    @Inject(method = "render(Lnet/minecraft/world/BlockRenderView;Ljava/util/List;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;ZI)V",
            at = @At("HEAD"), cancellable = true)
    private void hideTallPlantsList(BlockRenderView world,
                                    List<BlockModelPart> parts,
                                    BlockState state,
                                    BlockPos pos,
                                    MatrixStack matrices,
                                    VertexConsumer vertexConsumer,
                                    boolean cull,
                                    int overlay,
                                    CallbackInfo ci) {

        if (!HideGrassConfig.isEnabled()) return;

        if (state.isOf(Blocks.TALL_GRASS)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderSmooth(Lnet/minecraft/world/BlockRenderView;Ljava/util/List;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;ZI)V",
            at = @At("HEAD"), cancellable = true)
    private void hideTallPlantsSmooth(BlockRenderView world,
                                     List<BlockModelPart> parts,
                                     BlockState state,
                                     BlockPos pos,
                                     MatrixStack matrices,
                                     VertexConsumer vertexConsumer,
                                     boolean cull,
                                     int overlay,
                                     CallbackInfo ci) {

        if (!HideGrassConfig.isEnabled()) return;

        if (state.isOf(Blocks.TALL_GRASS)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderFlat(Lnet/minecraft/world/BlockRenderView;Ljava/util/List;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;ZI)V",
            at = @At("HEAD"), cancellable = true)
    private void hideTallPlantsFlat(BlockRenderView world,
                                    List<BlockModelPart> parts,
                                    BlockState state,
                                    BlockPos pos,
                                    MatrixStack matrices,
                                    VertexConsumer vertexConsumer,
                                    boolean cull,
                                    int overlay,
                                    CallbackInfo ci) {

        if (!HideGrassConfig.isEnabled()) return;

        if (state.isOf(Blocks.TALL_GRASS)) {
            ci.cancel();
        }
    }
}
