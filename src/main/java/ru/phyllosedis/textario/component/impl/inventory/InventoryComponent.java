package ru.phyllosedis.textario.component.impl.inventory;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.component.Component;
import ru.phyllosedis.textario.component.factory.AutoFactory;
import ru.phyllosedis.textario.component.factory.ComponentArgs;
import ru.phyllosedis.textario.type.ComponentType;
import ru.phyllosedis.textario.type.ResourceType;

import java.util.List;

@Getter
@ToString
@AutoFactory(value = ComponentType.INVENTORY)
public class InventoryComponent extends Component {
    // количество ячеек
    private final int size;
    // максимальный размер одного стека в инвентаре
    private final int stackSize;
    // предмет ordinal, количество
    private final List<Slot> slots;

    public record Slot(int resource, int count) {
    }

    InventoryComponent(int size, int stackSize, List<Slot> slots) {
        super();
        this.size = size;
        this.stackSize = stackSize;
        this.slots = slots;
    }

    public record ReadableSlot(ResourceType type, int count) {
    }

    public record Args(int size, int stackSize, List<ReadableSlot> slots) implements ComponentArgs<InventoryComponent> {
        @Override
        public InventoryComponent instantiate() {
            return new InventoryComponent(
                    size,
                    stackSize,
                    slots.stream()
                            .map(e -> new InventoryComponent.Slot(e.type().ordinal(), e.count()))
                            .toList());
        }
    }

}
