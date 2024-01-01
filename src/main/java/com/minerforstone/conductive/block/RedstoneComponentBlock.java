package com.minerforstone.conductive.block;

import com.minerforstone.conductive.block.entity.RedstoneComponentBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class RedstoneComponentBlock extends Block implements EntityBlock {

    public RedstoneComponentBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RedstoneComponentBlockEntity(pos, state);
    }

    static final BooleanProperty OPEN = BooleanProperty.create("open");

    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(OPEN);
    }

    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        if (state.getValue(OPEN))
            return Shapes.box(0f, 0f, 0f, 1f, 0.0625f, 1f);
        else
            return Shapes.box(0f, 0f, 0f, 1f, 0.125f, 1f);
    }


    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {

        if (tryOpen(player, level, pos, state)) return InteractionResult.SUCCESS;
        if (tryClose(player, level, pos, state)) return InteractionResult.SUCCESS;

        if (!level.isClientSide && state.getValue(OPEN)) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof RedstoneComponentBlockEntity redstoneComponentBlockEntity){
                int pixelX = (int) getTouchedPixel(hitResult).x;
                int pixelY = (int) getTouchedPixel(hitResult).y;

                player.sendSystemMessage(Component.literal(
                        "I was last touched at: " +
                                redstoneComponentBlockEntity.x +
                                ", " +
                                redstoneComponentBlockEntity.y
                        )
                );
                player.sendSystemMessage(Component.literal("You touched me at: " + pixelX + ", " + pixelY));

                redstoneComponentBlockEntity.x = pixelX;
                redstoneComponentBlockEntity.y = pixelY;

                level.getChunk(pos).setUnsaved(true);
            }
        }

        return InteractionResult.SUCCESS;
    }

    protected Item LID = ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:smooth_stone_slab"));

    protected boolean tryClose(Player player, Level level, BlockPos pos, BlockState state) {
        if (player.getMainHandItem().getItem().equals(LID) && level.getBlockState(pos).getValue(OPEN)) {
            level.setBlock(pos, state.setValue(OPEN, false), 0);
            level.playSound(null, pos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 10, 1.5f);
            if (!player.isCreative()) player.getMainHandItem().shrink(1);
            return true;
        } else return false;
    }

    protected boolean tryOpen(Player player, Level level, BlockPos pos, BlockState state) {
        if (player.getMainHandItem().isEmpty() && !level.getBlockState(pos).getValue(OPEN)) {
            level.setBlock(pos, state.setValue(OPEN, true), 0);
            level.playSound(null, pos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 10, 1.5f);
            if (!player.isCreative()) player.addItem(LID.getDefaultInstance());
            return true;
        } else return false;
    }

    protected Vec2 getTouchedPixel(BlockHitResult hit) {
        Vec3 hitPos = hit.getLocation();
        int x = Math.floorMod((int) Math.round(16 * hitPos.x - 0.5), 16);
        int y = Math.floorMod((int) Math.round(16 * hitPos.z - 0.5), 16);

        return new Vec2(x, y);
    }
}
