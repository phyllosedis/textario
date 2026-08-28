package ru.phyllosedis.textario.production.mining;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.engine.ecs.component.Component;
import ru.phyllosedis.textario.engine.ecs.component.AutoFactory;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.component.ComponentType;
import ru.phyllosedis.textario.resource.ResourceType;

@Getter
@ToString
@AutoFactory(ComponentType.MINING)
public class MiningComponent extends Component {

    private final int resourceType;

    MiningComponent(int resourceType) {
        super();
        this.resourceType = resourceType;
    }

    public record Args(ResourceType resourceType) implements ComponentArgs<MiningComponent> {
        @Override
        public MiningComponent instantiate() {
            return new MiningComponent(resourceType.ordinal());
        }
    }

}
