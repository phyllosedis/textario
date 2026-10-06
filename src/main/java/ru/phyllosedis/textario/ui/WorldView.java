package ru.phyllosedis.textario.ui;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.phyllosedis.textario.console.MapRenderer;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.inventory.InventoryComponent;
import ru.phyllosedis.textario.inventory.OutputInventoryComponent;
import ru.phyllosedis.textario.logistics.port.LogisticPort;
import ru.phyllosedis.textario.logistics.port.PortSide;
import ru.phyllosedis.textario.logistics.port.PortType;
import ru.phyllosedis.textario.logistics.underground.UndergroundComponent;
import ru.phyllosedis.textario.logistics.underground.UndergroundMode;
import ru.phyllosedis.textario.production.DispatchedProductComponent;
import ru.phyllosedis.textario.production.ProgressComponent;
import ru.phyllosedis.textario.production.assembler.AssemblerComponent;
import ru.phyllosedis.textario.production.furnace.FuelComponent;
import ru.phyllosedis.textario.production.recipe.RecipeBook;
import ru.phyllosedis.textario.production.DispatchedProductComponent;
import ru.phyllosedis.textario.production.ProgressComponent;
import ru.phyllosedis.textario.resource.ResourceType;
import ru.phyllosedis.textario.world.OccupancyGrid;
import ru.phyllosedis.textario.world.PortResolver;
import ru.phyllosedis.textario.world.RotationComponent;

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
    private final RecipeBook book;

    public record Cell(int x, int y, String terrain, char glyph, boolean occupied,
                         List<PortSide> inputs, List<PortSide> outputs, String tag) {
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
                List<PortSide> inputs = List.of();
                List<PortSide> outputs = List.of();
                String tag = "";
                if (entityId != null) {
                    LogisticPort ports = cm.get(entityId, LogisticPort.class);
                    RotationComponent rotation = cm.get(entityId, RotationComponent.class);
                    int steps = rotation == null ? 0 : rotation.getSteps();
                    UndergroundComponent underground = cm.get(entityId, UndergroundComponent.class);
                    if (underground != null) {
                        tag = UndergroundMode.UNDEFINED.getByOrdinal(underground.getMode()).name();
                        inputs = List.of(PortResolver.rotateSide(PortSide.BACK, steps));
                        outputs = List.of(PortResolver.rotateSide(PortSide.FRONT, steps));
                    } else if (ports != null) {
                        inputs = ports.getPorts().stream()
                                .filter(p -> PortType.UNDEFINED.getByOrdinal(p.type()) == PortType.INPUT)
                                .map(p -> PortResolver.rotateSide(
                                        PortSide.UNDEFINED.getByOrdinal(p.side()), steps))
                                .toList();
                        outputs = ports.getPorts().stream()
                                .filter(p -> PortType.UNDEFINED.getByOrdinal(p.type()) == PortType.OUTPUT)
                                .map(p -> PortResolver.rotateSide(
                                        PortSide.UNDEFINED.getByOrdinal(p.side()), steps))
                                .toList();
                    }
                }
                cells.add(new Cell(x, y, terrain.name(), mapRenderer.glyphAt(x, y),
                        entityId != null, inputs, outputs, tag));
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
    public String describe(int x, int y) {        ResourceType terrain;
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
        RotationComponent rotation = cm.get(entityId, RotationComponent.class);
        if (rotation != null) {
            sb.append(" смотрит ").append(PortResolver.directionName(rotation.getSteps()));
        }
        AssemblerComponent assembler = cm.get(entityId, AssemblerComponent.class);
        if (assembler != null) {
            String recipe = book.byId(assembler.getRecipeId())
                    .map(r -> r.name() + " (" + r.id() + ")")
                    .orElse("рецепт не выбран");
            sb.append(" рецепт: ").append(recipe);
        }
        FuelComponent fuel = cm.get(entityId, FuelComponent.class);
        if (fuel != null) {
            sb.append(" топливо: ").append(fuel.getHeat());
        }
        InventoryComponent inv = cm.get(entityId, InventoryComponent.class);
        if (inv != null && !inv.getSlots().isEmpty()) {
            sb.append(" inv=[");
            appendSlots(sb, inv);
            sb.append("]");
        }
        OutputInventoryComponent out = cm.get(entityId, OutputInventoryComponent.class);
        if (out != null && !out.getSlots().isEmpty()) {
            sb.append(" out=[");
            appendSlots(sb, out);
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

    private static void appendSlots(StringBuilder sb, InventoryComponent inv) {
        for (InventoryComponent.Slot slot : inv.getSlots()) {
            sb.append(ResourceType.UNDEFINED.getByOrdinal(slot.resource()))
                    .append("x").append(slot.count()).append(" ");
        }
    }
}
