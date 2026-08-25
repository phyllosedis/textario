package ru.phyllosedis.textario.component.impl.mining;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.component.Component;
import ru.phyllosedis.textario.component.factory.ComponentArgs;
import ru.phyllosedis.textario.type.ComponentType;
import ru.phyllosedis.textario.type.Grade;
import ru.phyllosedis.textario.type.ResourceType;

@Getter
@ToString
public class MiningComponent extends Component {

    private final int resourceType;
    private final int speed;
    private final int progress;
    private final int tier;

    MiningComponent(int resourceType, int speed, int progress, int tier) {
        super(ComponentType.MINING);
        this.resourceType = resourceType;
        this.speed = speed;
        this.progress = progress;
        this.tier = tier;
    }

    public record Args(ResourceType resourceType, int speed, int progress, Grade tier) implements ComponentArgs<MiningComponent> {
    }

}
