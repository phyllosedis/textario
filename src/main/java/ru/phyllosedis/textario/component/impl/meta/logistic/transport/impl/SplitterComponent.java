package ru.phyllosedis.textario.component.impl.meta.logistic.transport.impl;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.component.Component;
import ru.phyllosedis.textario.component.factory.AutoFactory;
import ru.phyllosedis.textario.component.factory.ComponentArgs;
import ru.phyllosedis.textario.type.ComponentType;
import ru.phyllosedis.textario.type.SplitMode;

@Getter
@ToString
@AutoFactory(ComponentType.TRANSPORT)
public class SplitterComponent extends Component {
    private final int splitMode;

    public SplitterComponent(int splitMode) {
        this.splitMode = splitMode;
    }

    public record Args(SplitMode splitMode) implements ComponentArgs<SplitterComponent> {
        @Override
        public SplitterComponent instantiate() {
            return new SplitterComponent(splitMode.ordinal());
        }
    }
}
