package ru.phyllosedis.textario.logistics.inserter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.ecs.ComponentFactoryRegistry;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.ecs.component.Requires;
import ru.phyllosedis.textario.engine.ecs.system.AbstractSystem;
import ru.phyllosedis.textario.inventory.InventoryComponent;
import ru.phyllosedis.textario.inventory.InventoryAccess;
import ru.phyllosedis.textario.logistics.port.LogisticPort;
import ru.phyllosedis.textario.logistics.port.PortSide;
import ru.phyllosedis.textario.logistics.port.PortType;
import ru.phyllosedis.textario.production.ProgressComponent;
import ru.phyllosedis.textario.resource.ResourceType;
import ru.phyllosedis.textario.resource.SystemOrder;
import ru.phyllosedis.textario.world.BuildingComponent;
import ru.phyllosedis.textario.world.OccupancyGrid;
import ru.phyllosedis.textario.world.PortResolver;
import ru.phyllosedis.textario.world.PositionComponent;
import ru.phyllosedis.textario.world.RotationComponent;

import java.util.ArrayList;
import java.util.List;

@Requires({
        InserterComponent.class,
        PositionComponent.class,
        LogisticPort.class,
        BuildingComponent.class,
})
@Component
@Order(SystemOrder.INSERTER)
public class InserterSystem extends AbstractSystem {

    private final OccupancyGrid occupancyGrid;

    public InserterSystem(ComponentFactoryRegistry cfm, ComponentManager cm, OccupancyGrid occupancyGrid) {
        super(cfm, cm);
        this.occupancyGrid = occupancyGrid;
    }

    @Override
    protected void updateEntity(long id) {
        InserterComponent inserter = cm.get(id, InserterComponent.class);
        PositionComponent position = cm.get(id, PositionComponent.class);
        BuildingComponent building = cm.get(id, BuildingComponent.class);
        LogisticPort port = cm.get(id, LogisticPort.class);
        if (inserter == null || position == null || building == null || port == null) {
            return;
        }
        LogisticPort.Port inputPort = findPort(port, PortType.INPUT);
        LogisticPort.Port outputPort = findPort(port, PortType.OUTPUT);

        int[] inputPosition = PortResolver.resolve(position, building, inputPort, rotationOf(id));

        int[] outputPosition = PortResolver.resolve(position, building, outputPort, rotationOf(id));

        Long sourceId = occupancyGrid.getEntityAt(
                inputPosition[0],
                inputPosition[1]
        );

        Long destinationId = occupancyGrid.getEntityAt(
                outputPosition[0],
                outputPosition[1]
        );

        if (sourceId == null || destinationId == null) {
            return;
        }
        if (sourceId == id || destinationId == id) {
            return;
        }

        InventoryComponent sourceInventory = InventoryAccess.sourceInventory(cm, sourceId);
        InventoryComponent destinationInventory = cm.get(destinationId, InventoryComponent.class);

        if (sourceInventory == null || destinationInventory == null) {
            return;
        }

        // Замах: прогресс капает со скоростью руки, перенос — пачкой stackSize
        ProgressComponent progress = cm.get(id, ProgressComponent.class);
        double swing = (progress == null ? 100.0 : progress.getProgress()) + inserter.getTransferSpeed();
        if (swing < 100.0) {
            cm.add(id, cfm.create(new ProgressComponent.Args(swing)));
            return;
        }

        boolean transferred = transfer(
                inserter,
                sourceId,
                sourceInventory,
                destinationId,
                destinationInventory
        );

        cm.add(id, cfm.create(new ProgressComponent.Args(transferred ? 0.0 : 100.0)));
    }

    private boolean transfer(
            InserterComponent inserter,
            long sourceId,
            InventoryComponent source,
            long destinationId,
            InventoryComponent destination
    ) {
        InventoryComponent.Slot sourceSlot =
                findSourceSlot(source);

        if (sourceSlot == null) {
            return false;
        }

        ResourceType resourceType =
                ResourceType.UNDEFINED.getByOrdinal(
                        sourceSlot.resource()
                );

        int destinationFreeSpace =
                getFreeSpace(
                        destination,
                        resourceType
                );

        if (destinationFreeSpace <= 0) {
            return false;
        }

        int amount = Math.min(
                inserter.getStackSize(),
                Math.min(
                        sourceSlot.count(),
                        destinationFreeSpace
                )
        );

        if (amount <= 0) {
            return false;
        }

        removeFromInventory(
                sourceId,
                source,
                sourceSlot,
                amount
        );

        addToInventory(
                destinationId,
                destination,
                resourceType,
                amount
        );
        return true;
    }

    private InventoryComponent.Slot findSourceSlot(
            InventoryComponent inventory
    ) {
        return inventory.getSlots()
                .stream()
                .filter(slot -> slot.count() > 0)
                .findFirst()
                .orElse(null);
    }

    private int getFreeSpace(
            InventoryComponent inventory,
            ResourceType resourceType
    ) {
        for (InventoryComponent.Slot slot :
                inventory.getSlots()) {

            if (slot.resource() == resourceType.ordinal()) {
                return inventory.getStackSize() - slot.count();
            }
        }

        /*
         * В destination ещё нет такого ресурса.
         * Значит, нужен новый слот.
         */
        if (inventory.getSlots().size() < inventory.getSize()) {
            return inventory.getStackSize();
        }

        return 0;
    }

    private void removeFromInventory(
            long entityId,
            InventoryComponent inventory,
            InventoryComponent.Slot sourceSlot,
            int amount
    ) {
        List<InventoryComponent.Slot> slots =
                inventory.getSlots()
                        .stream()
                        .map(slot -> {
                            if (slot == sourceSlot) {
                                return new InventoryComponent.Slot(
                                        slot.resource(),
                                        slot.count() - amount
                                );
                            }

                            return slot;
                        })
                        .filter(slot -> slot.count() > 0)
                        .toList();

        writeInventory(
                entityId,
                inventory,
                slots
        );
    }

    private void addToInventory(
            long entityId,
            InventoryComponent inventory,
            ResourceType resourceType,
            int amount
    ) {
        List<InventoryComponent.Slot> slots =
                new ArrayList<>(inventory.getSlots());

        for (int i = 0; i < slots.size(); i++) {

            InventoryComponent.Slot slot =
                    slots.get(i);

            if (slot.resource() == resourceType.ordinal()) {

                slots.set(
                        i,
                        new InventoryComponent.Slot(
                                slot.resource(),
                                slot.count() + amount
                        )
                );

                writeInventory(
                        entityId,
                        inventory,
                        slots
                );

                return;
            }
        }

        slots.add(
                new InventoryComponent.Slot(
                        resourceType.ordinal(),
                        amount
                )
        );

        writeInventory(
                entityId,
                inventory,
                slots
        );
    }

    private void writeInventory(
            long entityId,
            InventoryComponent inventory,
            List<InventoryComponent.Slot> slots
    ) {
        InventoryAccess.writeInventory(cm, cfm, entityId, inventory, slots);
    }

    private LogisticPort.Port findPort(
            LogisticPort logisticPort,
            PortType requiredType
    ) {
        return logisticPort.getPorts()
                .stream()
                .filter(port ->
                        PortType.UNDEFINED.getByOrdinal(port.type())
                                == requiredType
                )
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "У манипулятора отсутствует порт "
                                        + requiredType
                        )
                );
    }

    private int rotationOf(long id) {
        RotationComponent rotation = cm.get(id, RotationComponent.class);
        return rotation == null ? 0 : rotation.getSteps();
    }
}
