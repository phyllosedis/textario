package ru.phyllosedis.textario.logistics.belt;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.engine.ecs.component.AutoFactory;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.component.ComponentType;
import ru.phyllosedis.textario.logistics.LogisticComponent;

@Getter
@ToString
@AutoFactory(ComponentType.LOGISTIC)
public class BeltComponent extends LogisticComponent {

    private final double speed;
    private final double throughput;

    protected BeltComponent(double speed, double throughput) {
        super();
        this.speed = speed;
        this.throughput = throughput;
    }

    public record Args(double speed, double throughput) implements ComponentArgs<BeltComponent> {
        @Override
        public BeltComponent instantiate() {
            return new BeltComponent(speed, throughput);
        }
    }
}
