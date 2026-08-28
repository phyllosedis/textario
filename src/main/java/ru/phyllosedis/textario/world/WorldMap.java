package ru.phyllosedis.textario.world;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class WorldMap {
    private final Map<Integer, Map<Integer, Long>> grid = new HashMap<>();

    public void placeEntity(int x, int y, long entityId) {
        grid.computeIfAbsent(x, k -> new HashMap<>()).put(y, entityId);
    }

    public void removeEntity(int x, int y) {
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
