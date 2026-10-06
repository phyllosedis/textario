package ru.phyllosedis.textario.production.furnace;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.ecs.ComponentFactoryRegistry;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.ecs.component.Requires;
import ru.phyllosedis.textario.engine.ecs.system.AbstractSystem;
import ru.phyllosedis.textario.inventory.InventoryComponent;
import ru.phyllosedis.textario.production.DispatchedProductComponent;
import ru.phyllosedis.textario.production.ProgressComponent;
import ru.phyllosedis.textario.production.station.OperationFinishedMarkerComponent;
import ru.phyllosedis.textario.resource.ResourceType;
import ru.phyllosedis.textario.resource.SystemOrder;

import java.util.ArrayList;
import java.util.List;

@Requires({FurnaceComponent.class, OperationFinishedMarkerComponent.class, InventoryComponent.class})
@Component
@Order(SystemOrder.FURNACE)
public class FurnaceSystem extends AbstractSystem {

    private final FurnaceRecipes recipes;

    public FurnaceSystem(ComponentFactoryRegistry cfm, ComponentManager cm, FurnaceRecipes recipes) {
        super(cfm, cm);
        this.recipes = recipes;
    }

    @Override
    protected void updateEntity(long id) {
        OperationFinishedMarkerComponent finished = cm.get(id, OperationFinishedMarkerComponent.class);
        InventoryComponent inventory = cm.get(id, InventoryComponent.class);
        if (finished == null || inventory == null) {
            return;
        }

        int cycles = finished.getCompletedCycles();
        InventoryComponent.Slot oreSlot = findSmeltableSlot(inventory);

        if (oreSlot == null) {
            // Нечего плавить — холостой цикл
            cm.add(id, cfm.create(new ProgressComponent.Args(0.0)));
            cm.remove(id, OperationFinishedMarkerComponent.class);
            return;
        }

        ResourceType ore = ResourceType.UNDEFINED.getByOrdinal(oreSlot.resource());
        ResourceType plate = recipes.outputFor(ore).orElse(ResourceType.UNDEFINED);
        int plates = Math.min(cycles, oreSlot.count());

        consume(id, inventory, oreSlot, plates);
        cm.add(id, cfm.create(new DispatchedProductComponent.Args(plate, plates)));
        cm.add(id, cfm.create(new ProgressComponent.Args(0.0)));
        cm.remove(id, OperationFinishedMarkerComponent.class);
    }

    private InventoryComponent.Slot findSmeltableSlot(InventoryComponent inventory) {
        return inventory.getSlots().stream()
                .filter(slot -> slot.count() > 0)
                .filter(slot -> recipes.isSmeltable(ResourceType.UNDEFINED.getByOrdinal(slot.resource())))
                .findFirst()
                .orElse(null);
    }

    private void consume(long id, InventoryComponent inventory, InventoryComponent.Slot slot, int amount) {
        List<InventoryComponent.Slot> slots = new ArrayList<>();
        for (InventoryComponent.Slot s : inventory.getSlots()) {
            if (s == slot) {
                int rest = s.count() - amount;
                if (rest > 0) {
                    slots.add(new InventoryComponent.Slot(s.resource(), rest));
                }
            } else {
                slots.add(s);
            }
        }
        cm.add(id, cfm.create(new InventoryComponent.Args(
                inventory.getSize(), inventory.getStackSize(),
                slots.stream()
                        .map(s -> new InventoryComponent.ReadableSlot(
                                ResourceType.UNDEFINED.getByOrdinal(s.resource()), s.count()))
                        .toList())));
    }
}
