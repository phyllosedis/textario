package ru.phyllosedis.textario.logistics.splitter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.ecs.ComponentFactoryRegistry;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.ecs.component.Requires;
import ru.phyllosedis.textario.engine.ecs.system.AbstractSystem;
import ru.phyllosedis.textario.inventory.InventoryComponent;
import ru.phyllosedis.textario.logistics.port.LogisticPort;
import ru.phyllosedis.textario.logistics.port.PortSide;
import ru.phyllosedis.textario.logistics.port.PortType;
import ru.phyllosedis.textario.resource.ResourceType;
import ru.phyllosedis.textario.resource.SystemOrder;
import ru.phyllosedis.textario.world.BuildingComponent;
import ru.phyllosedis.textario.world.OccupancyGrid;
import ru.phyllosedis.textario.world.PortResolver;
import ru.phyllosedis.textario.world.PositionComponent;
import ru.phyllosedis.textario.world.RotationComponent;
import ru.phyllosedis.textario.world.BuildingComponent;
import ru.phyllosedis.textario.world.OccupancyGrid;
import ru.phyllosedis.textario.world.PositionComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Requires({SplitterComponent.class, LogisticPort.class, PositionComponent.class, BuildingComponent.class, InventoryComponent.class})
@Component
@Order(SystemOrder.BELT)
public class SplitterSystem extends AbstractSystem {

    private final OccupancyGrid occupancyGrid;
    private final Map<Long, Integer> roundRobin = new ConcurrentHashMap<>();

    public SplitterSystem(ComponentFactoryRegistry cfm, ComponentManager cm, OccupancyGrid occupancyGrid) {
        super(cfm, cm);
        this.occupancyGrid = occupancyGrid;
    }

    @Override
    protected void updateEntity(long id) {
        SplitterComponent splitter = cm.get(id, SplitterComponent.class);
        LogisticPort ports = cm.get(id, LogisticPort.class);
        PositionComponent position = cm.get(id, PositionComponent.class);
        BuildingComponent building = cm.get(id, BuildingComponent.class);
        InventoryComponent buffer = cm.get(id, InventoryComponent.class);
        if (splitter == null || ports == null || position == null || building == null || buffer == null) {
            return;
        }

        // Подтянуть 1 шт со входа, если буфер пуст
        if (buffer.getSlots().isEmpty()) {
            pullInput(id, position, building, ports);
            buffer = cm.get(id, InventoryComponent.class);
            if (buffer == null || buffer.getSlots().isEmpty()) {
                return;
            }
        }

        InventoryComponent.Slot slot = buffer.getSlots().get(0);
        ResourceType type = ResourceType.UNDEFINED.getByOrdinal(slot.resource());
        List<Long> outputs = outputEntities(id, position, building, ports);

        Long target = switch (SplitMode.UNDEFINED.getByOrdinal(splitter.getSplitMode())) {
            case ROUND_ROBIN -> {
                int next = (roundRobin.getOrDefault(id, 0) + 1) % Math.max(1, outputs.size());
                roundRobin.put(id, next);
                yield outputs.isEmpty() ? null : outputs.get(next % outputs.size());
            }
            case BALANCED -> outputs.stream()
                    .max((a, b) -> Integer.compare(freeSpace(a, type), freeSpace(b, type)))
                    .orElse(null);
            case PRIORITY_LEFT, PRIORITY_RIGHT -> outputs.isEmpty() ? null : outputs.get(0);
            default -> outputs.isEmpty() ? null : outputs.get(0);
        };

        if (target == null || target == id) {
            return;
        }
        if (freeSpace(target, type) <= 0) {
            return;
        }
        takeFromBuffer(id, buffer, slot, 1);
        InventoryComponent dest = cm.get(target, InventoryComponent.class);
        addToInventory(target, dest, type, 1);
    }

    private void pullInput(long id, PositionComponent position, BuildingComponent building, LogisticPort ports) {
        LogisticPort.Port input = ports.getPorts().stream()
                .filter(p -> PortType.UNDEFINED.getByOrdinal(p.type()) == PortType.INPUT)
                .findFirst().orElse(null);
        if (input == null) {
            return;
        }
        int[] pos = PortResolver.resolve(position, building, input, rotationOf(id));
        Long srcId = occupancyGrid.getEntityAt(pos[0], pos[1]);
        if (srcId == null || srcId == id) {
            return;
        }
        InventoryComponent src = cm.get(srcId, InventoryComponent.class);
        InventoryComponent buf = cm.get(id, InventoryComponent.class);
        if (src == null || src.getSlots().isEmpty() || buf == null) {
            return;
        }
        InventoryComponent.Slot srcSlot = src.getSlots().get(0);
        ResourceType type = ResourceType.UNDEFINED.getByOrdinal(srcSlot.resource());
        if (freeSpace(id, type) <= 0) {
            return;
        }
        removeFromInventory(srcId, src, srcSlot, 1);
        addToInventory(id, cm.get(id, InventoryComponent.class), type, 1);
    }

