package com.minerforstone.conductive.block;

import com.minerforstone.conductive.block.entity.RedstoneComponentBlockEntity;
import com.minerforstone.conductive.block.state.properties.BlockStateProperties;
import com.minerforstone.conductive.util.DirectionMathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RedstoneComponentBlock extends HorizontalDirectionalBlock implements EntityBlock {
    static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    static final int MAX_BOUND = 15;
    static final int MIN_BOUND = 0;

    public RedstoneComponentBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(OPEN, true).setValue(FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new RedstoneComponentBlockEntity(pos, state);
    }

    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(OPEN, FACING);
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        if (state.getValue(OPEN))
            return Shapes.box(0f, 0f, 0f, 1f, 0.0625f, 1f);
        else
            return Shapes.box(0f, 0f, 0f, 1f, 0.125f, 1f);
    }

    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (level.isClientSide) return;
        if (tryOpen(player, level, pos, state)) {
            BlockState newState = state.setValue(OPEN, true);
            level.setBlock(pos, newState, 0);
            level.markAndNotifyBlock(pos, (LevelChunk) level.getChunk(pos), newState, newState, 3, 3);
        }

    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide) return InteractionResult.PASS;

        if (tryClose(player, level, pos, state)) return InteractionResult.CONSUME;

        if (state.getValue(OPEN)) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof RedstoneComponentBlockEntity redstoneComponentBlockEntity){

                Vec2 coordinates = DirectionMathHelper.rotateCoordinatesFromNorth(
                        new Vec2(getTouchedPixel(hitResult).x, getTouchedPixel(hitResult).y),
                        state.getValue(FACING),
                        MIN_BOUND,
                        MAX_BOUND
                );

                int pixelX = (int) coordinates.x;
                int pixelY = (int) coordinates.y;

                if (player.getMainHandItem().isEmpty()) {
                    Item giveItem = redstoneComponentBlockEntity.removePart(pixelX, pixelY);
                    if (giveItem != null && !player.isCreative())
                        player.addItem(new ItemStack(giveItem));

                    level.getChunk(pos).setUnsaved(true);
                    return InteractionResult.CONSUME;
                }

                if (player.getMainHandItem().is(ItemTags.create(new ResourceLocation("conductive:component_parts")))) {
                    ItemStack item = player.getMainHandItem();
                    if (redstoneComponentBlockEntity.addPart(pixelX, pixelY, ForgeRegistries.ITEMS.getKey(item.getItem()).toString()) && !player.isCreative())
                        item.shrink(1);

                    level.getChunk(pos).setUnsaved(true);
                    return InteractionResult.CONSUME;
                }
            }
        }
        return InteractionResult.FAIL;
    }

    protected boolean tryClose(Player player, Level level, BlockPos pos, BlockState state) {
        Item LID = ForgeRegistries.ITEMS.getValue(new ResourceLocation("conductive:redstone_component_lid"));
        if (player.getMainHandItem().getItem().equals(LID) && level.getBlockState(pos).getValue(OPEN)) {
            level.setBlock(pos, state.setValue(OPEN, false), 0);
            level.playSound(null, pos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 10, 1.5f);
            if (!player.isCreative()) player.getMainHandItem().shrink(1);
            return true;
        } else return false;
    }

    protected boolean tryOpen(Player player, Level level, BlockPos pos, BlockState state) {
        Item LID = ForgeRegistries.ITEMS.getValue(new ResourceLocation("conductive:redstone_component_lid"));
        if (!level.getBlockState(pos).getValue(OPEN) && player.isCrouching()) {
            level.setBlock(pos, state.setValue(OPEN, true), 0);
            level.playSound(null, pos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 10, 1.5f);
            if (!player.isCreative()) Block.popResource(level, pos, LID.getDefaultInstance());
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
