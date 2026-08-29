package ru.phyllosedis.textario.logistics.splitter;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.component.AutoFactory;
import ru.phyllosedis.textario.logistics.LogisticComponent;
import ru.phyllosedis.textario.engine.ecs.component.ComponentType;

@Getter
@ToString
@AutoFactory(ComponentType.LOGISTIC)
public class SplitterComponent extends LogisticComponent {
    private final int splitMode;

    protected SplitterComponent(int splitMode) {
        this.splitMode = splitMode;
    }

    public record Args(SplitMode splitMode) implements ComponentArgs<SplitterComponent> {
        @Override
        public SplitterComponent instantiate() {
            return new SplitterComponent(splitMode.ordinal());
        }
    }
}
