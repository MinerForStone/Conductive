package com.minerforstone.conductive.client.renderer.blockentity;

import com.minerforstone.conductive.block.RedstoneComponentBlock;
import com.minerforstone.conductive.block.entity.RedstoneComponentBlockEntity;
import com.minerforstone.conductive.client.model.TraceModel;
import com.minerforstone.conductive.util.DirectionMathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

public class RedstoneComponentRenderer implements BlockEntityRenderer<RedstoneComponentBlockEntity> {
    private static TraceModel model;
    private static final float MAX_BOUND = 15/16F;
    private static final float MIN_BOUND = 0;

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
                Vec2 coordinates = DirectionMathHelper.rotateCoordinatesFromNorth(
                        new Vec2(part.getInt("y")/16.0F, part.getInt("x")/16.0F),
                        blockEntity.getBlockState().getValue(RedstoneComponentBlock.FACING),
                        MIN_BOUND,
                        MAX_BOUND
                        );
                poseStack.translate(coordinates.y, 1.0/16.0F, coordinates.x);
                model.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.entitySolid(new ResourceLocation("minecraft:textures/block/redstone_block.png"))), combinedLight, combinedOverlay, 1, 1, 1, 1);
                poseStack.popPose();
            }
        }
    }
}
