package ru.phyllosedis.textario.world;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.resource.ResourceCategory;
import ru.phyllosedis.textario.resource.ResourceType;
import ru.phyllosedis.textario.resource.capability.BuildSurface;
import ru.phyllosedis.textario.resource.capability.Mineable;

/**
 * Атомарная проверка и размещение
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PlacementService {

    private final OccupancyGrid occupancyGrid;
    private final ComponentManager cm;

    /**
     * Валидация: можно ли воткнуть постройку размером WxH на координаты X:Y.
     * Проверяется террейн под каждой клеткой: для requiredCategory ORE нужна руда
     * (Mineable), для остальных — стройплощадка (BuildSurface).
     */
    public boolean canPlace(int startX, int startY, int width, int height, ResourceCategory requiredCategory) {
        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {

                // 1. Проверка границ карты
                if (x < 0 || x >= occupancyGrid.getWidth() || y < 0 || y >= occupancyGrid.getHeight()) return false;

                // 2. Проверка на занятость другой постройкой
                if (occupancyGrid.isCellOccupied(x, y)) return false;

                // 3. Проверка террейна под клеткой
                ResourceType terrain = occupancyGrid.getTerrainAt(x, y);
                if (requiredCategory == ResourceCategory.ORE) {
                    if (!terrain.hasCapability(Mineable.class)) {
                        return false;
                    }
                } else {
                    if (!terrain.hasCapability(BuildSurface.class)) {
                        log.debug("На {} нельзя строить", terrain);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * @deprecated используйте {@link #canPlace(int, int, int, int, ResourceCategory)} —
     * террейн теперь читается из карты, а не из аргумента.
     */
    @Deprecated
    public boolean canPlace(int startX, int startY, int width, int height, ResourceCategory requiredCategory, ResourceType resourceType) {
        return canPlace(startX, startY, width, height, requiredCategory);
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
