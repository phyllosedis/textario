package ru.phyllosedis.textario.console;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.phyllosedis.textario.engine.ecs.ComponentFactoryRegistry;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.spring.EntityBlueprintService;
import ru.phyllosedis.textario.inventory.InventoryComponent;
import ru.phyllosedis.textario.logistics.belt.BeltMarkerComponent;
import ru.phyllosedis.textario.logistics.inserter.InserterMarkerComponent;
import ru.phyllosedis.textario.logistics.splitter.SplitMode;
import ru.phyllosedis.textario.production.DispatchedProductComponent;
import ru.phyllosedis.textario.production.ProgressComponent;
import ru.phyllosedis.textario.resource.ResourceType;
import ru.phyllosedis.textario.resource.Tier;
import ru.phyllosedis.textario.world.OccupancyGrid;
import ru.phyllosedis.textario.world.RotationComponent;

import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class GameCommands {

    private final EntityBlueprintService blueprints;
    private final ComponentManager cm;
    private final ComponentFactoryRegistry cfm;
    private final OccupancyGrid occupancyGrid;
    private final MapRenderer mapRenderer;

    public String placeMiner(int x, int y, ResourceType ore) {
        return tryPlace(() -> {
            long id = blueprints.createMiner(x, y, Tier.ONE, ore);
            return "бур #" + id + " на " + x + ":" + y;
        });
    }

    public String placeBelt(int x, int y) {
        return placeBelt(x, y, 0);
    }

    public String placeBelt(int x, int y, int rotation) {
        return tryPlace(() -> {
            long id = blueprints.createBelt(x, y, Tier.ONE, ResourceType.EARTH, rotation);
            return "лента #" + id + " на " + x + ":" + y;
        });
    }

    public String placeInserter(int x, int y) {
        return placeInserter(x, y, 0);
    }

    public String placeInserter(int x, int y, int rotation) {
        return tryPlace(() -> {
            long id = blueprints.createInserter(x, y, Tier.ONE, ResourceType.EARTH, rotation);
            return "рука #" + id + " на " + x + ":" + y;
        });
    }

    public String placeChest(int x, int y) {
        return tryPlace(() -> {
            long id = blueprints.createChest(x, y, Tier.ONE);
            return "сундук #" + id + " на " + x + ":" + y;
        });
    }

    public String placeFurnace(int x, int y) {
        return tryPlace(() -> {
            long id = blueprints.createFurnace(x, y, Tier.ONE);
            return "печь #" + id + " на " + x + ":" + y;
        });
    }

    /**
     * Повернуть ленту/руку на клетке на 90° по часовой.
     */
    public String rotateAt(int x, int y) {
        Long id;
        try {
            id = occupancyGrid.getEntityAt(x, y);
        } catch (Exception e) {
            return "FAIL: " + e.getMessage();
        }
        if (id == null) {
            return "FAIL: на " + x + ":" + y + " пусто";
        }
        if (!cm.has(id, BeltMarkerComponent.class) && !cm.has(id, InserterMarkerComponent.class)) {
            return "FAIL: #" + id + " не поворачивается (только лента и рука)";
        }
        RotationComponent current = cm.get(id, RotationComponent.class);
        int next = ((current == null ? 0 : current.getSteps()) + 1) % 4;
        cm.add(id, cfm.create(new RotationComponent.Args(next)));
        return "OK: #" + id + " теперь смотрит " + directionName(next);
    }

    public boolean isRotatable(long id) {
        return cm.has(id, BeltMarkerComponent.class) || cm.has(id, InserterMarkerComponent.class);
    }

    public Long entityAt(int x, int y) {
        try {
            return occupancyGrid.getEntityAt(x, y);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Куда смотрит выход (FRONT): 0 вниз, 1 влево, 2 вверх, 3 вправо.
     */
    public static int parseDirection(String word) {
        if (word == null) {
            return 0;
        }
        return switch (word.toLowerCase()) {
            case "down", "вниз", "s", "юг" -> 0;
            case "left", "влево", "a", "запад" -> 1;
            case "up", "вверх", "w", "север" -> 2;
            case "right", "вправо", "d", "восток" -> 3;
            default -> 0;
        };
    }

    public static String directionName(int rotation) {
        return switch (((rotation % 4) + 4) % 4) {
            case 0 -> "вниз";
            case 1 -> "влево";
            case 2 -> "вверх";
            case 3 -> "вправо";
            default -> "?";
        };
    }

    public String placeSplitter(int x, int y, SplitMode mode) {
        return tryPlace(() -> {
            long id = blueprints.createSplitter(x, y, Tier.ONE, ResourceType.EARTH, mode);
            return "разделитель #" + id + " на " + x + ":" + y + " " + mode;
        });
    }

    public String map() {
        return mapRenderer.render(0, 15, 25, 20);
    }

    public String map(int x, int y, int w, int h) {
        return mapRenderer.render(x, y, w, h);
    }

    public String inventories() {
        StringBuilder sb = new StringBuilder();
        Map<Long, String> entities = new TreeMap<>(blueprints.createdEntities());
        if (entities.isEmpty()) {
            return "(пока нет построек)";
        }
        for (Map.Entry<Long, String> e : entities.entrySet()) {
            long id = e.getKey();
            sb.append("#").append(id).append(" ").append(e.getValue());
            InventoryComponent inv = cm.get(id, InventoryComponent.class);
            if (inv != null) {
                sb.append(" inv=[");
                for (InventoryComponent.Slot slot : inv.getSlots()) {
                    ResourceType type = ResourceType.UNDEFINED.getByOrdinal(slot.resource());
                    sb.append(type).append("x").append(slot.count()).append(" ");
                }
                sb.append("]");
            }
            DispatchedProductComponent buf = cm.get(id, DispatchedProductComponent.class);
            if (buf != null) {
                ResourceType type = ResourceType.UNDEFINED.getByOrdinal(buf.getResource());
                sb.append(" buf=").append(type).append("x").append(buf.getCount());
            }
            ProgressComponent progress = cm.get(id, ProgressComponent.class);
            if (progress != null) {
                sb.append(String.format(" %.0f%%", progress.getProgress()));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String tryPlace(PlaceAction action) {
        try {
            return "OK: " + action.place();
        } catch (Exception e) {
            return "FAIL: " + e.getMessage();
        }
    }

    private interface PlaceAction {
        String place();
    }
}
