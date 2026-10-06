package ru.phyllosedis.textario;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.phyllosedis.textario.bootstrap.TextarioApplication;
import ru.phyllosedis.textario.console.GameCommands;
import ru.phyllosedis.textario.combat.EnemyMarkerComponent;
import ru.phyllosedis.textario.engine.metrics.MetricsService;
import ru.phyllosedis.textario.inventory.OutputInventoryComponent;
import ru.phyllosedis.textario.world.SaveService;
import ru.phyllosedis.textario.engine.ecs.ComponentFactoryRegistry;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.spring.EntityBlueprintService;
import ru.phyllosedis.textario.inventory.InventoryComponent;
import ru.phyllosedis.textario.resource.ResourceType;
import ru.phyllosedis.textario.resource.Tier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = TextarioApplication.class, properties = "textario.console.enabled=false")
@DisplayName("Прототип: цепочки добычи и плавки")
class PrototypeChainTest {

    @Autowired
    private EntityBlueprintService blueprints;

    @Autowired
    private ComponentManager cm;

    @Autowired
    private ComponentFactoryRegistry cfm;

    @Autowired
    private GameCommands gameCommands;

    @Autowired
    private SaveService saves;

    @Autowired
    private MetricsService metrics;

    @Test
    @DisplayName("Уголь доезжает из бура в сундук через манипулятор")
    void coalReachesChest() throws Exception {
        long miner = blueprints.createMiner(18, 20, Tier.ONE);
        blueprints.createInserter(18, 22, Tier.ONE, ResourceType.EARTH, 0);
        long chest = blueprints.createChest(18, 23, Tier.ONE);

        Thread.sleep(6000);

        InventoryComponent inv = cm.get(chest, InventoryComponent.class);
        int coal = inv.getSlots().stream()
                .filter(s -> ResourceType.UNDEFINED.getByOrdinal(s.resource()) == ResourceType.COAL)
                .mapToInt(InventoryComponent.Slot::count)
                .sum();
        System.out.println("[test] сундук #" + chest + " уголь: " + coal + " (бур #" + miner + ")");
        assertTrue(coal > 0, "в сундуке должен быть уголь, добытый буром #" + miner);
    }

    @Test
    @DisplayName("Печь плавит руду в плиты")
    void furnaceSmeltsPlates() throws Exception {
        long furnace = blueprints.createFurnace(30, 30, Tier.ONE);
        cm.add(furnace, cfm.create(new InventoryComponent.Args(4, 50,
                List.of(new InventoryComponent.ReadableSlot(ResourceType.IRON_ORE, 5),
                        new InventoryComponent.ReadableSlot(ResourceType.COAL, 2)))));

        Thread.sleep(6000);

        OutputInventoryComponent out = cm.get(furnace, OutputInventoryComponent.class);
        int plates = out.getSlots().stream()
                .filter(s -> ResourceType.UNDEFINED.getByOrdinal(s.resource()) == ResourceType.IRON_PLATE)
                .mapToInt(InventoryComponent.Slot::count)
                .sum();
        System.out.println("[test] печь #" + furnace + " плиты: " + plates);
        assertTrue(plates == 5, "из 5 руды должно выйти ровно 5 плит, вышло " + plates);
    }

    @Test
    @DisplayName("Повёрнутая рука везёт вбок")
    void rotatedInserterMovesSideways() throws Exception {
        long src = blueprints.createChest(40, 22, Tier.ONE);
        cm.add(src, cfm.create(new InventoryComponent.Args(8, 100,
                List.of(new InventoryComponent.ReadableSlot(ResourceType.COAL, 3)))));
        // Поворот 3: вход смотрит влево (40:22), выход — вправо (42:22)
        blueprints.createInserter(41, 22, Tier.ONE, ResourceType.EARTH, 3);
        long dst = blueprints.createChest(42, 22, Tier.ONE);

        Thread.sleep(3000);

        InventoryComponent inv = cm.get(dst, InventoryComponent.class);
        int coal = inv.getSlots().stream()
                .filter(s -> ResourceType.UNDEFINED.getByOrdinal(s.resource()) == ResourceType.COAL)
                .mapToInt(InventoryComponent.Slot::count)
                .sum();
        System.out.println("[test] сундук-приёмник #" + dst + " уголь: " + coal);
        assertTrue(coal > 0, "повёрнутая рука должна перевезти уголь вбок");
    }

