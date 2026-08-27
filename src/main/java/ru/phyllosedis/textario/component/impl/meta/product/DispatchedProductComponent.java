package ru.phyllosedis.textario.component.impl.meta.product;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.component.Component;
import ru.phyllosedis.textario.component.factory.AutoFactory;
import ru.phyllosedis.textario.component.factory.ComponentArgs;
import ru.phyllosedis.textario.type.ComponentType;
import ru.phyllosedis.textario.type.ResourceType;

@Getter
@ToString
@AutoFactory(ComponentType.DISPATCHED_PRODUCT)
public class DispatchedProductComponent extends Component {

    private final int resourceType;
    private final int count;

    protected DispatchedProductComponent(int resourceType, int count) {
        super();
        this.resourceType = resourceType;
        this.count = count;
    }

    public record Args(ResourceType resourceType, int count) implements ComponentArgs<DispatchedProductComponent> {
        @Override
        public DispatchedProductComponent instantiate() {
            return new DispatchedProductComponent(resourceType.ordinal(), count);
        }
    }
}
