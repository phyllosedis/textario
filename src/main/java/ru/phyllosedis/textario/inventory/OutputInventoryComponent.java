package ru.phyllosedis.textario.inventory;

import lombok.Getter;
import lombok.ToString;
import ru.phyllosedis.textario.engine.ecs.component.AutoFactory;
import ru.phyllosedis.textario.engine.ecs.component.ComponentArgs;
import ru.phyllosedis.textario.engine.ecs.component.ComponentType;

import java.util.List;

/**
 * Выходной склад станции (печь, сборщик): сюда падает готовая
 * продукция, отсюда её забирают руки и ленты. Входное сырьё
 * лежит в обычном InventoryComponent — руки берут СНАЧАЛА отсюда,
 * поэтому руда больше не уедет вместо плит.
 */
@Getter
@ToString
@AutoFactory(ComponentType.INVENTORY)
public class OutputInventoryComponent extends InventoryComponent {

    OutputInventoryComponent(int size, int stackSize, List<Slot> slots) {
        super(size, stackSize, slots);
    }

    public record Args(int size, int stackSize, List<ReadableSlot> slots)
            implements ComponentArgs<OutputInventoryComponent> {
        @Override
        public OutputInventoryComponent instantiate() {
            return new OutputInventoryComponent(
                    size,
                    stackSize,
                    slots.stream()
                            .map(e -> new Slot(e.type().ordinal(), e.count()))
                            .toList());
        }
    }
}
