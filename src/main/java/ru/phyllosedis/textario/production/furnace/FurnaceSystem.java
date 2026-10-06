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
import ru.phyllosedis.textario.production.recipe.RecipeBook;
import ru.phyllosedis.textario.production.station.OperationFinishedMarkerComponent;
import ru.phyllosedis.textario.resource.ResourceType;
import ru.phyllosedis.textario.resource.SystemOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Requires({FurnaceComponent.class, OperationFinishedMarkerComponent.class, InventoryComponent.class})
@Component
@Order(SystemOrder.FURNACE)
public class FurnaceSystem extends AbstractSystem {

    private final RecipeBook book;

    public FurnaceSystem(ComponentFactoryRegistry cfm, ComponentManager cm, RecipeBook book) {
        super(cfm, cm);
        this.book = book;
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
            idle(id);
            return;
        }

        ResourceType ore = ResourceType.UNDEFINED.getByOrdinal(oreSlot.resource());
        RecipeBook.Recipe recipe = book.furnaceRecipeFor(ore).orElse(null);
        if (recipe == null) {
            idle(id);
            return;
        }
        Map.Entry<ResourceType, Integer> output = recipe.outputs().entrySet().iterator().next();
        int need = recipe.inputs().getOrDefault(ore, 1);

        // Дозаправка: жрём уголь из своего же склада.
        // Инвентарь при этом пересоздаётся, поэтому слот руды перечитываем.
        int heat = heatOf(id);
        InventoryComponent inv = cm.get(id, InventoryComponent.class);
        while (heat < Math.min(cycles, oreSlot.count() / need)) {
            InventoryComponent.Slot fuelSlot = findFuelSlot(inv);
            if (fuelSlot == null) {
                break;
            }
            ResourceType fuelType = ResourceType.UNDEFINED.getByOrdinal(fuelSlot.resource());
            consume(id, inv, fuelSlot, 1);
            heat += book.fuelValue(fuelType);
            inv = cm.get(id, InventoryComponent.class);
        }

        oreSlot = findSmeltableSlot(inv);
        if (oreSlot == null) {
            cm.add(id, cfm.create(new FuelComponent.Args(heat)));
            idle(id);
            return;
        }
        int plates = Math.min(cycles, oreSlot.count() / need);
        plates = Math.min(plates, heat);

        if (plates <= 0) {
            cm.add(id, cfm.create(new FuelComponent.Args(heat)));
            idle(id);
            return;
        }

        consume(id, inv, oreSlot, need * plates);
        cm.add(id, cfm.create(new FuelComponent.Args(heat - plates)));
        cm.add(id, cfm.create(new DispatchedProductComponent.Args(output.getKey(), output.getValue() * plates)));
        cm.add(id, cfm.create(new ProgressComponent.Args(0.0)));
        cm.remove(id, OperationFinishedMarkerComponent.class);
    }

    private void idle(long id) {
        cm.add(id, cfm.create(new ProgressComponent.Args(0.0)));
        cm.remove(id, OperationFinishedMarkerComponent.class);
    }

    private int heatOf(long id) {
        FuelComponent fuel = cm.get(id, FuelComponent.class);
        return fuel == null ? 0 : fuel.getHeat();
    }

    private InventoryComponent.Slot findFuelSlot(InventoryComponent inventory) {
        return inventory.getSlots().stream()
                .filter(slot -> slot.count() > 0)
                .filter(slot -> book.fuelValue(
                        ResourceType.UNDEFINED.getByOrdinal(slot.resource())) > 0)
                .findFirst()
                .orElse(null);
    }

    private InventoryComponent.Slot findSmeltableSlot(InventoryComponent inventory) {
        return inventory.getSlots().stream()
                .filter(slot -> slot.count() > 0)
                .filter(slot -> book.furnaceRecipeFor(
                        ResourceType.UNDEFINED.getByOrdinal(slot.resource())).isPresent())
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
