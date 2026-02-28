package com.you.hidegrass.model;

import com.you.hidegrass.HideGrassConfig;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.random.Random;

import java.util.Collections;
import java.util.List;

public class HidePlantModel implements BlockStateModel {

    private final BlockStateModel delegate;

    public HidePlantModel(BlockStateModel delegate) {
        this.delegate = delegate;
    }

    @Override
    public void addParts(Random random, List<BlockModelPart> parts) {
        if (!HideGrassConfig.isEnabled()) {
            delegate.addParts(random, parts);
        }
    }

    @Override
    public List<BlockModelPart> getParts(Random random) {
        if (!HideGrassConfig.isEnabled()) {
            return delegate.getParts(random);
        }
        return Collections.emptyList();
    }

    @Override
    public Sprite particleSprite() {
        return delegate.particleSprite();
    }
}

