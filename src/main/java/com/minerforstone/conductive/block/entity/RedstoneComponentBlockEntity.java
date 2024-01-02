package com.minerforstone.conductive.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import static com.minerforstone.conductive.Conductive.REDSTONE_COMPONENT_ENTITY;

public class RedstoneComponentBlockEntity extends BlockEntity {
    public RedstoneComponentBlockEntity(BlockPos pos, BlockState state) {
        super(REDSTONE_COMPONENT_ENTITY.get(), pos, state);
    }

    protected ListTag parts = new ListTag();

    public void addPart(int x, int y, String id) {
        if (getPartId(x, y) == -1) {
            parts.add(new CompoundTag() {{
                putString("id", id);
                putInt("x", x);
                putInt("y", y);
            }});
        }
    }

    public void removePart(int x, int y) {
        int removeId = getPartId(x, y);
        if (removeId != -1) parts.remove(removeId);
    }

    public int getPartId(int x, int y) {
        for (int i = 0; i < parts.size(); i++) {
            CompoundTag current_part = parts.getCompound(i);
            if (current_part.getInt("x") == x && current_part.getInt("y") == y) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);

        compoundTag.put("Parts", parts);
    }

    @Override
    public void load(@NotNull CompoundTag compoundTag) {
        super.load(compoundTag);

        parts = compoundTag.getList("Parts", 10);
    }
}