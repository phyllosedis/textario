package ru.phyllosedis.textario.console;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.spring.EntityBlueprintService;
import ru.phyllosedis.textario.inventory.InventoryComponent;
import ru.phyllosedis.textario.logistics.splitter.SplitMode;
import ru.phyllosedis.textario.production.DispatchedProductComponent;
import ru.phyllosedis.textario.production.ProgressComponent;
import ru.phyllosedis.textario.resource.ResourceType;
import ru.phyllosedis.textario.resource.Tier;

import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class GameCommands {

    private final EntityBlueprintService blueprints;
    private final ComponentManager cm;
    private final MapRenderer mapRenderer;

    public String placeMiner(int x, int y, ResourceType ore) {
        return tryPlace(() -> {
            long id = blueprints.createMiner(x, y, Tier.ONE, ore);
            return "бур #" + id + " на " + x + ":" + y;
        });
    }

    public String placeBelt(int x, int y) {
        return tryPlace(() -> {
            long id = blueprints.createBelt(x, y, Tier.ONE, ResourceType.EARTH);
            return "лента #" + id + " на " + x + ":" + y;
        });
    }

    public String placeInserter(int x, int y) {
        return tryPlace(() -> {
            long id = blueprints.createInserter(x, y, Tier.ONE, ResourceType.EARTH);
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
