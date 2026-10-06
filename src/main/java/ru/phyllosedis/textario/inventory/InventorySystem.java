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
        InventoryComponent inv = cm.get(id, InventoryComponent.class);
        if (inv == null) {
            return false;
        }
        List<InventoryComponent.Slot> next = withAdded(inv, resType);
        if (next == null) {
            return false;
        }
        cm.add(id, cfm.create(new InventoryComponent.Args(
                inv.getSize(), inv.getStackSize(), toReadable(next))));
        return true;
    }

    /**
     * То же, но в выходной склад станции.
     */
    protected boolean insertOutputItem(long id, ResourceType resType) {
        OutputInventoryComponent inv = cm.get(id, OutputInventoryComponent.class);
        if (inv == null) {
            return false;
        }
        List<InventoryComponent.Slot> next = withAdded(inv, resType);
        if (next == null) {
            return false;
        }
        cm.add(id, cfm.create(new OutputInventoryComponent.Args(
                inv.getSize(), inv.getStackSize(), toReadable(next))));
        return true;
    }

    protected int insertOutputStack(long id, ResourceType resType, int count) {
        int inserted = 0;
        for (int i = 0; i < count; i++) {
            if (insertOutputItem(id, resType)) {
                inserted++;
            } else {
                break;
            }
        }
        return inserted;
    }

    private static List<InventoryComponent.Slot> withAdded(InventoryComponent inv, ResourceType resType) {
        int currentStackLimit = inv.getStackSize();
        List<InventoryComponent.ReadableSlot> readableSlots = new ArrayList<>();
        boolean addedToExisting = false;

        for (InventoryComponent.Slot slot : inv.getSlots()) {
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
            if (inv.getSlots().size() >= inv.getSize()) {
                return null;
            }
            readableSlots.add(new InventoryComponent.ReadableSlot(resType, 1));
        }

        return readableSlots.stream()
                .map(e -> new InventoryComponent.Slot(e.type().ordinal(), e.count()))
                .toList();
    }

    private static List<InventoryComponent.ReadableSlot> toReadable(List<InventoryComponent.Slot> slots) {
        return slots.stream()
                .map(e -> new InventoryComponent.ReadableSlot(
                        ResourceType.UNDEFINED.getByOrdinal(e.resource()), e.count()))
                .toList();
    }

    /**
     * Кладёт count предметов пачкой, возвращает сколько влезло.
     */
    protected int insertStack(long id, ResourceType resType, int count) {
        int inserted = 0;
        for (int i = 0; i < count; i++) {
            if (insertItem(id, resType)) {
                inserted++;
            } else {
                break;
            }
        }
        return inserted;
    }
}
