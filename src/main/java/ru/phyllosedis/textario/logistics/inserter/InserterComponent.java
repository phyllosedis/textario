package ru.phyllosedis.textario.logistics.inserter;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.engine.ecs.component.AutoFactory;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.component.ComponentType;
import ru.phyllosedis.textario.logistics.LogisticComponent;
import ru.phyllosedis.textario.resource.ContentState;

import java.util.Set;

@Getter
@ToString
@AutoFactory(ComponentType.LOGISTIC)
public class InserterComponent extends LogisticComponent {

    private final double transferSpeed;
    private final int range;
    private final int stackSize;
    private final Set<ContentState> supportedStates;


    protected InserterComponent(double transferSpeed, int range, int stackSize, Set<ContentState> supportedStates) {
        super();
        this.transferSpeed = transferSpeed;
        this.range = range;
        this.stackSize = stackSize;
        this.supportedStates = supportedStates;
    }

    public record Args(double transferSpeed, int range, int stackSize,
                       Set<ContentState> supportedStates) implements ComponentArgs<InserterComponent> {
        @Override
        public InserterComponent instantiate() {
            return new InserterComponent(transferSpeed, range, stackSize, supportedStates);
        }
    }
}
