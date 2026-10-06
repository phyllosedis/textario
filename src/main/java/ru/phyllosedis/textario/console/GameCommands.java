package ru.phyllosedis.textario.console;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.phyllosedis.textario.engine.ecs.ComponentFactoryRegistry;
import ru.phyllosedis.textario.engine.ecs.ComponentManager;
import ru.phyllosedis.textario.engine.spring.EntityBlueprintService;
import ru.phyllosedis.textario.inventory.InventoryComponent;
import ru.phyllosedis.textario.logistics.belt.BeltMarkerComponent;
import ru.phyllosedis.textario.logistics.inserter.InserterMarkerComponent;
import ru.phyllosedis.textario.logistics.splitter.SplitMode;
import ru.phyllosedis.textario.production.DispatchedProductComponent;
import ru.phyllosedis.textario.production.ProgressComponent;
import ru.phyllosedis.textario.production.assembler.AssemblerComponent;
import ru.phyllosedis.textario.production.assembler.AssemblerMarkerComponent;
import ru.phyllosedis.textario.production.mining.MiningMarkerComponent;
import ru.phyllosedis.textario.production.recipe.RecipeBook;
import ru.phyllosedis.textario.resource.ResourceType;
import ru.phyllosedis.textario.resource.Tier;
import ru.phyllosedis.textario.storage.ChestMarkerComponent;
import ru.phyllosedis.textario.ui.WorldView;
import ru.phyllosedis.textario.world.BuildingComponent;
import ru.phyllosedis.textario.world.OccupancyGrid;
import ru.phyllosedis.textario.world.PositionComponent;
import ru.phyllosedis.textario.world.PortResolver;
import ru.phyllosedis.textario.world.RotationComponent;

