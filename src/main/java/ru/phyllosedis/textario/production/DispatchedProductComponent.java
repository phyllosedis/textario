package ru.phyllosedis.textario.production;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.engine.ecs.component.AutoFactory;
import ru.phyllosedis.textario.engine.ecs.component.Component;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.component.ComponentType;
import ru.phyllosedis.textario.resource.ResourceType;

@Getter
@ToString
@AutoFactory(ComponentType.DISPATCHED_PRODUCT)
public class DispatchedProductComponent extends Component {

    private final int count;

    protected DispatchedProductComponent(int count) {
        this.count = count;
    }

    public record Args(int count) implements ComponentArgs<DispatchedProductComponent> {
        @Override
        public DispatchedProductComponent instantiate() {
            return new DispatchedProductComponent(count);
        }
    }
}
