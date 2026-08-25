package ru.phyllosedis.textario.system.mining.impl.tierone;

import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.Entity;
import ru.phyllosedis.textario.GameInitializer;
import ru.phyllosedis.textario.component.ComponentManager;
import ru.phyllosedis.textario.component.factory.ComponentFactoryManager;
import ru.phyllosedis.textario.component.impl.inventory.InventoryComponent;
import ru.phyllosedis.textario.component.impl.mining.MiningComponent;
import ru.phyllosedis.textario.system.mining.MiningSystem;
import ru.phyllosedis.textario.type.Grade;
import ru.phyllosedis.textario.type.ResourceType;

import java.util.ArrayList;
import java.util.List;

import static ru.phyllosedis.textario.constants.Constants.INVENTORY_STACK_LIMIT;

@Component
public class TierOneMiningSystem extends MiningSystem {


    public TierOneMiningSystem(ComponentFactoryManager cfm, ComponentManager cm) {
        super(cfm, cm, Grade.TIER_1);
    }

    public void update() {
        List<Entity> activeEntities = GameInitializer.activeEntities;

        for (int i = 0; i < activeEntities.size(); i++) {
            long id = activeEntities.get(i).getId();

            if (cm.has(id, MiningComponent.class) && cm.has(id, InventoryComponent.class)) {
                MiningComponent oldMining = cm.get(id, MiningComponent.class);
                InventoryComponent oldInv = cm.get(id, InventoryComponent.class);

                ResourceType resType = ResourceType.getByOrdinal(oldMining.getResourceType());

                // --- ПРОВЕРКА НАЛИЧИЯ МЕСТА (СЛОТЫ И СТАКИ) ---
                boolean hasSpace = false;

                // Ищем, есть ли уже стак этой руды, заполненный меньше чем на STACK_LIMIT
                for (InventoryComponent.Slot slot : oldInv.getSlots()) {
                    if (slot.resource() == resType.ordinal() && slot.count() < INVENTORY_STACK_LIMIT) {
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
                    continue; // Бур останавливается, прогресс НЕ сохраняется и не растёт
                }

                // --- ЛОГИКА ПРОГРЕССА БУРЕНИЯ ---
                int boostSpeed = Math.round(oldMining.getSpeed() * grade.getBoost());
                int newProgress = oldMining.getProgress() + boostSpeed;

                boolean oreMined = false;
                if (newProgress >= 100) {
                    newProgress = 0;
                    oreMined = true;
                }

                // --- ЛОГИКА ДОБАВЛЕНИЯ В СЛОТЫ ПРИ УСПЕШНОЙ ДОБЫЧЕ ---
                if (oreMined) {
                    // Переводим внутренние примитивные слоты в красивые ReadableSlot для Args
                    List<InventoryComponent.ReadableSlot> readableSlots = new ArrayList<>();
                    boolean addedToExisting = false;

                    for (InventoryComponent.Slot slot : oldInv.getSlots()) {
                        ResourceType type = ResourceType.getByOrdinal(slot.resource());
                        int count = slot.count();

                        // Если нашли стак нашей руды, где еще есть место — докидываем туда +1
                        if (!addedToExisting && type == resType && count < INVENTORY_STACK_LIMIT) {
                            readableSlots.add(new InventoryComponent.ReadableSlot(type, count + 1));
                            addedToExisting = true;
                        } else {
                            readableSlots.add(new InventoryComponent.ReadableSlot(type, count));
                        }
                    }

                    // Если стака с местом не нашлось, но мы на шаге выше проверили, что есть пустой слот — открываем новый стак
                    if (!addedToExisting) {
                        readableSlots.add(new InventoryComponent.ReadableSlot(resType, 1));
                    }

                    // Сохраняем обновленный инвентарь
                    cm.add(id, cfm.create(new InventoryComponent.Args(oldInv.getSize(), readableSlots)));

                    // Считаем общее число руды для лога
                    int totalOre = readableSlots.stream()
                            .filter(s -> s.type() == resType)
                            .mapToInt(InventoryComponent.ReadableSlot::count)
                            .sum();

                    System.out.println("[Бур #" + id + "] Добыта 1 ед. " + resType + ". Занято слотов: " + readableSlots.size() + "/" + oldInv.getSize() + " (Всего руды: " + totalOre + ")");
                }

                // В любом случае сохраняем изменившийся прогресс бурения
                MiningComponent.Args args = new MiningComponent.Args(
                        resType,
                        oldMining.getSpeed(),
                        newProgress,
                        grade
                );
                cm.add(id, cfm.create(args));
            }
        }
    }
}
