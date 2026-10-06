package ru.phyllosedis.textario.inventory;

import ru.phyllosedis.textario.engine.ecs.ComponentFactoryRegistry;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.inventory.InventoryComponent.ReadableSlot;
import ru.phyllosedis.textario.inventory.InventoryComponent.Slot;
import ru.phyllosedis.textario.resource.ResourceType;

import java.util.List;

/**
 * Доступ к складам сущности: источник — сначала выходной склад,
 * потом обычный. Запись сама выбирает правильный тип Args,
 * чтобы не затереть один склад другим.
 */
public final class InventoryAccess {

    private InventoryAccess() {
    }

    public static InventoryComponent sourceInventory(ComponentManager cm, long id) {
        OutputInventoryComponent out = cm.get(id, OutputInventoryComponent.class);
        if (out != null && !out.getSlots().isEmpty()) {
            return out;
        }
        return cm.get(id, InventoryComponent.class);
    }

    public static void writeInventory(ComponentManager cm, ComponentFactoryRegistry cfm,
                                      long entityId, InventoryComponent inv, List<Slot> slots) {
        List<ReadableSlot> readable = slots.stream()
                .map(s -> new ReadableSlot(
                        ResourceType.UNDEFINED.getByOrdinal(s.resource()), s.count()))
                .toList();
        if (inv instanceof OutputInventoryComponent) {
            cm.add(entityId, cfm.create(
                    new OutputInventoryComponent.Args(inv.getSize(), inv.getStackSize(), readable)));
        } else {
            cm.add(entityId, cfm.create(
                    new InventoryComponent.Args(inv.getSize(), inv.getStackSize(), readable)));
        }
    }
}
