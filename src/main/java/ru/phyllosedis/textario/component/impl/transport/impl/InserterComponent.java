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
public class InserterComponent extends ItemTransportComponent {
    protected InserterComponent() {
        super();
    }

    public record Args() implements ComponentArgs<InserterComponent> {
        @Override
        public InserterComponent instantiate() {
            return new InserterComponent();
        }
    }
}
