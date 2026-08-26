package ru.phyllosedis.textario.component.impl.meta.logistic.transport.impl;


import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.component.Component;
import ru.phyllosedis.textario.component.factory.AutoFactory;
import ru.phyllosedis.textario.component.factory.ComponentArgs;
import ru.phyllosedis.textario.type.ComponentType;
import ru.phyllosedis.textario.type.PortSide;
import ru.phyllosedis.textario.type.PortType;

import java.util.List;

@Getter
@ToString
@AutoFactory(ComponentType.TRANSPORT)
public class TransportPortComponent extends Component {
    private final List<Port> ports;

    public TransportPortComponent(List<Port> ports) {
        this.ports = ports;
    }

    public record Port(
            int id,
            int type,
            int side
    ) {
    }

    public record ReadablePort(int id, PortType type, PortSide side) {
    }

    public record Args(List<ReadablePort> ports) implements ComponentArgs<TransportPortComponent> {

        @Override
        public TransportPortComponent instantiate() {
            return new TransportPortComponent(
                    ports.stream()
                            .map(e -> new Port(
                                    e.id,
                                    e.type.ordinal(),
                                    e.side.ordinal()))
                            .toList());
        }
    }


}
