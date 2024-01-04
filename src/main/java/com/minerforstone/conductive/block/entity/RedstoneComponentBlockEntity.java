package com.minerforstone.conductive.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;

import static com.minerforstone.conductive.Conductive.REDSTONE_COMPONENT_ENTITY;

public class RedstoneComponentBlockEntity extends BlockEntity {
    protected ListTag parts = new ListTag();
    public RedstoneComponentBlockEntity(BlockPos pos, BlockState state) {
        super(REDSTONE_COMPONENT_ENTITY.get(), pos, state);
    }

    public ListTag getParts() {
        return parts;
    }
    public boolean isOpen() {
        return this.getBlockState().getValue(BooleanProperty.create("open"));
    }

    public void addPart(int x, int y, String id) {
        if (getPartId(x, y) == -1) {
            parts.add(new CompoundTag() {{
                putString("id", id);
                putInt("x", x);
                putInt("y", y);
            }});
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);
        }
    }

    public void removePart(int x, int y) {
        int removeId = getPartId(x, y);
        if (removeId != -1) {
            parts.remove(removeId);
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);
        }
    }

    public int getPartId(int x, int y) {
        for (int i = 0; i < parts.size(); i++) {
            CompoundTag currentPart = parts.getCompound(i);
            if (currentPart.getInt("x") == x && currentPart.getInt("y") == y) {
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

    @Override
    @NotNull
    public CompoundTag getUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();
        saveAdditional(compoundTag);
        return compoundTag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}