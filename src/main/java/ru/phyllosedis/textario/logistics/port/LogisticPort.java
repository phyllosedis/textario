package ru.phyllosedis.textario.logistics.port;


import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.engine.ecs.component.Component;
import ru.phyllosedis.textario.engine.ecs.component.AutoFactory;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.component.ComponentType;

import java.util.List;

@Getter
@ToString
@AutoFactory(ComponentType.LOGISTIC)
public class LogisticPort extends Component {
    private final List<Port> ports;

    public LogisticPort(List<Port> ports) {
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

    public record Args(List<ReadablePort> ports) implements ComponentArgs<LogisticPort> {

        @Override
        public LogisticPort instantiate() {
            return new LogisticPort(
                    ports.stream()
                            .map(e -> new Port(
                                    e.id,
                                    e.type.ordinal(),
                                    e.side.ordinal()))
                            .toList());
        }
    }


}
