package ru.phyllosedis.textario.system.mining;

import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.component.impl.inventory.InventoryComponent;
import ru.phyllosedis.textario.component.impl.meta.grade.GradeComponent;
import ru.phyllosedis.textario.component.impl.mining.MiningComponent;
import ru.phyllosedis.textario.component.impl.position.PositionComponent;
import ru.phyllosedis.textario.system.AbstractSystem;
import ru.phyllosedis.textario.system.Requires;
import ru.phyllosedis.textario.type.Grade;
import ru.phyllosedis.textario.type.ResourceType;

import java.util.ArrayList;
import java.util.List;

@Requires({MiningComponent.class, InventoryComponent.class, PositionComponent.class, GradeComponent.class})
public abstract class MiningSystem extends AbstractSystem {

    protected MiningSystem(ComponentFactoryManager cfm, ComponentManager cm) {
        super(cfm, cm);
    }

    @Override
    protected void updateEntity(long id) {
        MiningComponent oldMining = cm.get(id, MiningComponent.class);
        InventoryComponent oldInv = cm.get(id, InventoryComponent.class);

        Grade currentGrade = cm.get(id, GradeComponent.class).getGrade();

        ResourceType resType = ResourceType.getByOrdinal(oldMining.getResourceType());

        // Локальная переменная лимита стака, взятая напрямую из инвентаря сущности
        int currentStackLimit = oldInv.getStackSize();

        // --- ПРОВЕРКА НАЛИЧИЯ МЕСТА (СЛОТЫ И СТАКИ) ---
        boolean hasSpace = false;

        // Ищем, есть ли уже стак этой руды, заполненный меньше чем текущий лимит инвентаря
        for (InventoryComponent.Slot slot : oldInv.getSlots()) {
            if (slot.resource() == resType.ordinal() && slot.count() < currentStackLimit) {
                hasSpace = true;
                break;
            }
        }

        // Если неполного стака нет, но общее количество занятых слотов меньше размера инвентаря
        if (!hasSpace && oldInv.getSlots().size() < oldInv.getSize()) {
            hasSpace = true;
        }

        if (!hasSpace) {
            System.out.println("[Бур #" + id + "] Переполнен! Все слоты (" + oldInv.getSize() + ") забиты стаками.");
            return;
        }

        // --- ЛОГИКА ПРОГРЕССА БУРЕНИЯ ---
        int boostSpeed = Math.round(oldMining.getSpeed() * currentGrade.getBoost());
        int newProgress = oldMining.getProgress() + boostSpeed;

        boolean oreMined = false;
        if (newProgress >= 100) {
            newProgress = 0;
            oreMined = true;
        }

        // --- ЛОГИКА ДОБАВЛЕНИЯ В СЛОТЫ ПРИ УСПЕШНОЙ ДОБЫЧЕ ---
        if (oreMined) {
            List<InventoryComponent.ReadableSlot> readableSlots = new ArrayList<>();
            boolean addedToExisting = false;

            for (InventoryComponent.Slot slot : oldInv.getSlots()) {
                ResourceType type = ResourceType.getByOrdinal(slot.resource());
                int count = slot.count();

                // Проверяем доступное место на основе динамического лимита конкретного инвентаря
                if (!addedToExisting && type == resType && count < currentStackLimit) {
                    readableSlots.add(new InventoryComponent.ReadableSlot(type, count + 1));
                    addedToExisting = true;
                } else {
                    readableSlots.add(new InventoryComponent.ReadableSlot(type, count));
                }
            }

            if (!addedToExisting) {
                readableSlots.add(new InventoryComponent.ReadableSlot(resType, 1));
            }

            // Пересоздаем инвентарь, сохраняя его изначальный размер и размер стака
            cm.add(id, cfm.create(new InventoryComponent.Args(oldInv.getSize(), oldInv.getStackSize(), readableSlots)));

            int totalOre = readableSlots.stream()
                    .filter(s -> s.type() == resType)
                    .mapToInt(InventoryComponent.ReadableSlot::count)
                    .sum();

            System.out.println("[Бур #" + id + "] Добыта 1 ед. " + resType + ". Занято слотов: " + readableSlots.size() + "/" + oldInv.getSize() + " (Всего руды: " + totalOre + ")");
        }

        MiningComponent.Args args = new MiningComponent.Args(
                resType,
                oldMining.getSpeed(),
                newProgress
        );
        cm.add(id, cfm.create(args));
    }

}
