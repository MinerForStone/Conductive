package com.minerforstone.conductive.block.state.properties;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class BlockStateProperties {
    public static BooleanProperty OPEN = BooleanProperty.create("open");
    public static DirectionProperty FACING = DirectionProperty.create("facing");
}
