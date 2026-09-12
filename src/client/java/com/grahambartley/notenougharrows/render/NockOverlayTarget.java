package com.grahambartley.notenougharrows.render;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;

public record NockOverlayTarget(
    ModelTransformationMode mode,
    boolean leftHanded,
    MatrixStack matrices,
    VertexConsumerProvider vertexConsumers,
    int light,
    int overlay) {}
