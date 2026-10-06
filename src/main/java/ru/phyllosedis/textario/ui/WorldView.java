package ru.phyllosedis.textario.ui;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.phyllosedis.textario.console.MapRenderer;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.inventory.InventoryComponent;
import ru.phyllosedis.textario.production.DispatchedProductComponent;
import ru.phyllosedis.textario.production.ProgressComponent;
import ru.phyllosedis.textario.resource.ResourceType;
import ru.phyllosedis.textario.world.OccupancyGrid;

import java.util.ArrayList;
import java.util.List;

/**
 * Read-модель мира для фронтендов. GUI ходит только сюда
 * (и в GameCommands/CommandParser для действий) — внутрь
 * систем и ComponentManager напрямую не лезет.
 */
@Service
@RequiredArgsConstructor
public class WorldView {

    private final OccupancyGrid occupancyGrid;
    private final MapRenderer mapRenderer;
    private final ComponentManager cm;

    public record Cell(int x, int y, String terrain, char glyph, boolean occupied) {
    }

    public List<Cell> snapshot(int x0, int y0, int w, int h) {
        List<Cell> cells = new ArrayList<>(w * h);
        for (int y = y0; y < y0 + h; y++) {
            for (int x = x0; x < x0 + w; x++) {
                ResourceType terrain;
                try {
                    terrain = occupancyGrid.getTerrainAt(x, y);
                } catch (Exception e) {
                    terrain = ResourceType.UNDEFINED;
                }
                Long entityId = null;
                try {
                    entityId = occupancyGrid.getEntityAt(x, y);
                } catch (Exception ignored) {
                }
                cells.add(new Cell(x, y, terrain.name(), mapRenderer.glyphAt(x, y), entityId != null));
            }
        }
        return cells;
    }

    public int mapWidth() {
        return occupancyGrid.getWidth();
    }

    public int mapHeight() {
        return occupancyGrid.getHeight();
    }

    /**
     * Короткое описание клетки для статусбара: координаты,
     * террейн, постройка и её склад.
     */
    public String describe(int x, int y) {
        ResourceType terrain;
        try {
            terrain = occupancyGrid.getTerrainAt(x, y);
        } catch (Exception e) {
            terrain = ResourceType.UNDEFINED;
        }
        StringBuilder sb = new StringBuilder(x + ":" + y + " " + terrain);
        Long entityId;
        try {
            entityId = occupancyGrid.getEntityAt(x, y);
        } catch (Exception e) {
            entityId = null;
        }
        if (entityId == null) {
            return sb.toString();
        }
        sb.append(" | ").append(mapRenderer.glyphAt(x, y)).append(" #").append(entityId);
        InventoryComponent inv = cm.get(entityId, InventoryComponent.class);
        if (inv != null && !inv.getSlots().isEmpty()) {
            sb.append(" inv=[");
            for (InventoryComponent.Slot slot : inv.getSlots()) {
                sb.append(ResourceType.UNDEFINED.getByOrdinal(slot.resource()))
                        .append("x").append(slot.count()).append(" ");
            }
            sb.append("]");
        }
        DispatchedProductComponent buf = cm.get(entityId, DispatchedProductComponent.class);
        if (buf != null) {
            sb.append(" buf=").append(ResourceType.UNDEFINED.getByOrdinal(buf.getResource()))
                    .append("x").append(buf.getCount());
        }
        ProgressComponent progress = cm.get(entityId, ProgressComponent.class);
        if (progress != null) {
            sb.append(String.format(" %.0f%%", progress.getProgress()));
        }
        return sb.toString();
    }
}
