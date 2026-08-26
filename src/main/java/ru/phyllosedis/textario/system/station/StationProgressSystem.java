package ru.phyllosedis.textario.system.station;

import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.component.impl.meta.marker.station.OperationFinishedMarkerComponent;
import ru.phyllosedis.textario.component.impl.meta.station.StationComponent;
import ru.phyllosedis.textario.system.AbstractSystem;
import ru.phyllosedis.textario.system.Requires;

@Requires({StationComponent.class})
public abstract class StationProgressSystem extends AbstractSystem {

    public StationProgressSystem(ComponentFactoryManager cfm, ComponentManager cm) {
        super(cfm, cm);
    }

    @Override
    protected void updateEntity(long id) {
        StationComponent station = cm.get(id, StationComponent.class);
        double newProgress = station.getProgress() + station.getSpeed();
        boolean isFinished = newProgress >= 100.0;

        if (isFinished) {
            int completedCycles = (int) (newProgress / 100.0);
            newProgress %= 100.0;
        }

        cm.add(id, cfm.create(new StationComponent.Args(station.getSpeed(), newProgress)));

        if (isFinished) {
            cm.add(id, cfm.create(new OperationFinishedMarkerComponent.Args(/*completedCycles*/)));
        }
    }
}
