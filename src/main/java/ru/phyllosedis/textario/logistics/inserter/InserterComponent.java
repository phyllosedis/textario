package ru.phyllosedis.textario.logistics.inserter;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.engine.ecs.component.AutoFactory;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.component.ComponentType;
import ru.phyllosedis.textario.logistics.LogisticComponent;

@Getter
@ToString
@AutoFactory(ComponentType.LOGISTIC)
public class InserterComponent extends LogisticComponent {

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
