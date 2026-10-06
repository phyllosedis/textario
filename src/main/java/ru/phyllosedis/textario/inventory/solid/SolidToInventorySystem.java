package ru.phyllosedis.textario.inventory.solid;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.ecs.ComponentFactoryRegistry;
import ru.phyllosedis.textario.engine.ecs.component.Requires;
import ru.phyllosedis.textario.inventory.InventoryComponent;
import ru.phyllosedis.textario.inventory.InventorySystem;
import ru.phyllosedis.textario.inventory.OutputInventoryComponent;
import ru.phyllosedis.textario.logistics.ContentStateComponent;
import ru.phyllosedis.textario.production.DispatchedProductComponent;
import ru.phyllosedis.textario.inventory.InventorySystem;
import ru.phyllosedis.textario.resource.ContentState;
import ru.phyllosedis.textario.resource.ResourceType;
import ru.phyllosedis.textario.resource.SystemOrder;

@Component
@Order(SystemOrder.INVENTORY)
@Requires({InventoryComponent.class, ContentStateComponent.class, DispatchedProductComponent.class})
public class SolidToInventorySystem extends InventorySystem {
    protected SolidToInventorySystem(ComponentFactoryRegistry cfm, ComponentManager cm) {
        super(cfm, cm);
    }

    @Override
    protected void updateEntity(long id) {
        ContentStateComponent state = cm.get(id, ContentStateComponent.class);

        // Обрабатываем ТОЛЬКО твердые предметы (буры, заводы)
        if (state == null || ContentState.UNDEFINED.getByOrdinal(state.getContentState()) != ContentState.SOLID) return;

        DispatchedProductComponent dispatched = cm.get(id, DispatchedProductComponent.class);
        if (dispatched == null) return;

        ResourceType resType = ResourceType.UNDEFINED.getByOrdinal(dispatched.getResource());
        // У станций с выходным складом продукция идёт туда, иначе в обычный
        boolean useOutput = cm.has(id, OutputInventoryComponent.class);
        int inserted = 0;
        for (int i = 0; i < dispatched.getCount(); i++) {
            boolean ok = useOutput ? insertOutputItem(id, resType) : insertItem(id, resType);
            if (ok) {
                inserted++;
            } else {
                break;
            }
        }

        if (inserted >= dispatched.getCount()) {
            // Всё влезло — очищаем буфер выдачи
            cm.remove(id, DispatchedProductComponent.class);
        } else if (inserted > 0) {
            // Влезло частично — обновляем остаток
            cm.add(id, cfm.create(new DispatchedProductComponent.Args(resType, dispatched.getCount() - inserted)));
        }
        // Ничего не влезло — буфер остаётся, станция стоит (противодавление)
    }
}
