package ru.phyllosedis.textario.world;

import ru.phyllosedis.textario.logistics.port.LogisticPort;
import ru.phyllosedis.textario.logistics.port.PortSide;

/**
 * Единый резолвер соседних клеток для портов с учётом поворота.
 * Базовые смещения (без поворота, квадрат 1x1):
 * BACK = вверх (0,-1), FRONT = вниз (0,+1), LEFT = (-1,0), RIGHT = (+1,0).
 * Поворот — четверти по часовой стрелке на экране (ось Y вниз).
 */
public final class PortResolver {

    private PortResolver() {
    }

    public static int[] resolve(PositionComponent position, BuildingComponent building,
                                LogisticPort.Port port, int rotation) {
        int[] base = baseOffset(PortSide.UNDEFINED.getByOrdinal(port.side()), building);
        int[] rotated = rotate(base[0], base[1], rotation);
        return new int[]{position.getX() + rotated[0], position.getY() + rotated[1]};
    }

    public static PortSide rotateSide(PortSide side, int rotation) {
        int[] base = baseOffset(side, 1, 1);
        int[] rotated = rotate(base[0], base[1], rotation);
        if (rotated[0] == 0 && rotated[1] == -1) return PortSide.BACK;
        if (rotated[0] == 0 && rotated[1] == 1) return PortSide.FRONT;
        if (rotated[0] == -1 && rotated[1] == 0) return PortSide.LEFT;
        if (rotated[0] == 1 && rotated[1] == 0) return PortSide.RIGHT;
        return PortSide.UNDEFINED;
    }

    private static int[] baseOffset(PortSide side, BuildingComponent building) {
        return baseOffset(side, building.getWidth(), building.getHeight());
    }

    private static int[] baseOffset(PortSide side, int width, int height) {
        return switch (side) {
            case BACK -> new int[]{0, -1};
            case FRONT -> new int[]{0, height};
            case LEFT -> new int[]{-1, 0};
            case RIGHT -> new int[]{width, 0};
            default -> throw new IllegalArgumentException("Неизвестная сторона порта: " + side);
        };
    }

    private static int[] rotate(int dx, int dy, int rotation) {
        return switch (((rotation % 4) + 4) % 4) {
            case 0 -> new int[]{dx, dy};
            case 1 -> new int[]{-dy, dx};
            case 2 -> new int[]{-dx, -dy};
            case 3 -> new int[]{dy, -dx};
            default -> new int[]{dx, dy};
        };
    }
}
