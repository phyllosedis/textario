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
public class InserterComponent extends ItemTransportComponent {

    private final double transferSpeed;
    private final int range;
    private final int stackSize;

    protected InserterComponent(double transferSpeed, int range, int stackSize) {
        super();
        this.transferSpeed = transferSpeed;
        this.range = range;
        this.stackSize = stackSize;
    }

    public record Args(double transferSpeed, int range, int stackSize) implements ComponentArgs<InserterComponent> {
        @Override
        public InserterComponent instantiate() {
            return new InserterComponent(transferSpeed, range, stackSize);
        }
    }
}
