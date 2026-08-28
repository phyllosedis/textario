package ru.phyllosedis.textario.logistics.belt;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.engine.ecs.component.AutoFactory;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.component.MarkerComponent;
import ru.phyllosedis.textario.engine.ecs.component.Component;
import ru.phyllosedis.textario.engine.ecs.component.ComponentType;

@Getter
@ToString
@AutoFactory(ComponentType.LOGISTIC)
public class BeltMarkerComponent extends Component implements MarkerComponent {
    protected BeltMarkerComponent() {
        super();
    }

    public record Args() implements ComponentArgs<BeltMarkerComponent> {
        @Override
        public BeltMarkerComponent instantiate() {
            return new BeltMarkerComponent();
        }
    }
}
