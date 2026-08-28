package ru.phyllosedis.textario.production.mining;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.engine.ecs.component.AutoFactory;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.component.MarkerComponent;
import ru.phyllosedis.textario.engine.ecs.component.Component;
import ru.phyllosedis.textario.engine.ecs.component.ComponentType;


@ToString
@Getter
@AutoFactory(ComponentType.MARKER)
public class MiningMarkerComponent extends Component implements MarkerComponent {

    protected MiningMarkerComponent() {

    }

    public record Args() implements ComponentArgs<MiningMarkerComponent> {
        @Override
        public MiningMarkerComponent instantiate() {
            return new MiningMarkerComponent();
        }
    }
}
