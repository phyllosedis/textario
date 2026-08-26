package ru.phyllosedis.textario.component.impl.meta.logistic.transport.impl;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.component.factory.AutoFactory;
import ru.phyllosedis.textario.component.factory.ComponentArgs;
import ru.phyllosedis.textario.component.impl.meta.logistic.transport.ItemTransportComponent;
import ru.phyllosedis.textario.type.ComponentType;

@Getter
@ToString
@AutoFactory(ComponentType.TRANSPORT)
public class BeltComponent extends ItemTransportComponent {

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
