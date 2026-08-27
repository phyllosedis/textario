package ru.phyllosedis.textario.system.inventory.solid;

import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.component.impl.meta.logistic.ContentStateComponent;
import ru.phyllosedis.textario.component.impl.meta.product.DispatchedProductComponent;
import ru.phyllosedis.textario.system.inventory.InventorySystem;
import ru.phyllosedis.textario.type.ContentState;
import ru.phyllosedis.textario.type.ResourceType;


public abstract class SolidToInventorySystem extends InventorySystem {
    protected SolidToInventorySystem(ComponentFactoryManager cfm, ComponentManager cm) {
        super(cfm, cm);
    }

    @Override
    protected void updateEntity(long id) {
        ContentStateComponent state = cm.get(id, ContentStateComponent.class);

        // Обрабатываем ТОЛЬКО твердые предметы (буры, заводы)
        if (ContentState.UNDEFINED.getByOrdinal(state.getContentType()) != ContentState.SOLID) return;

        DispatchedProductComponent dispatched = cm.get(id, DispatchedProductComponent.class);

        // Используем твой метод insertItem из InventorySystem!
        boolean success = insertItem(id, ResourceType.UNDEFINED.getByOrdinal(dispatched.getResourceType()));

        if (success) {
            // Если успешно переложили в инвентарь — очищаем буфер выдачи
            cm.remove(id, DispatchedProductComponent.class);
        } else {
            System.out.println("[Бур #" + id + "] Внутренний инвентарь забит, руда ждет выгрузки манипулятором!");
            // Буфер НЕ удаляем. Бур остановится (потому что ProductionProgressSystem можно научить не тикать, если буфер не пуст)
        }
    }
}
