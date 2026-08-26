package ru.phyllosedis.textario.component.impl.mining;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.component.Component;
import ru.phyllosedis.textario.component.factory.AutoFactory;
import ru.phyllosedis.textario.component.factory.ComponentArgs;
import ru.phyllosedis.textario.type.ComponentType;
import ru.phyllosedis.textario.type.ResourceType;

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