import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class GameCommands {

    private final EntityBlueprintService blueprints;
    private final ComponentManager cm;
    private final ComponentFactoryRegistry cfm;
    private final OccupancyGrid occupancyGrid;
    private final RecipeBook book;
    private final WorldView worldView;
    private final MapRenderer mapRenderer;

    public String placeMiner(int x, int y, ResourceType ore) {
        return tryPlace(() -> {
            long id = blueprints.createMiner(x, y, Tier.ONE, ore);
            return "бур #" + id + " на " + x + ":" + y;
        });
    }

    public String placeBelt(int x, int y) {
        return placeBelt(x, y, 0);
    }

    public String placeBelt(int x, int y, int rotation) {
        return tryPlace(() -> {
            long id = blueprints.createBelt(x, y, Tier.ONE, ResourceType.EARTH, rotation);
            return "лента #" + id + " на " + x + ":" + y;
        });
    }

    public String placeInserter(int x, int y) {
        return placeInserter(x, y, 0);
    }

    public String placeInserter(int x, int y, int rotation) {
        return tryPlace(() -> {
            long id = blueprints.createInserter(x, y, Tier.ONE, ResourceType.EARTH, rotation);
            return "рука #" + id + " на " + x + ":" + y;
        });
    }

    public String placeChest(int x, int y) {
        return tryPlace(() -> {
            long id = blueprints.createChest(x, y, Tier.ONE);
            return "сундук #" + id + " на " + x + ":" + y;
        });
    }

    public String placeFurnace(int x, int y) {
        return tryPlace(() -> {
            long id = blueprints.createFurnace(x, y, Tier.ONE);
            return "печь #" + id + " на " + x + ":" + y;
        });
    }

    /**
     * Повернуть ленту/руку на клетке на 90° по часовой.
     */
    public String rotateAt(int x, int y) {
        Long id;
        try {
            id = occupancyGrid.getEntityAt(x, y);
        } catch (Exception e) {
            return "FAIL: " + e.getMessage();
        }
        if (id == null) {
            return "FAIL: на " + x + ":" + y + " пусто";
        }
        if (!cm.has(id, BeltMarkerComponent.class) && !cm.has(id, InserterMarkerComponent.class)) {
            return "FAIL: #" + id + " не поворачивается (только лента и рука)";
        }
        RotationComponent current = cm.get(id, RotationComponent.class);
        int next = ((current == null ? 0 : current.getSteps()) + 1) % 4;
        cm.add(id, cfm.create(new RotationComponent.Args(next)));
        return "OK: #" + id + " теперь смотрит " + directionName(next);
    }

    public boolean isRotatable(long id) {
        return cm.has(id, BeltMarkerComponent.class) || cm.has(id, InserterMarkerComponent.class);
    }

    public Long entityAt(int x, int y) {
        try {
            return occupancyGrid.getEntityAt(x, y);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Куда смотрит выход (FRONT): 0 вниз, 1 влево, 2 вверх, 3 вправо.
     */
    public static int parseDirection(String word) {
        return PortResolver.parseDirection(word);
    }

    public static String directionName(int rotation) {
        return PortResolver.directionName(rotation);
    }

    public record ResolvedRef(long id, int x, int y, String kind) {
    }

    /**
     * Адресация построек: "x:y" или "тип@x:y" (belt@5:21).
     */
    public ResolvedRef resolveRef(String ref) {
        String s = ref.trim();
        String type = null;
        String coords = s;
        int at = s.indexOf('@');
        if (at >= 0) {
            type = s.substring(0, at).toLowerCase();
            coords = s.substring(at + 1);
        }
        String[] xy = coords.split("[:,]");
        if (xy.length != 2) {
            throw new IllegalArgumentException("нужны координаты вида x:y, получил '" + ref + "'");
        }
        int x;
        int y;
        try {
            x = Integer.parseInt(xy[0].trim());
            y = Integer.parseInt(xy[1].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("нужны координаты вида x:y, получил '" + ref + "'");
        }
        Long id = entityAt(x, y);
        if (id == null) {
            throw new IllegalArgumentException("на " + x + ":" + y + " пусто");
        }
        String kind = kindOf(id);
        if (type != null && !type.isEmpty() && !type.equals(kind)) {
            throw new IllegalArgumentException("на " + x + ":" + y + " стоит " + kind + ", а не " + type);
        }
        return new ResolvedRef(id, x, y, kind);
    }

    public String kindOf(long id) {
        if (cm.has(id, ru.phyllosedis.textario.production.mining.MiningMarkerComponent.class)) return "miner";
        if (cm.has(id, BeltMarkerComponent.class)) return "belt";
        if (cm.has(id, InserterMarkerComponent.class)) return "inserter";
        if (cm.has(id, ru.phyllosedis.textario.logistics.splitter.SplitterMarkerComponent.class)) return "splitter";
        if (cm.has(id, ChestMarkerComponent.class)) return "chest";
        if (cm.has(id, ru.phyllosedis.textario.production.furnace.FurnaceMarkerComponent.class)) return "furnace";
        if (cm.has(id, AssemblerMarkerComponent.class)) return "assembler";
        return "unknown";
    }

    public String info(String ref) {
        try {
            ResolvedRef r = resolveRef(ref);
            return "#" + r.id() + " " + r.kind() + "@" + r.x() + ":" + r.y()
                    + " — " + worldView.describe(r.x(), r.y());
        } catch (Exception e) {
            return "FAIL: " + e.getMessage();
        }
    }

    public String demolish(String ref) {
        final ResolvedRef r;
        try {
            r = resolveRef(ref);
        } catch (Exception e) {
            return "FAIL: " + e.getMessage();
        }
        PositionComponent pos = cm.get(r.id(), PositionComponent.class);
        BuildingComponent building = cm.get(r.id(), BuildingComponent.class);
        if (pos != null && building != null) {
            for (int x = pos.getX(); x < pos.getX() + building.getWidth(); x++) {
                for (int y = pos.getY(); y < pos.getY() + building.getHeight(); y++) {
                    occupancyGrid.freeCell(x, y);
                }
            }
        }
        cm.removeEntity(r.id());
        blueprints.forget(r.id());
        return "OK: снесён " + r.kind() + " #" + r.id();
    }

    public String placeAssembler(int x, int y) {
        return tryPlace(() -> {
            long id = blueprints.createAssembler(x, y, Tier.ONE);
            return "сборщик #" + id + " на " + x + ":" + y + " (рецепт: <ref> set <имя>)";
        });
    }

    public String setRecipe(String ref, String recipeWord) {
        final ResolvedRef r;
        try {
            r = resolveRef(ref);
        } catch (Exception e) {
            return "FAIL: " + e.getMessage();
        }
        if (!cm.has(r.id(), AssemblerMarkerComponent.class)) {
            return "FAIL: #" + r.id() + " не сборщик (это " + r.kind() + ")";
        }
        RecipeBook.Recipe recipe = book.byIdOrName(recipeWord).orElse(null);
        if (recipe == null) {
            return "FAIL: не знаю рецепта '" + recipeWord + "', список: recipes";
        }
        if (recipe.station() != RecipeBook.Station.ASSEMBLER) {
            return "FAIL: '" + recipe.name() + "' делается в печи, а не в сборщике";
        }
        cm.add(r.id(), cfm.create(new AssemblerComponent.Args(recipe.id())));
        return "OK: сборщик #" + r.id() + " теперь делает '" + recipe.name() + "'";
    }

    public String recipesText() {
        StringBuilder sb = new StringBuilder("РЕЦЕПТЫ:\n");
        sb.append("ПЕЧЬ:\n");
        for (RecipeBook.Recipe r : book.byStation(RecipeBook.Station.FURNACE)) {
            sb.append("  ").append(formatRecipe(r)).append("\n");
        }
        sb.append("СБОРЩИК:\n");
        for (RecipeBook.Recipe r : book.byStation(RecipeBook.Station.ASSEMBLER)) {
            sb.append("  ").append(formatRecipe(r)).append("\n");
        }
        sb.append("Выбор: assembler@x:y set <имя>");
        return sb.toString();
    }

    private String formatRecipe(RecipeBook.Recipe r) {
        return r.id() + " (" + r.name() + "): " + formatStack(r.inputs()) + " -> " + formatStack(r.outputs());
    }

    private String formatStack(java.util.Map<ResourceType, Integer> stack) {
        StringBuilder sb = new StringBuilder();
        for (java.util.Map.Entry<ResourceType, Integer> e : stack.entrySet()) {
            if (sb.length() > 0) sb.append(" + ");
            sb.append(e.getKey()).append("x").append(e.getValue());
        }
        return sb.toString();
    }

    public String placeableText() {
        return """
                РАЗМЕЩАЕМЫЕ:
                  miner x y ORE — бур 2x2, ORE = IRON_ORE/COPPER_ORE/COAL, только на руду
                  belt x y [dir] — лента 1x1
                  ins x y [dir] — рука 1x1
                  chest x y — сундук 1x1
                  fur x y — печь 2x2 (плавит по рецептам из recipes)
                  spl x y MODE — разделитель 2x1, MODE = ROUND_ROBIN/BALANCED/PRIORITY_LEFT/PRIORITY_RIGHT
                  assembler x y — сборщик 2x2, рецепт: assembler@x:y set <имя>
                  dir = down/left/up/right (куда смотрит выход), по умолчанию down
                  удалить: delete <ref>, инфо: info <ref>, <ref> = x:y или тип@x:y""";
    }

    public boolean isAssembler(long id) {
        return cm.has(id, AssemblerMarkerComponent.class);
    }

    public java.util.List<RecipeBook.Recipe> assemblerRecipes() {
        return book.byStation(RecipeBook.Station.ASSEMBLER);
    }

    public String placeSplitter(int x, int y, SplitMode mode) {
        return tryPlace(() -> {
            long id = blueprints.createSplitter(x, y, Tier.ONE, ResourceType.EARTH, mode);
            return "разделитель #" + id + " на " + x + ":" + y + " " + mode;
        });
    }

    public String map() {
        return mapRenderer.render(0, 15, 25, 20);
    }

    public String map(int x, int y, int w, int h) {
        return mapRenderer.render(x, y, w, h);
    }

    public String inventories() {
        StringBuilder sb = new StringBuilder();
        Map<Long, String> entities = new TreeMap<>(blueprints.createdEntities());
        if (entities.isEmpty()) {
            return "(пока нет построек)";
        }
        for (Map.Entry<Long, String> e : entities.entrySet()) {
            long id = e.getKey();
            sb.append("#").append(id).append(" ").append(e.getValue());
            InventoryComponent inv = cm.get(id, InventoryComponent.class);
            if (inv != null) {
                sb.append(" inv=[");
                for (InventoryComponent.Slot slot : inv.getSlots()) {
                    ResourceType type = ResourceType.UNDEFINED.getByOrdinal(slot.resource());
                    sb.append(type).append("x").append(slot.count()).append(" ");
                }
                sb.append("]");
            }
            DispatchedProductComponent buf = cm.get(id, DispatchedProductComponent.class);
            if (buf != null) {
                ResourceType type = ResourceType.UNDEFINED.getByOrdinal(buf.getResource());
                sb.append(" buf=").append(type).append("x").append(buf.getCount());
            }
            ProgressComponent progress = cm.get(id, ProgressComponent.class);
            if (progress != null) {
                sb.append(String.format(" %.0f%%", progress.getProgress()));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String tryPlace(PlaceAction action) {
        try {
            return "OK: " + action.place();
        } catch (Exception e) {
            return "FAIL: " + e.getMessage();
        }
    }

    private interface PlaceAction {
        String place();
    }
}
