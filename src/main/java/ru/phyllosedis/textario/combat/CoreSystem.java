package ru.phyllosedis.textario.combat;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.ecs.ComponentFactoryRegistry;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.ecs.component.Requires;
import ru.phyllosedis.textario.engine.ecs.system.AbstractSystem;
import ru.phyllosedis.textario.engine.events.GameOverEvent;
import ru.phyllosedis.textario.engine.loop.TickGate;
import ru.phyllosedis.textario.resource.SystemOrder;

import java.util.HashSet;
import java.util.Set;
import org.springframework.context.ApplicationEventPublisher;

@Requires({CoreMarkerComponent.class, HealthComponent.class})
@Component
@Order(SystemOrder.CORE)
public class CoreSystem extends AbstractSystem {

    private final TickGate tickGate;
    private final WaveService waves;
    private final ApplicationEventPublisher publisher;
    private final Set<Long> announced = new HashSet<>();

    public CoreSystem(ComponentFactoryRegistry cfm, ComponentManager cm, TickGate tickGate,
                      WaveService waves, ApplicationEventPublisher publisher) {
        super(cfm, cm);
        this.tickGate = tickGate;
        this.waves = waves;
        this.publisher = publisher;
    }

    @Override
    protected void updateEntity(long id) {
        HealthComponent health = cm.get(id, HealthComponent.class);
        if (health == null || health.getHp() > 0 || !announced.add(id)) {
            return;
        }
        tickGate.setPaused(true);
        publisher.publishEvent(new GameOverEvent(id, waves.currentWave()));
    }
}