    private List<Long> outputEntities(long id, PositionComponent position, BuildingComponent building,
                                        LogisticPort ports) {
        List<Long> result = new ArrayList<>();
        for (LogisticPort.Port port : ports.getPorts()) {
            if (PortType.UNDEFINED.getByOrdinal(port.type()) != PortType.OUTPUT) {
                continue;
            }
            int[] pos = PortResolver.resolve(position, building, port, rotationOf(id));
            Long entityId = occupancyGrid.getEntityAt(pos[0], pos[1]);
            if (entityId != null && cm.get(entityId, InventoryComponent.class) != null) {
                result.add(entityId);
            }
        }
        return result;
    }

    private int rotationOf(long id) {
        RotationComponent rotation = cm.get(id, RotationComponent.class);
        return rotation == null ? 0 : rotation.getSteps();
    }

    private int freeSpace(long entityId, ResourceType type) {
        InventoryComponent inv = cm.get(entityId, InventoryComponent.class);
        if (inv == null) {
            return 0;
        }
        return freeSpace(inv, type);
    }

    private int freeSpace(InventoryComponent inv, ResourceType type) {
        for (InventoryComponent.Slot slot : inv.getSlots()) {
            if (slot.resource() == type.ordinal()) {
                return inv.getStackSize() - slot.count();
            }
        }
        if (inv.getSlots().size() < inv.getSize()) {
            return inv.getStackSize();
        }
        return 0;
    }

    private void takeFromBuffer(long id, InventoryComponent buf, InventoryComponent.Slot slot, int amount) {
        List<InventoryComponent.Slot> slots = new ArrayList<>();
        for (InventoryComponent.Slot s : buf.getSlots()) {
            if (s == slot) {
                int rest = s.count() - amount;
                if (rest > 0) {
                    slots.add(new InventoryComponent.Slot(s.resource(), rest));
                }
            } else {
                slots.add(s);
            }
        }
        writeInventory(id, buf, slots);
    }

    private void removeFromInventory(long entityId, InventoryComponent inv, InventoryComponent.Slot srcSlot, int amount) {
        List<InventoryComponent.Slot> slots = inv.getSlots().stream()
                .map(s -> s == srcSlot
                        ? new InventoryComponent.Slot(s.resource(), s.count() - amount)
                        : s)
                .filter(s -> s.count() > 0)
                .toList();
        writeInventory(entityId, inv, slots);
    }

    private void addToInventory(long entityId, InventoryComponent inv, ResourceType type, int amount) {
        List<InventoryComponent.Slot> slots = new ArrayList<>(inv.getSlots());
        for (int i = 0; i < slots.size(); i++) {
            InventoryComponent.Slot s = slots.get(i);
            if (s.resource() == type.ordinal()) {
                slots.set(i, new InventoryComponent.Slot(s.resource(), s.count() + amount));
                writeInventory(entityId, inv, slots);
                return;
            }
        }
        slots.add(new InventoryComponent.Slot(type.ordinal(), amount));
        writeInventory(entityId, inv, slots);
    }

    private void writeInventory(long entityId, InventoryComponent inv, List<InventoryComponent.Slot> slots) {
        cm.add(entityId, cfm.create(new InventoryComponent.Args(
                inv.getSize(), inv.getStackSize(),
                slots.stream()
                        .map(s -> new InventoryComponent.ReadableSlot(
                                ResourceType.UNDEFINED.getByOrdinal(s.resource()), s.count()))
                        .toList())));
    }

    private int[] resolvePortPosition(PositionComponent position, BuildingComponent building, LogisticPort.Port port) {
        int x = position.getX();
        int y = position.getY();
        return switch (PortSide.UNDEFINED.getByOrdinal(port.side())) {
            case BACK -> new int[]{x, y - 1};
            case FRONT -> new int[]{x, y + building.getHeight()};
            case LEFT -> new int[]{x - 1, y};
            case RIGHT -> new int[]{x + building.getWidth(), y};
            default -> throw new IllegalArgumentException("Неизвестная сторона порта: " + port.side());
        };
    }
}
