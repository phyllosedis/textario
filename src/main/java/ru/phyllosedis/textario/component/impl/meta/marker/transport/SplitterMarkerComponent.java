package ru.phyllosedis.textario.component.impl.meta.marker.transport;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.component.factory.AutoFactory;
import ru.phyllosedis.textario.component.factory.ComponentArgs;
import ru.phyllosedis.textario.component.impl.meta.marker.MarkerComponent;
import ru.phyllosedis.textario.type.ComponentType;

@Getter
@ToString
@AutoFactory(ComponentType.MARKER)
public class SplitterMarkerComponent extends MarkerComponent {
    protected SplitterMarkerComponent() {
    }

    public record Args() implements ComponentArgs<SplitterMarkerComponent> {
        @Override
        public SplitterMarkerComponent instantiate() {
            return new SplitterMarkerComponent();
        }
    }
}
