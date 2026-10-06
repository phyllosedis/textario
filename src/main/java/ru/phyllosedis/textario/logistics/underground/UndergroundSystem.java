package ru.phyllosedis.textario.logistics.underground;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.ecs.ComponentFactoryRegistry;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.ecs.component.Requires;
import ru.phyllosedis.textario.engine.ecs.system.AbstractSystem;
import ru.phyllosedis.textario.inventory.InventoryComponent;
import ru.phyllosedis.textario.resource.ResourceType;
import ru.phyllosedis.textario.resource.SystemOrder;
import ru.phyllosedis.textario.world.BuildingComponent;
import ru.phyllosedis.textario.world.OccupancyGrid;
import ru.phyllosedis.textario.world.PortResolver;
import ru.phyllosedis.textario.world.PositionComponent;
import ru.phyllosedis.textario.world.RotationComponent;

import java.util.ArrayList;
import java.util.List;

@Requires({UndergroundComponent.class, PositionComponent.class, BuildingComponent.class, InventoryComponent.class})
@Component
@Order(SystemOrder.BELT)
public class UndergroundSystem extends AbstractSystem {

    public static final int MAX_DIST = 4;

    private final OccupancyGrid occupancyGrid;

    public UndergroundSystem(ComponentFactoryRegistry cfm, ComponentManager cm, OccupancyGrid occupancyGrid) {
        super(cfm, cm);
        this.occupancyGrid = occupancyGrid;
    }

    @Override
    protected void updateEntity(long id) {
        UndergroundComponent underground = cm.get(id, UndergroundComponent.class);
        PositionComponent position = cm.get(id, PositionComponent.class);
        if (underground == null || position == null) {
            return;
        }
        int rotation = rotationOf(id);
        int[] front = PortResolver.rotateVec(0, 1, rotation);
        int[] back = PortResolver.rotateVec(0, -1, rotation);

        if (UndergroundMode.UNDEFINED.getByOrdinal(underground.getMode()) == UndergroundMode.ENTRY) {
            pullFromBack(id, position, back);
            pushUnderground(id, position, front, rotation);
        } else {
            pushForward(id, position, front);
        }
    }

    private void pullFromBack(long id, PositionComponent position, int[] back) {
        InventoryComponent buf = cm.get(id, InventoryComponent.class);
        if (buf == null) {
            return;
        }
        ResourceType carried = buf.getSlots().isEmpty() ? null
                : ResourceType.UNDEFINED.getByOrdinal(buf.getSlots().get(0).resource());
        Long srcId = occupancyGrid.getEntityAt(position.getX() + back[0], position.getY() + back[1]);
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
        if (carried != null && carried != srcType) {
            return;
        }
        if (freeSpace(buf, srcType) <= 0) {
            return;
        }
        removeFromInventory(srcId, src, srcSlot, 1);
        addToInventory(id, cm.get(id, InventoryComponent.class), srcType, 1);
    }

    private void pushUnderground(long id, PositionComponent position, int[] front, int rotation) {
        InventoryComponent buf = cm.get(id, InventoryComponent.class);
        if (buf == null || buf.getSlots().isEmpty()) {
            return;
        }
        InventoryComponent.Slot slot = buf.getSlots().get(0);
        ResourceType type = ResourceType.UNDEFINED.getByOrdinal(slot.resource());
        for (int k = 1; k <= MAX_DIST; k++) {
            Long exitId = occupancyGrid.getEntityAt(
                    position.getX() + front[0] * k, position.getY() + front[1] * k);
            if (exitId == null || exitId == id) {
                continue;
            }
            UndergroundComponent other = cm.get(exitId, UndergroundComponent.class);
            if (other == null
                    || UndergroundMode.UNDEFINED.getByOrdinal(other.getMode()) != UndergroundMode.EXIT
                    || rotationOf(exitId) != rotation) {
                continue;
            }
            InventoryComponent dest = cm.get(exitId, InventoryComponent.class);
            if (dest == null || freeSpace(dest, type) <= 0) {
                return;
            }
            takeFromBuffer(id, buf, slot, 1);
            addToInventory(exitId, cm.get(exitId, InventoryComponent.class), type, 1);
            return;
        }
    }

    private void pushForward(long id, PositionComponent position, int[] front) {
        InventoryComponent buf = cm.get(id, InventoryComponent.class);
        if (buf == null || buf.getSlots().isEmpty()) {
            return;
        }
        InventoryComponent.Slot slot = buf.getSlots().get(0);
        if (slot.count() <= 0) {
            return;
        }
        Long destId = occupancyGrid.getEntityAt(position.getX() + front[0], position.getY() + front[1]);
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
        takeFromBuffer(id, buf, slot, 1);
        addToInventory(destId, cm.get(destId, InventoryComponent.class), type, 1);
    }

    private int rotationOf(long id) {
        RotationComponent rotation = cm.get(id, RotationComponent.class);
        return rotation == null ? 0 : rotation.getSteps();
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
}
