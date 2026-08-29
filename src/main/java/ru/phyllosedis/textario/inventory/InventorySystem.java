package ru.phyllosedis.textario.inventory;

import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.ecs.ComponentFactoryRegistry;
import ru.phyllosedis.textario.logistics.ContentStateComponent;
import ru.phyllosedis.textario.production.DispatchedProductComponent;
import ru.phyllosedis.textario.engine.ecs.system.AbstractSystem;
import ru.phyllosedis.textario.engine.ecs.component.Requires;
import ru.phyllosedis.textario.resource.ResourceType;

import java.util.ArrayList;
import java.util.List;

@Requires({InventoryComponent.class, ContentStateComponent.class, DispatchedProductComponent.class})
public abstract class InventorySystem extends AbstractSystem {
    protected InventorySystem(ComponentFactoryRegistry cfm, ComponentManager cm) {
        super(cfm, cm);
    }

    /**
     * Внутренний хелпер для наследников: проверяет место и добавляет предмет.
     * Возвращает true, если предмет поместился.
     */
    protected boolean insertItem(long id, ResourceType resType) {
        InventoryComponent oldInv = cm.get(id, InventoryComponent.class);
        int currentStackLimit = oldInv.getStackSize();

        boolean hasSpace = false;
        for (InventoryComponent.Slot slot : oldInv.getSlots()) {
            if (slot.resource() == resType.ordinal() && slot.count() < currentStackLimit) {
                hasSpace = true;
                break;
            }
        }
        if (!hasSpace && oldInv.getSlots().size() < oldInv.getSize()) {
            hasSpace = true;
        }

        if (!hasSpace) return false;

        List<InventoryComponent.ReadableSlot> readableSlots = new ArrayList<>();
        boolean addedToExisting = false;

        for (InventoryComponent.Slot slot : oldInv.getSlots()) {
            ResourceType type = ResourceType.UNDEFINED.getByOrdinal(slot.resource());
            int count = slot.count();

            if (!addedToExisting && type == resType && count < currentStackLimit) {
                readableSlots.add(new InventoryComponent.ReadableSlot(type, count + 1));
                addedToExisting = true;
            } else {
                readableSlots.add(new InventoryComponent.ReadableSlot(type, count));
            }
        }

        if (!addedToExisting) {
            readableSlots.add(new InventoryComponent.ReadableSlot(resType, 1));
        }

        cm.add(id, cfm.create(new InventoryComponent.Args(oldInv.getSize(), oldInv.getStackSize(), readableSlots)));
        return true;
    }
}
