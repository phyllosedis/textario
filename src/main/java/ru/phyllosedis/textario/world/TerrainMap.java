package ru.phyllosedis.textario.world;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.resource.ResourceType;

/**
 * Тип поверхности/ресурс клетки
 */
@Component
public class TerrainMap {
    private final int width;
    private final int height;
    private final int[][] terrain;

    public TerrainMap(
            @Value("${textario.map-size.width}") int width,
            @Value("${textario.map-size.height}") int height
    ) {
        this.width = width;
        this.height = height;
        this.terrain = new int[width][height];

        generate();
    }

    public int getTerrainType(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return ResourceType.UNDEFINED.ordinal();
        }
        return terrain[x][y];
    }

    private void generate() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                terrain[x][y] = ResourceType.EARTH.ordinal();
            }
        }

        // Стартовые рудные поля 3x3 для прототипа
        fillPatch(5, 20, ResourceType.IRON_ORE);
        fillPatch(12, 25, ResourceType.COPPER_ORE);
        fillPatch(18, 20, ResourceType.COAL);
    }

    private void fillPatch(int startX, int startY, ResourceType type) {
        for (int x = startX; x < startX + 3; x++) {
            for (int y = startY; y < startY + 3; y++) {
                if (x >= 0 && x < width && y >= 0 && y < height) {
                    terrain[x][y] = type.ordinal();
                }
            }
        }
    }

}
