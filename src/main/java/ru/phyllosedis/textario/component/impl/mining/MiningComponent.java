package ru.phyllosedis.textario.component.impl.mining;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.component.Component;
import ru.phyllosedis.textario.component.factory.ComponentArgs;
import ru.phyllosedis.textario.type.ComponentType;
import ru.phyllosedis.textario.type.ResourceType;

@Getter
@ToString
public class MiningComponent extends Component {

    private final int resourceType;
    private final int speed;
    private final int progress;

    MiningComponent(int resourceType, int speed, int progress) {
        super(ComponentType.MINING);
        this.resourceType = resourceType;
        this.speed = speed;
        this.progress = progress;
    }

    public record Args(ResourceType resourceType, int speed, int progress) implements ComponentArgs<MiningComponent> {
    }

}
