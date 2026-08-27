package ru.phyllosedis.textario.system.station;

import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.component.impl.meta.marker.station.OperationFinishedMarkerComponent;
import ru.phyllosedis.textario.component.impl.meta.marker.station.StationComponent;
import ru.phyllosedis.textario.component.impl.mining.ProgressComponent;
import ru.phyllosedis.textario.system.AbstractSystem;
import ru.phyllosedis.textario.system.Requires;

@Requires({StationComponent.class, ProgressComponent.class})
public abstract class StationProgressSystem extends AbstractSystem {

    public StationProgressSystem(ComponentFactoryManager cfm, ComponentManager cm) {
        super(cfm, cm);
    }

    @Override
    protected void updateEntity(long id) {
        ProgressComponent progress = cm.get(id, ProgressComponent.class);

        double newProgress = progress.getProgress() + getSpeed(id);
        boolean isFinished = newProgress >= 100.0;

        if (isFinished) {
            int completedCycles = (int) (newProgress / 100.0);
            newProgress %= 100.0;
        }

        cm.add(id, cfm.create(new ProgressComponent.Args(newProgress)));

        if (isFinished) {
            cm.add(id, cfm.create(new OperationFinishedMarkerComponent.Args(/*completedCycles*/)));
        }
    }

    protected abstract double getSpeed(long id);
}
