package ru.phyllosedis.textario.world;

import org.springframework.stereotype.Service;
import ru.phyllosedis.textario.engine.spring.ecs.ComponentManager;
import ru.phyllosedis.textario.resource.ResourceType;

@Service
public class PlacementService {
    private final GameMap gameMap;
    private final ComponentManager cm;

    public PlacementService(GameMap gameMap, ComponentManager cm) {
        this.gameMap = gameMap;
        this.cm = cm;
    }

    /**
     * Валидация: можно ли воткнуть постройку размером WxH на координаты X:Y
     */
    public boolean canPlace(int startX, int startY, int width, int height, ResourceType requiredTerrain) {
        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {

                // 1. Проверка границ карты
                if (x < 0 || x >= gameMap.getWidth() || y < 0 || y >= gameMap.getHeight()) return false;

                // 2. Проверка на занятость другой постройкой
                if (gameMap.isCellOccupied(x, y)) return false;

                // 3. Проверка типа земли (например, Бур можно ставить ТОЛЬКО на руду)
                if (requiredTerrain != ResourceType.UNDEFINED && gameMap.getTerrainAt(x, y) != requiredTerrain) {
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
                gameMap.occupyCell(x, y, entityId);
            }
        }
    }

}
