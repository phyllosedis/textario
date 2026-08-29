package ru.phyllosedis.textario.world;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.phyllosedis.textario.engine.spring.ecs.ComponentManager;
import ru.phyllosedis.textario.resource.ResourceType;

/**
 * Атомарная проверка и размещение
 */
@Service
@RequiredArgsConstructor
public class PlacementService {

    private final OccupancyGrid occupancyGrid;
    private final ComponentManager cm;

    /**
     * Валидация: можно ли воткнуть постройку размером WxH на координаты X:Y
     */
    public boolean canPlace(int startX, int startY, int width, int height, ResourceType requiredTerrain) {
        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {

                // 1. Проверка границ карты
                if (x < 0 || x >= occupancyGrid.getWidth() || y < 0 || y >= occupancyGrid.getHeight()) return false;

                // 2. Проверка на занятость другой постройкой
                if (occupancyGrid.isCellOccupied(x, y)) return false;

                // 3. Проверка типа земли (например, Бур можно ставить ТОЛЬКО на руду)
                if (requiredTerrain != ResourceType.UNDEFINED && occupancyGrid.getTerrainAt(x, y) != requiredTerrain) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Фиксация постройки на карте
     */
    public void registerBuildingOnMap(long entityId, int startX, int startY, int width, int height) {
        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {
                occupancyGrid.occupyCell(x, y, entityId);
            }
        }
    }

}
