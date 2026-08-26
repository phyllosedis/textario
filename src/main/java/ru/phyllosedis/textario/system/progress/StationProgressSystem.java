package ru.phyllosedis.textario.system.progress;

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

        // Дробная математика на double
        double boostSpeed = station.getSpeed() + 50;
        double newProgress = station.getProgress() + boostSpeed;

        boolean isFinished = false;
        if (newProgress >= 100.0) {
            newProgress = 0.0;
            isFinished = true;
        }

        // Обновляем исключительно StationComponent
        cm.add(id, cfm.create(new StationComponent.Args(station.getSpeed(), newProgress)));

        // Если цикл завершен — вешаем маркер готовности продукта
        if (isFinished) {
            cm.add(id, cfm.create(new OperationFinishedMarkerComponent.Args()));
        }
    }
}
