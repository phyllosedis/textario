package ru.phyllosedis.textario.component.impl.transport.impl;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.component.factory.AutoFactory;
import ru.phyllosedis.textario.component.factory.ComponentArgs;
import ru.phyllosedis.textario.component.impl.transport.ItemTransportComponent;
import ru.phyllosedis.textario.type.ComponentType;

@Getter
@ToString
@AutoFactory(ComponentType.TRANSPORT)
public class BeltComponent extends ItemTransportComponent {
    protected BeltComponent() {
        super();
    }

    public record Args() implements ComponentArgs<BeltComponent> {
        @Override
        public BeltComponent instantiate() {
            return new BeltComponent();
        }
    }
}
