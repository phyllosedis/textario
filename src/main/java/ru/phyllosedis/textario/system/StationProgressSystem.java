package ru.phyllosedis.textario.system;

import ru.phyllosedis.textario.engine.spring.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.spring.ecs.ComponentFactoryManager;
import ru.phyllosedis.textario.production.station.OperationFinishedMarkerComponent;
import ru.phyllosedis.textario.production.station.StationMarkerComponent;
import ru.phyllosedis.textario.production.ProgressComponent;
import ru.phyllosedis.textario.engine.ecs.component.Requires;

@Requires({StationMarkerComponent.class, ProgressComponent.class})
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
