package ru.phyllosedis.textario.world;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.phyllosedis.textario.resource.ResourceType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameMap {
    @Getter
    @Value("${textario.map-size.width}")
    private Integer width;
    @Getter
    @Value("${textario.map-size.height}")
    private Integer height;


    // ресурсы карты
    private final ResourceType[][] terrainGrid = new ResourceType[width][height];

    // key="x_y", value=entityId
    private final Map<String, Long> buildingGrid = new ConcurrentHashMap<>();

    public GameMap() {
        // Инициализируем карту: заполняем всё пустой землей,
        // а в пару мест насыпаем залежи руды для тестов
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                terrainGrid[x][y] = ResourceType.UNDEFINED;
            }
        }
        // TODO вынести генерацию карты
        // Допустим, на координатах (5, 20) лежит жила железа
        terrainGrid[5][20] = ResourceType.IRON_ORE;
        terrainGrid[5][25] = ResourceType.COPPER_ORE;
    }

    private String toKey(int x, int y) {
        return x + "_" + y;
    }

    public ResourceType getTerrainAt(int x, int y) {
        return terrainGrid[x][y];
    }

    public boolean isCellOccupied(int x, int y) {
        return buildingGrid.containsKey(toKey(x, y));
    }

    public void occupyCell(int x, int y, long entityId) {
        buildingGrid.put(toKey(x, y), entityId);
    }

    public void freeCell(int x, int y) {
        buildingGrid.remove(toKey(x, y));
    }
}
