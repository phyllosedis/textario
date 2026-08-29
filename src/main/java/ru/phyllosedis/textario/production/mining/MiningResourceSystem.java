package ru.phyllosedis.textario.production.mining;

import ru.phyllosedis.textario.engine.ecs.ComponentFactoryRegistry;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.ecs.component.Requires;
import ru.phyllosedis.textario.engine.ecs.system.AbstractSystem;
import ru.phyllosedis.textario.inventory.InventoryComponent;
import ru.phyllosedis.textario.production.DispatchedProductComponent;
import ru.phyllosedis.textario.production.ProgressComponent;
import ru.phyllosedis.textario.production.station.OperationFinishedMarkerComponent;
import ru.phyllosedis.textario.production.station.TierMarkerComponent;
import ru.phyllosedis.textario.resource.ResourceType;
import ru.phyllosedis.textario.world.PositionComponent;

@Requires({
        MiningComponent.class,
        OperationFinishedMarkerComponent.class,
        TierMarkerComponent.class,
        PositionComponent.class,
        DispatchedProductComponent.class,
        InventoryComponent.class
})
public abstract class MiningResourceSystem extends AbstractSystem {

    public MiningResourceSystem(ComponentFactoryRegistry cfm, ComponentManager cm) {
        super(cfm, cm);
    }

    @Override
    protected void updateEntity(long id) {
        MiningComponent mining = cm.get(id, MiningComponent.class);
        ResourceType resType = ResourceType.UNDEFINED.getByOrdinal(mining.getResourceType());

        DispatchedProductComponent dispatchedProductComponent = cm.get(id, DispatchedProductComponent.class);
        InventoryComponent inventory = cm.get(id, InventoryComponent.class);
        int count = dispatchedProductComponent.getCount();


        // переназначение произведённого продукта с определением типа продукта
//        cm.remove(id, DispatchedProductComponent.class);
//        cm.add(id, cfm.create(new DispatchedProductComponent.Args(resType, count)));

//        inventory.

        cm.add(id, cfm.create(new ProgressComponent.Args(0.0)));

        onComplete(id, resType, dispatchedProductComponent.getCount());

        cm.remove(id, OperationFinishedMarkerComponent.class);
    }

    protected void onComplete(long id, ResourceType resType, int count) {
        System.out.println("Станция " + id + " завершила добычу (кол-во предметов: " + count + ") тип предмета " + resType + " агрегатное состояние предмета " + resType.getState());
    }

}
