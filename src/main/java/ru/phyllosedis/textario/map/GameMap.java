package ru.phyllosedis.textario.map;

import org.springframework.stereotype.Service;
import ru.phyllosedis.textario.type.ResourceType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static ru.phyllosedis.textario.constants.Constants.MAP_SIZE;

@Service
public class GameMap {
    // TODO вынести в параметр
    private final int width = MAP_SIZE;
    private final int height = MAP_SIZE;


    // ресурсы карты
    private final ResourceType[][] terrainGrid = new ResourceType[width][height];

    // key="x_y", value=entityId
    private final Map<String, Long> buildingGrid = new ConcurrentHashMap<>();

    public GameMap() {
        // Инициализируем карту: заполняем всё пустой землей,
        // а в пару мест насыпаем залежи руды для тестов
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                terrainGrid[x][y] = ResourceType.NONE; // Добавь NONE в свой ResourceType enum
            }
        }
        // TODO вынести генерацию карты
        // Допустим, на координатах (5, 20) лежит жила железа
        terrainGrid[5][20] = ResourceType.IRON_ORE;
        terrainGrid[5][25] = ResourceType.COPPER_ORE;
    }

    private String toKey(int x, int y) { return x + "_" + y; }

    public ResourceType getTerrainAt(int x, int y) { return terrainGrid[x][y]; }

    public boolean isCellOccupied(int x, int y) { return buildingGrid.containsKey(toKey(x, y)); }

    public void occupyCell(int x, int y, long entityId) { buildingGrid.put(toKey(x, y), entityId); }

    public void freeCell(int x, int y) { buildingGrid.remove(toKey(x, y)); }
}
