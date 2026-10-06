package ru.phyllosedis.textario.production.furnace;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.engine.ecs.component.AutoFactory;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.component.Component;
import ru.phyllosedis.textario.engine.ecs.component.ComponentType;

@Getter
@ToString
@AutoFactory(ComponentType.EXTRACTOR)
public class FurnaceComponent extends Component {

    protected FurnaceComponent() {
        super();
    }

    public record Args() implements ComponentArgs<FurnaceComponent> {
        @Override
        public FurnaceComponent instantiate() {
            return new FurnaceComponent();
        }
    }
}
