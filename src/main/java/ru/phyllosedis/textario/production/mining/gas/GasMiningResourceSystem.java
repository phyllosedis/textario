package ru.phyllosedis.textario.production.mining.gas;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.engine.spring.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.spring.ecs.ComponentFactoryManager;
import ru.phyllosedis.textario.engine.ecs.component.Requires;
import ru.phyllosedis.textario.production.mining.MiningResourceSystem;
import ru.phyllosedis.textario.resource.ResourceType;

@Requires({GasStateMarkerComponent.class})
@Component
@Order(10)
public class GasMiningResourceSystem extends MiningResourceSystem {

    public GasMiningResourceSystem(ComponentFactoryManager cfm, ComponentManager cm) {
        super(cfm, cm);
    }

    @Override
    protected void onComplete(long id, ResourceType resType) {
        System.out.println("[Добыча газа] Сущность " + id + " качает " + resType + " со скоростью ");
    }
}
