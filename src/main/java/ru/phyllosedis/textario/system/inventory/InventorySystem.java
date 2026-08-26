package ru.phyllosedis.textario.system.inventory;

import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.component.impl.inventory.InventoryComponent;
import ru.phyllosedis.textario.component.impl.meta.logistic.ContentStateComponent;
import ru.phyllosedis.textario.component.impl.meta.station.product.DispatchedProductComponent;
import ru.phyllosedis.textario.system.AbstractSystem;
import ru.phyllosedis.textario.system.Requires;
import ru.phyllosedis.textario.type.ResourceType;

import java.util.ArrayList;
import java.util.List;

@Requires({InventoryComponent.class, ContentStateComponent.class, DispatchedProductComponent.class})
public abstract class InventorySystem extends AbstractSystem {
    protected InventorySystem(ComponentFactoryManager cfm, ComponentManager cm) {
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
