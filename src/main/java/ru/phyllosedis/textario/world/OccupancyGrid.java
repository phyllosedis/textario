package ru.phyllosedis.textario.world;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.resource.ResourceType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Занятость клеток сущностями
 */
@Component
public class OccupancyGrid {

    private final Map<Integer, Map<Integer, Long>> grid = new ConcurrentHashMap<>();
    private final TerrainMap terrainMap;

    @Getter
    private final Integer width;

    @Getter
    private final Integer height;

    public OccupancyGrid(TerrainMap terrainMap, @Value("${textario.map-size.width}") int width, @Value("${textario.map-size.height}") int height) {
        this.terrainMap = terrainMap;
        this.width = width;
        this.height = height;
    }

    public ResourceType getTerrainAt(int x, int y) {
        return ResourceType.UNDEFINED.getByOrdinal(terrainMap.getTerrainType(x, y));
    }

    public boolean isCellOccupied(int x, int y) {
        return Optional.ofNullable(grid.get(x).get(y)).isPresent();
    }

    public void occupyCell(int x, int y, long entityId) {
        grid.computeIfAbsent(x, k -> new HashMap<>()).put(y, entityId);
    }

    public void freeCell(int x, int y) {
        if (grid.containsKey(x)) {
            grid.get(x).remove(y);
        }
    }

    public Long getEntityAt(int x, int y) {
        Map<Integer, Long> yMap = grid.get(x);
        if (yMap == null) return null;
        return yMap.get(y);
    }
}
