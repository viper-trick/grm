package com.you.hidegrass.mixin;

import com.you.hidegrass.HideGrassConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BlockRenderManager.class)
public class BlockRenderManagerMixin {

    /**
     * When the game asks for the model of grass/fern, return the model for AIR instead.
     * That way chunk building and any other render path gets no geometry for these blocks.
     */
    @ModifyVariable(
            method = "getModel(Lnet/minecraft/block/BlockState;)Lnet/minecraft/client/render/model/BlockStateModel;",
            at = @At("HEAD"),
            argsOnly = true
    )
    private BlockState hideGrassModel(BlockState state) {
        if (!HideGrassConfig.isEnabled()) return state;
        if (state.isOf(Blocks.TALL_GRASS)) {
            return Blocks.AIR.getDefaultState();
        }
        return state;
    }

    @Inject(
            method = "renderBlock(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;ZLjava/util/List;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void hideGrassRender(BlockState state, BlockPos pos, BlockRenderView world,
                                MatrixStack matrices, VertexConsumer vertexConsumer,
                                boolean cull, List<BlockModelPart> parts, CallbackInfo ci) {

        if (!HideGrassConfig.isEnabled()) return;

        if (state.isOf(Blocks.TALL_GRASS)) {
            ci.cancel();
        }
    }
}
