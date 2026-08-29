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
        return terrain[x][y];
    }

    private void generate() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                terrain[x][y] = ResourceType.EARTH.ordinal();
            }
        }

        // TODO вынести генерацию карты
        terrain[5][20] = ResourceType.IRON_ORE.ordinal();
        terrain[5][25] = ResourceType.COPPER_ORE.ordinal();
    }

}
