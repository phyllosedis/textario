package ru.phyllosedis.textario.console;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.logistics.belt.BeltMarkerComponent;
import ru.phyllosedis.textario.logistics.inserter.InserterMarkerComponent;
import ru.phyllosedis.textario.logistics.splitter.SplitterMarkerComponent;
import ru.phyllosedis.textario.production.furnace.FurnaceMarkerComponent;
import ru.phyllosedis.textario.production.assembler.AssemblerMarkerComponent;
import ru.phyllosedis.textario.production.mining.MiningMarkerComponent;
import ru.phyllosedis.textario.resource.ResourceType;
import ru.phyllosedis.textario.storage.ChestMarkerComponent;
import ru.phyllosedis.textario.world.OccupancyGrid;

@Service
@RequiredArgsConstructor
public class MapRenderer {

    private final ComponentManager cm;
    private final OccupancyGrid occupancyGrid;

    public String render(int x0, int y0, int w, int h) {
        StringBuilder sb = new StringBuilder();
        sb.append("    ");
        for (int x = x0; x < x0 + w; x++) {
            sb.append(String.format("%2d", x % 100));
        }
        sb.append("\n");
        for (int y = y0; y < y0 + h; y++) {
            sb.append(String.format("%3d ", y));
            for (int x = x0; x < x0 + w; x++) {
                sb.append(glyph(x, y)).append(' ');
            }
            sb.append("\n");
        }
        sb.append("M=бур B=лента I=рука S=разделитель C=сундук F=печь A=сборщик | f=Fe c=Cu k=уголь .=земля\n");
        return sb.toString();
    }

    public char glyphAt(int x, int y) {
        return glyph(x, y);
    }

    private char glyph(int x, int y) {
        Long entityId = null;
        try {
            entityId = occupancyGrid.getEntityAt(x, y);
        } catch (Exception ignored) {
        }
        if (entityId != null) {
            if (cm.has(entityId, MiningMarkerComponent.class)) return 'M';
            if (cm.has(entityId, BeltMarkerComponent.class)) return 'B';
            if (cm.has(entityId, InserterMarkerComponent.class)) return 'I';
            if (cm.has(entityId, SplitterMarkerComponent.class)) return 'S';
            if (cm.has(entityId, ChestMarkerComponent.class)) return 'C';
            if (cm.has(entityId, FurnaceMarkerComponent.class)) return 'F';
            if (cm.has(entityId, AssemblerMarkerComponent.class)) return 'A';
            return '?';
        }
        ResourceType terrain;
        try {
            terrain = occupancyGrid.getTerrainAt(x, y);
        } catch (Exception e) {
            return ' ';
        }
        if (terrain == null) return ' ';
        return switch (terrain) {
            case IRON_ORE -> 'f';
            case COPPER_ORE -> 'c';
            case COAL -> 'k';
            case WATER -> '~';
            case EARTH -> '.';
            default -> '?';
        };
    }
}
