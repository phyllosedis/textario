package ru.phyllosedis.textario.logistics.inserter;

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
import ru.phyllosedis.textario.production.station.OperationFinishedMarkerComponent;
import ru.phyllosedis.textario.production.station.StationMarkerComponent;
import ru.phyllosedis.textario.resource.ResourceType;
import ru.phyllosedis.textario.resource.SystemOrder;
import ru.phyllosedis.textario.world.BuildingComponent;
import ru.phyllosedis.textario.world.OccupancyGrid;
import ru.phyllosedis.textario.world.PositionComponent;

import java.util.ArrayList;
import java.util.List;

@Requires({
        InserterComponent.class,
        OperationFinishedMarkerComponent.class,
        PositionComponent.class,
        LogisticPort.class,
        BuildingComponent.class,
        StationMarkerComponent.class,
        InventoryComponent.class,
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
        LogisticPort.Port inputPort = findPort(port, PortType.INPUT);
        LogisticPort.Port outputPort = findPort(port, PortType.OUTPUT);

        int[] inputPosition =
                resolvePortPosition(
                        position,
                        building,
                        inputPort
                );

        int[] outputPosition =
                resolvePortPosition(
                        position,
                        building,
                        outputPort
                );

        Long sourceId = occupancyGrid.getEntityAt(
                inputPosition[0],
                inputPosition[1]
        );

        Long destinationId = occupancyGrid.getEntityAt(
                outputPosition[0],
                outputPosition[1]
        );


        if (sourceId == null || destinationId == null) {
            removeFinishedMarker(id);
            return;
        }

        InventoryComponent sourceInventory = cm.get(sourceId, InventoryComponent.class);

        InventoryComponent destinationInventory = cm.get(destinationId, InventoryComponent.class);

        if (sourceInventory == null || destinationInventory == null) {
            removeFinishedMarker(id);
            return;
        }

        boolean transferred = transfer(
                inserter,
                sourceId,
                sourceInventory,
                destinationId,
                destinationInventory
        );

        if (transferred) {
            removeFinishedMarker(id);
        }
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
        cm.add(
                entityId,
                cfm.create(
                        new InventoryComponent.Args(
                                inventory.getSize(),
                                inventory.getStackSize(),
                                slots.stream()
                                        .map(slot ->
                                                new InventoryComponent.ReadableSlot(
                                                        ResourceType.UNDEFINED
                                                                .getByOrdinal(
                                                                        slot.resource()
                                                                ),
                                                        slot.count()
                                                )
                                        )
                                        .toList()
                        )
                )
        );
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

    private void validatePorts(
            long id,
            LogisticPort logisticPort
    ) {
        if (logisticPort.getPorts().size() != 2) {
            throw new IllegalStateException(
                    "Манипулятор " + id
                            + " должен иметь ровно 2 порта"
            );
        }

        findPort(logisticPort, PortType.INPUT);
        findPort(logisticPort, PortType.OUTPUT);
    }

    // FRONT = +Y, BACK = -Y
    private int[] resolvePortPosition(
            PositionComponent position,
            BuildingComponent building,
            LogisticPort.Port port
    ) {
        int x = position.getX();
        int y = position.getY();

        return switch (
                PortSide.UNDEFINED.getByOrdinal(port.side())
                ) {
            case BACK -> new int[]{
                    x,
                    y - 1
            };

            case FRONT -> new int[]{
                    x,
                    y + building.getHeight()
            };

            case LEFT -> new int[]{
                    x - 1,
                    y
            };

            case RIGHT -> new int[]{
                    x + building.getWidth(),
                    y
            };

            default -> throw new IllegalArgumentException(
                    "Неизвестная сторона порта: "
                            + port.side()
            );
        };
    }

    private void removeFinishedMarker(long id) {
        cm.remove(
                id,
                OperationFinishedMarkerComponent.class
        );
    }
/*
    //FRONT = +Y, BACK = -Y
    private int[] resolvePortPosition(
            PositionComponent position,
            BuildingComponent building,
            LogisticPort.Port port
    ) {
        int x = position.getX();
        int y = position.getY();

        return switch (
                PortSide.UNDEFINED.getByOrdinal(port.side())
                ) {
            case BACK -> new int[]{
                    x,
                    y - 1
            };

            case FRONT -> new int[]{
                    x,
                    y + building.getHeight()
            };

            case LEFT -> new int[]{
                    x - 1,
                    y
            };

            case RIGHT -> new int[]{
                    x + building.getWidth(),
                    y
            };

            default -> throw new IllegalArgumentException(
                    "Неизвестная сторона порта: "
                            + port.side()
            );
        };
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
                                "Не найден порт " + requiredType
                        )
                );
    }

    private void validatePorts(
            long id,
            LogisticPort logisticPort
    ) {
        if (logisticPort.getPorts().size() != 2) {
            throw new IllegalStateException(
                    "Манипулятор " + id +
                            " должен иметь ровно 2 порта"
            );
        }
    }

    private boolean transferOneStack(
            InserterComponent inserter,
            long sourceId,
            InventoryComponent source,
            long destinationId,
            InventoryComponent destination
    ) {
        InventoryComponent.Slot sourceSlot =
                findFirstNonEmptySlot(source);

        if (sourceSlot == null) {
            return false;
        }

        ResourceType resourceType =
                ResourceType.UNDEFINED.getByOrdinal(
                        sourceSlot.resource()
                );

        if (resourceType == ResourceType.UNDEFINED) {
            return false;
        }

        int freeSpace =
                getFreeSpace(
                        destination,
                        resourceType
                );

        if (freeSpace <= 0) {
            return false;
        }

        int amount = Math.min(
                inserter.getStackSize(),
                Math.min(
                        sourceSlot.count(),
                        freeSpace
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

    private InventoryComponent.Slot findFirstNonEmptySlot(
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

                return Math.max(
                        0,
                        inventory.getStackSize() - slot.count()
                );
            }
        }

        *//*
     * Такого ресурса ещё нет.
     * Проверяем наличие свободного слота.
     *//*
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
        var slots = inventory.getSlots()
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
        var slots =
                new java.util.ArrayList<>(
                        inventory.getSlots()
                );

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
            java.util.List<InventoryComponent.Slot> slots
    ) {
        cm.add(
                entityId,
                cfm.create(
                        new InventoryComponent.Args(
                                inventory.getSize(),
                                inventory.getStackSize(),
                                slots.stream()
                                        .map(slot ->
                                                new InventoryComponent.ReadableSlot(
                                                        ResourceType.UNDEFINED
                                                                .getByOrdinal(
                                                                        slot.resource()
                                                                ),
                                                        slot.count()
                                                )
                                        )
                                        .toList()
                        )
                )
        );
    }

    private void removeOperationFinished(long id) {
        cm.remove(
                id,
                OperationFinishedMarkerComponent.class
        );
    }*/
}
