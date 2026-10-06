package ru.phyllosedis.textario.production.assembler;

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

@Requires({AssemblerComponent.class, OperationFinishedMarkerComponent.class, InventoryComponent.class})
@Component
@Order(SystemOrder.ASSEMBLER)
public class AssemblerSystem extends AbstractSystem {

    private final RecipeBook book;

    public AssemblerSystem(ComponentFactoryRegistry cfm, ComponentManager cm, RecipeBook book) {
        super(cfm, cm);
        this.book = book;
    }

    @Override
    protected void updateEntity(long id) {
        OperationFinishedMarkerComponent finished = cm.get(id, OperationFinishedMarkerComponent.class);
        AssemblerComponent assembler = cm.get(id, AssemblerComponent.class);
        InventoryComponent inventory = cm.get(id, InventoryComponent.class);
        if (finished == null || assembler == null || inventory == null) {
            return;
        }

        RecipeBook.Recipe recipe = book.byId(assembler.getRecipeId()).orElse(null);
        if (recipe == null || recipe.station() != RecipeBook.Station.ASSEMBLER) {
            idle(id);
            return;
        }

        int crafts = finished.getCompletedCycles();
        for (Map.Entry<ResourceType, Integer> need : recipe.inputs().entrySet()) {
            int have = countOf(inventory, need.getKey());
            crafts = Math.min(crafts, have / need.getValue());
        }
        if (crafts <= 0) {
            idle(id);
            return;
        }

        for (Map.Entry<ResourceType, Integer> need : recipe.inputs().entrySet()) {
            inventory = cm.get(id, InventoryComponent.class);
            consume(id, inventory, need.getKey(), need.getValue() * crafts);
        }
        Map.Entry<ResourceType, Integer> output = recipe.outputs().entrySet().iterator().next();
        cm.add(id, cfm.create(new DispatchedProductComponent.Args(
                output.getKey(), output.getValue() * crafts)));
        cm.add(id, cfm.create(new ProgressComponent.Args(0.0)));
        cm.remove(id, OperationFinishedMarkerComponent.class);
    }

    private void idle(long id) {
        cm.add(id, cfm.create(new ProgressComponent.Args(0.0)));
        cm.remove(id, OperationFinishedMarkerComponent.class);
    }

    private int countOf(InventoryComponent inventory, ResourceType type) {
        return inventory.getSlots().stream()
                .filter(s -> s.resource() == type.ordinal())
                .mapToInt(InventoryComponent.Slot::count)
                .sum();
    }

    private void consume(long id, InventoryComponent inventory, ResourceType type, int amount) {
        List<InventoryComponent.Slot> slots = new ArrayList<>();
        int rest = amount;
        for (InventoryComponent.Slot s : inventory.getSlots()) {
            if (s.resource() == type.ordinal() && rest > 0) {
                int left = s.count() - Math.min(rest, s.count());
                rest -= Math.min(rest, s.count());
                if (left > 0) {
                    slots.add(new InventoryComponent.Slot(s.resource(), left));
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
