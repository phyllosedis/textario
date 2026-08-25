package ru.phyllosedis.textario.component.impl.inventory;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.component.Component;
import ru.phyllosedis.textario.component.factory.ComponentArgs;
import ru.phyllosedis.textario.type.ComponentType;
import ru.phyllosedis.textario.type.ResourceType;

import java.util.List;
import java.util.Map;

@Getter
@ToString
public class InventoryComponent extends Component {
    // количество ячеек
    private final int size;
    // предмет ordinal, количество
    private final List<Slot> slots;

    public record Slot(int resource, int count){}

    InventoryComponent(int size, List<Slot> slots) {
        super(ComponentType.INVENTORY);
        this.size = size;
        this.slots = slots;
    }

    public record ReadableSlot(ResourceType type, int count) {}
    public record Args(int size, List<ReadableSlot> slots) implements ComponentArgs<InventoryComponent> {
    }

}
