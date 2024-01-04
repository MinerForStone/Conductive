package com.minerforstone.conductive.client.renderer.blockentity;

import com.minerforstone.conductive.block.entity.RedstoneComponentBlockEntity;
import com.minerforstone.conductive.client.model.TraceModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;

public class RedstoneComponentRenderer implements BlockEntityRenderer<RedstoneComponentBlockEntity> {
    private static TraceModel model;

    public RedstoneComponentRenderer(BlockEntityRendererProvider.Context context) {
        model = new TraceModel(context.bakeLayer(TraceModel.LAYER_LOCATION));
    }

    @Override
    public void render(RedstoneComponentBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        if (blockEntity.isOpen()) {
            ListTag parts = blockEntity.getParts();
            for (int i = 0; i < parts.size(); i++) {
                CompoundTag part = parts.getCompound(i);
                poseStack.pushPose();
                poseStack.translate(part.getInt("x")/16.0F, 1/16.0F, part.getInt("y")/16.0F);
                model.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.entitySolid(new ResourceLocation("minecraft:textures/block/redstone_block.png"))), combinedLight, combinedOverlay, 1, 1, 1, 1);
                poseStack.popPose();
            }
        }
    }
}