    @Test
    @DisplayName("Сборщик крафтит шестерёнки из плит")
    void assemblerCraftsGears() throws Exception {
        long asm = blueprints.createAssembler(50, 30, Tier.ONE);
        String set = gameCommands.setRecipe("50:30", "шестерёнки");
        assertTrue(set.startsWith("OK"), "рецепт должен выбраться: " + set);
        cm.add(asm, cfm.create(new InventoryComponent.Args(4, 50,
                List.of(new InventoryComponent.ReadableSlot(ResourceType.IRON_PLATE, 4)))));

        Thread.sleep(5000);

        OutputInventoryComponent out = cm.get(asm, OutputInventoryComponent.class);
        int gears = out.getSlots().stream()
                .filter(s -> ResourceType.UNDEFINED.getByOrdinal(s.resource()) == ResourceType.IRON_GEAR)
                .mapToInt(InventoryComponent.Slot::count)
                .sum();
        System.out.println("[test] сборщик #" + asm + " шестерёнки: " + gears);
        assertTrue(gears >= 2, "из 4 плит должно выйти 2 шестерёнки");
    }

    @Test
    @DisplayName("info показывает постройку, delete сносит её")
    void infoAndDemolish() {
        long chest = blueprints.createChest(60, 60, Tier.ONE);

        String info = gameCommands.info("chest@60:60");
        assertTrue(info.contains("60:60"), "info должно показать координаты: " + info);
        assertTrue(info.contains("chest"), "info должно показать тип: " + info);

        String badRef = gameCommands.info("belt@60:60");
        assertTrue(badRef.startsWith("FAIL"), "чужой тип в ref должен отвергаться: " + badRef);

        String demolished = gameCommands.demolish("60:60");
        assertTrue(demolished.startsWith("OK"), "снос должен сработать: " + demolished);
        assertTrue(gameCommands.entityAt(60, 60) == null, "клетка должна освободиться");
        assertTrue(gameCommands.info("60:60").startsWith("FAIL"), "после сноса там пусто");

        System.out.println("[test] снесён сундук #" + chest + ": " + demolished);
    }

    @Test
    @DisplayName("Подземка телепортирует через пустую клетку")
    void undergroundTeleports() throws Exception {
        long src = blueprints.createChest(50, 39, Tier.ONE);
        cm.add(src, cfm.create(new InventoryComponent.Args(8, 100,
                List.of(new InventoryComponent.ReadableSlot(ResourceType.COAL, 3)))));
        blueprints.createUnderground(50, 40, Tier.ONE, 0,
                ru.phyllosedis.textario.logistics.underground.UndergroundMode.ENTRY);
        blueprints.createUnderground(50, 42, Tier.ONE, 0,
                ru.phyllosedis.textario.logistics.underground.UndergroundMode.EXIT);
        long dst = blueprints.createChest(50, 43, Tier.ONE);

        Thread.sleep(5000);

        InventoryComponent inv = cm.get(dst, InventoryComponent.class);
        int coal = inv.getSlots().stream()
                .filter(s -> ResourceType.UNDEFINED.getByOrdinal(s.resource()) == ResourceType.COAL)
                .mapToInt(InventoryComponent.Slot::count)
                .sum();
        System.out.println("[test] сундук за подземкой #" + dst + " уголь: " + coal);
        assertTrue(coal > 0, "уголь должен пройти сквозь подземку (50:41 пустая)");
    }

    @Test
    @DisplayName("Рука берёт плиты, а не руду, и не чаще замаха")
    void inserterPrefersOutput() throws Exception {
        long furnace = blueprints.createFurnace(55, 30, Tier.ONE);
        cm.add(furnace, cfm.create(new InventoryComponent.Args(4, 50,
                List.of(new InventoryComponent.ReadableSlot(ResourceType.IRON_ORE, 3)))));
        cm.add(furnace, cfm.create(new OutputInventoryComponent.Args(2, 50,
                List.of(new InventoryComponent.ReadableSlot(ResourceType.IRON_PLATE, 2)))));
        blueprints.createInserter(55, 32, Tier.ONE, ResourceType.EARTH, 0);
        long chest = blueprints.createChest(55, 33, Tier.ONE);

        Thread.sleep(2500);

        InventoryComponent chestInv = cm.get(chest, InventoryComponent.class);
        int plates = chestInv.getSlots().stream()
                .filter(s -> ResourceType.UNDEFINED.getByOrdinal(s.resource()) == ResourceType.IRON_PLATE)
                .mapToInt(InventoryComponent.Slot::count)
                .sum();
        int oreInChest = chestInv.getSlots().stream()
                .filter(s -> ResourceType.UNDEFINED.getByOrdinal(s.resource()) == ResourceType.IRON_ORE)
                .mapToInt(InventoryComponent.Slot::count)
                .sum();
        InventoryComponent furnaceInv = cm.get(furnace, InventoryComponent.class);
        int oreLeft = furnaceInv.getSlots().stream()
                .filter(s -> ResourceType.UNDEFINED.getByOrdinal(s.resource()) == ResourceType.IRON_ORE)
                .mapToInt(InventoryComponent.Slot::count)
                .sum();
        System.out.println("[test] в сундуке плиты: " + plates + ", руды: " + oreInChest + ", в печи руды: " + oreLeft);
        assertTrue(plates >= 1, "рука должна вынуть плиты из выхода печи");
        assertTrue(oreInChest == 0 && oreLeft == 3, "руда должна лежать нетронутой");
    }

