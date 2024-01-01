package com.minerforstone.conductive.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import static com.minerforstone.conductive.Conductive.REDSTONE_COMPONENT_ENTITY;

public class RedstoneComponentBlockEntity extends BlockEntity {

    public int x = 0;
    public int y = 0;

    public RedstoneComponentBlockEntity(BlockPos pos, BlockState state) {
        super(REDSTONE_COMPONENT_ENTITY.get(), pos, state);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag compoundTag) {
        compoundTag.putInt("touched_x", x);
        compoundTag.putInt("touched_y", y);
        super.saveAdditional(compoundTag);
    }

    @Override
    public void load(@NotNull CompoundTag compoundTag) {
        super.load(compoundTag);
        x = compoundTag.getInt("touched_x");
        y = compoundTag.getInt("touched_y");
    }
}
