package com.minerforstone.conductive.client.renderer.blockentity;

import com.minerforstone.conductive.block.entity.RedstoneComponentBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.jetbrains.annotations.NotNull;

public class RedstoneComponentRenderer implements BlockEntityRenderer<RedstoneComponentBlockEntity> {
    public RedstoneComponentRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(@NotNull RedstoneComponentBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {

    }
}