    @Test
    @DisplayName("Рука ограничена скоростью замаха")
    void inserterRateLimited() throws Exception {
        long src = blueprints.createChest(70, 70, Tier.ONE);
        cm.add(src, cfm.create(new InventoryComponent.Args(8, 100,
                List.of(new InventoryComponent.ReadableSlot(ResourceType.COAL, 50)))));
        blueprints.createInserter(71, 70, Tier.ONE, ResourceType.EARTH, 3);
        long dst = blueprints.createChest(72, 70, Tier.ONE);

        Thread.sleep(2500);

        InventoryComponent inv = cm.get(dst, InventoryComponent.class);
        int coal = inv.getSlots().stream()
                .filter(s -> ResourceType.UNDEFINED.getByOrdinal(s.resource()) == ResourceType.COAL)
                .mapToInt(InventoryComponent.Slot::count)
                .sum();
        System.out.println("[test] за 2.5с рука перенесла угля: " + coal);
        assertTrue(coal >= 1 && coal <= 4, "рука 1 тира таскает ~1 шт/с, а не всё сразу, перенесено: " + coal);
    }

    @Test
    @DisplayName("stats показывает тики и добычу без ожидания")
    void statsSmoke() {
        assertTrue(metrics.tickCount() > 0, "движок должен тикать");
        String stats = gameCommands.stats();
        assertTrue(stats.contains("ТИК #"), "stats должен показать счётчик тиков: " + stats);
        assertTrue(metrics.get("mined", ResourceType.IRON_ORE) > 0, "демо-бур должен копать железо");
        System.out.println("[test] stats:\n" + stats);
    }

    @Test
    @DisplayName("Пауза останавливает тики, tick крутит вручную")
    void pauseAndStep() throws Exception {
        try {
            gameCommands.togglePause();
            long t1 = metrics.tickCount();
            Thread.sleep(300);
            long t2 = metrics.tickCount();
            assertTrue(t2 - t1 <= 2, "на паузе тики стоять, сдвиг " + (t2 - t1));

            String stepped = gameCommands.stepTicks(5);
            assertTrue(stepped.startsWith("OK"), stepped);
            assertEquals(t2 + 5, metrics.tickCount(), "tick 5 крутит ровно 5 тиков");
        } finally {
            gameCommands.resume();
        }
    }

    @Test
    @DisplayName("Турель расстреливает врага")
    void turretKillsEnemy() throws Exception {
        long turret = blueprints.createTurret(65, 65, Tier.ONE);
        cm.add(turret, cfm.create(new InventoryComponent.Args(2, 50,
                List.of(new InventoryComponent.ReadableSlot(ResourceType.COPPER_AMMO, 8)))));
        String spawned = gameCommands.spawnEnemy("67:65");
        assertTrue(spawned.startsWith("OK"), spawned);

        Thread.sleep(4000);

        assertTrue(gameCommands.entityAt(67, 65) == null, "турель должна добить врага за 4с");
        System.out.println("[test] турель #" + turret + " добила врага");
    }

    @Test
    @DisplayName("Волна спавнится у ядра")
    void waveSpawnsAtCore() {
        blueprints.createCore(80, 80, Tier.ONE);
        String forced = gameCommands.forceWave();
        assertTrue(forced.startsWith("OK"), forced);
        assertTrue(!cm.entitiesWith(EnemyMarkerComponent.class).isEmpty(), "волна должна заспавнить врагов");
        System.out.println("[test] " + forced + ", статус: " + gameCommands.waveStatus());
    }

    @Test
    @DisplayName("Сохранение и загрузка возвращают мир")
    void saveAndLoad() throws Exception {
        java.nio.file.Path file = java.nio.file.Files.createTempFile("textario-test-", ".json");
        try {
            blueprints.createChest(61, 61, Tier.ONE);
            String saved = saves.save(file.toString());
            assertTrue(saved.startsWith("OK"), "сохранение: " + saved);

            String demolished = gameCommands.demolish("61:61");
            assertTrue(demolished.startsWith("OK"), demolished);
            assertTrue(gameCommands.entityAt(61, 61) == null);

            String loaded = saves.load(file.toString());
            assertTrue(loaded.startsWith("OK"), "загрузка: " + loaded);
            assertTrue(gameCommands.entityAt(61, 61) != null, "сундук должен вернуться после загрузки");
            System.out.println("[test] save/load: " + loaded);
        } finally {
            java.nio.file.Files.deleteIfExists(file);
        }
    }
}
