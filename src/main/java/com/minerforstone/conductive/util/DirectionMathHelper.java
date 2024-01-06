package com.minerforstone.conductive.util;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec2;

public class DirectionMathHelper {
    public static Vec2 rotateCoordinatesFromNorth(Vec2 vector, Direction direction, float min, float max) {
        float x = vector.x;
        float y = vector.y;

        return switch (direction) {
            default -> new Vec2(min + x, min + y);
            case SOUTH -> new Vec2(max - x, max - y);
            case EAST -> new Vec2(min + y, max - x);
            case WEST -> new Vec2(max - y, min + x);
        };
    }
}
