package ru.phyllosedis.textario.logistics.belt;

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

import java.util.ArrayList;
import java.util.List;

@Requires({BeltComponent.class, PositionComponent.class, BuildingComponent.class, LogisticPort.class, InventoryComponent.class})
@Component
@Order(SystemOrder.BELT)
public class BeltSystem extends AbstractSystem {

    private final OccupancyGrid occupancyGrid;

    public BeltSystem(ComponentFactoryRegistry cfm, ComponentManager cm, OccupancyGrid occupancyGrid) {
        super(cfm, cm);
        this.occupancyGrid = occupancyGrid;
    }

    @Override
    protected void updateEntity(long id) {
        PositionComponent position = cm.get(id, PositionComponent.class);
        BuildingComponent building = cm.get(id, BuildingComponent.class);
        LogisticPort ports = cm.get(id, LogisticPort.class);
        InventoryComponent beltInv = cm.get(id, InventoryComponent.class);
        if (position == null || building == null || ports == null || beltInv == null) {
            return;
        }

        // 1. Протолкнуть свой предмет вперёд
        pushForward(id, position, building, ports, beltInv);

        // 2. Подтянуть предмет сзади
        pullFromBack(id, position, building, ports);
    }

    private void pushForward(long id, PositionComponent position, BuildingComponent building,
                             LogisticPort ports, InventoryComponent beltInv) {
        if (beltInv.getSlots().isEmpty()) {
            return;
        }
        InventoryComponent.Slot slot = beltInv.getSlots().get(0);
        if (slot.count() <= 0) {
            return;
        }
        int[] front = PortResolver.resolve(position, building,
                findPort(ports, PortType.OUTPUT), rotationOf(id));
        Long destId = occupancyGrid.getEntityAt(front[0], front[1]);
        if (destId == null || destId == id) {
            return;
        }
        InventoryComponent dest = cm.get(destId, InventoryComponent.class);
        if (dest == null) {
            return;
        }
        ResourceType type = ResourceType.UNDEFINED.getByOrdinal(slot.resource());
        if (freeSpace(dest, type) <= 0) {
            return;
        }
        takeFromBelt(id, beltInv, slot, 1);
        addToInventory(destId, dest, type, 1);
    }

    private void pullFromBack(long id, PositionComponent position, BuildingComponent building,
                              LogisticPort ports) {
        InventoryComponent beltInv = cm.get(id, InventoryComponent.class);
        if (beltInv == null) {
            return;
        }
        ResourceType carried = beltInv.getSlots().isEmpty() ? null
                : ResourceType.UNDEFINED.getByOrdinal(beltInv.getSlots().get(0).resource());
        int[] back = PortResolver.resolve(position, building,
                findPort(ports, PortType.INPUT), rotationOf(id));
        Long srcId = occupancyGrid.getEntityAt(back[0], back[1]);
        if (srcId == null || srcId == id) {
            return;
        }
        InventoryComponent src = cm.get(srcId, InventoryComponent.class);
        if (src == null || src.getSlots().isEmpty()) {
            return;
        }
        InventoryComponent.Slot srcSlot = src.getSlots().get(0);
        if (srcSlot.count() <= 0) {
            return;
        }
        ResourceType srcType = ResourceType.UNDEFINED.getByOrdinal(srcSlot.resource());
        // Лента везёт один тип за раз
        if (carried != null && carried != srcType) {
            return;
        }
        if (freeSpace(beltInv, srcType) <= 0) {
            return;
        }
        removeFromInventory(srcId, src, srcSlot, 1);
        addToInventory(id, cm.get(id, InventoryComponent.class), srcType, 1);
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

    private void takeFromBelt(long id, InventoryComponent beltInv, InventoryComponent.Slot slot, int amount) {
        List<InventoryComponent.Slot> slots = new ArrayList<>();
        for (InventoryComponent.Slot s : beltInv.getSlots()) {
            if (s == slot) {
                int rest = s.count() - amount;
                if (rest > 0) {
                    slots.add(new InventoryComponent.Slot(s.resource(), rest));
                }
            } else {
                slots.add(s);
            }
        }
        writeInventory(id, beltInv, slots);
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
                inv.getSize(),
                inv.getStackSize(),
                slots.stream()
                        .map(s -> new InventoryComponent.ReadableSlot(
                                ResourceType.UNDEFINED.getByOrdinal(s.resource()), s.count()))
                        .toList())));
    }

    private int rotationOf(long id) {
        RotationComponent rotation = cm.get(id, RotationComponent.class);
        return rotation == null ? 0 : rotation.getSteps();
    }

    private LogisticPort.Port findPort(LogisticPort ports, PortType type) {
        return ports.getPorts().stream()
                .filter(p -> PortType.UNDEFINED.getByOrdinal(p.type()) == type)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("У ленты нет порта " + type));
    }
}
