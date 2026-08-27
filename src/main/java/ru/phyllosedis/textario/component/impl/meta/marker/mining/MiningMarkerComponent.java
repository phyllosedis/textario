package ru.phyllosedis.textario.component.impl.meta.marker.mining;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.component.factory.AutoFactory;
import ru.phyllosedis.textario.component.factory.ComponentArgs;
import ru.phyllosedis.textario.component.impl.meta.marker.MarkerComponent;
import ru.phyllosedis.textario.type.ComponentType;


@ToString
@Getter
@AutoFactory(ComponentType.MARKER)
public class MiningMarkerComponent extends MarkerComponent {

    protected MiningMarkerComponent() {

    }

    public record Args() implements ComponentArgs<MiningMarkerComponent> {
        @Override
        public MiningMarkerComponent instantiate() {
            return new MiningMarkerComponent();
        }
    }
}
