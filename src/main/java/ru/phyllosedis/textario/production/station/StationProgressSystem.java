package ru.phyllosedis.textario.production.station;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.ecs.ComponentFactoryRegistry;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.ecs.component.Requires;
import ru.phyllosedis.textario.engine.ecs.system.AbstractSystem;
import ru.phyllosedis.textario.production.DispatchedProductComponent;
import ru.phyllosedis.textario.production.ProduceSpeedComponent;
import ru.phyllosedis.textario.production.ProgressComponent;
import ru.phyllosedis.textario.resource.SystemOrder;

@Order(SystemOrder.STATION)
@Component
@Requires({StationMarkerComponent.class, ProgressComponent.class, ProduceSpeedComponent.class})
public final class StationProgressSystem extends AbstractSystem {

    public StationProgressSystem(ComponentFactoryRegistry cfm, ComponentManager cm) {
        super(cfm, cm);
    }

    @Override
    protected void updateEntity(long id) {
        ProgressComponent progress = cm.get(id, ProgressComponent.class);
        ProduceSpeedComponent produceSpeedComponent = cm.get(id, ProduceSpeedComponent.class);

        double newProgress = progress.getProgress() + produceSpeedComponent.getSpeed();
        boolean isFinished = newProgress >= 100.0;

        int completedCycles = 0;
        if (isFinished) {
            completedCycles = (int) (newProgress / 100.0);
            newProgress %= 100.0;
        }

        cm.add(id, cfm.create(new ProgressComponent.Args(newProgress)));

        if (isFinished) {
            cm.add(id, cfm.create(new OperationFinishedMarkerComponent.Args(completedCycles)));
            cm.add(id, cfm.create(new DispatchedProductComponent.Args(completedCycles)));
        }
    }
}
