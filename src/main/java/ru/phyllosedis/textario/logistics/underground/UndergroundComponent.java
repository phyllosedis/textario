package ru.phyllosedis.textario.logistics.underground;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.engine.ecs.component.AutoFactory;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.component.ComponentType;
import ru.phyllosedis.textario.logistics.LogisticComponent;

@Getter
@ToString
@AutoFactory(ComponentType.LOGISTIC)
public class UndergroundComponent extends LogisticComponent {

    private final int mode;

    protected UndergroundComponent(int mode) {
        super();
        this.mode = mode;
    }

    public record Args(UndergroundMode mode) implements ComponentArgs<UndergroundComponent> {
        @Override
        public UndergroundComponent instantiate() {
            return new UndergroundComponent(mode.ordinal());
        }
    }
}
