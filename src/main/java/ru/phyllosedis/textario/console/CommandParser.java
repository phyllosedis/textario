package ru.phyllosedis.textario.console;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.phyllosedis.textario.logistics.splitter.SplitMode;
import ru.phyllosedis.textario.resource.ResourceType;
import ru.phyllosedis.textario.world.SaveService;

import java.util.Map;

/**
 * Один парсер команд для всех фронтендов (REPL и GUI).
 * Возвращает текст ответа, ничего не печатает сам.
 * На нехватку аргументов отвечает usage, а не исключением.
 */
@Service
@RequiredArgsConstructor
public class CommandParser {

    private static final Map<String, String> USAGE = Map.ofEntries(
            Map.entry("map", "map [x y w h] - показать карту (по умолчанию 0 15 25 20)"),
            Map.entry("inv", "inv - показать склады, буферы и прогресс всех построек"),
            Map.entry("stats", "stats - счётчики добычи/плавки/сборки и время тика по системам"),
            Map.entry("pause", "pause - вкл/выкл паузу (строить удобно на паузе)"),
            Map.entry("tick", "tick N - прокрутить N тиков вручную, работает и на паузе"),
            Map.entry("set", "set <ref> <рецепт> - например set assembler@10:42 iron-gear (или <ref> set <рецепт>)"),
            Map.entry("info", "info <ref> - что стоит на клетке (голый <ref> тоже работает)"),
            Map.entry("delete", "delete <ref> - снести постройку (алиасы: del, remove, demolish)"),
            Map.entry("recipes", "recipes - все рецепты с указанием станции"),
            Map.entry("placeable", "placeable - что можно строить (то же: help placeable)"),
            Map.entry("miner", "miner x y - бур 2x2, руду определяет сам по карте под корпусом"),
            Map.entry("belt", "belt x y [dir] - лента 1x1; dir: down/left/up/right (куда смотрит выход)"),
            Map.entry("ins", "ins x y [dir] - рука 1x1; dir: down/left/up/right"),
            Map.entry("chest", "chest x y - сундук 1x1"),
            Map.entry("fur", "fur x y - печь 2x2 (нужны руда и уголь)"),
            Map.entry("spl", "spl x y MODE [dir] - разделитель; MODE: ROUND_ROBIN, BALANCED, PRIORITY_LEFT, PRIORITY_RIGHT"),
            Map.entry("assembler", "assembler x y - сборщик 2x2, дальше: assembler@x:y set <имя рецепта>"),
            Map.entry("turret", "turret x y - турель 2x2, ест медные патроны из своего склада"),
            Map.entry("core", "core x y - ядро 3x3, его жрут враги; без ядра волн нет (песочница)"),
            Map.entry("spawn", "spawn x y - заспавнить врага (отладка)"),
            Map.entry("wave", "wave - статус волн; wave go - запустить следующую волну сейчас"),
            Map.entry("under", "under x y dir entry|exit - подземка 1x1, пара вход/выход до 4 клеток"),
            Map.entry("rot", "rot x y - повернуть ленту/руку/разделитель на 90 градусов"),
            Map.entry("save", "save [файл] - сохранить мир (по умолчанию textario-save.json)"),
            Map.entry("load", "load [файл] - загрузить мир (склады будут пустые)"),
            Map.entry("quit", "quit - выход")
    );

    private final GameCommands commands;
    private final SaveService saves;

    public String execute(String line) {
        if (line == null || line.isBlank()) {
            return "";
        }
        String[] parts = line.trim().split("\\s+");
        // Шаблон "<ref> set <рецепт>": assembler@10:42 set шестерёнки
        if (parts.length >= 3 && parts[1].equalsIgnoreCase("set")) {
            return commands.setRecipe(parts[0], joinFrom(parts, 2));
        }
        try {
            return switch (parts[0].toLowerCase()) {
                case "help", "man" -> parts.length >= 2 ? helpFor(parts[1]) : index();
                case "placeable" -> commands.placeableText();
                case "recipes" -> commands.recipesText();
                case "map" -> parts.length >= 5
                        ? commands.map(argInt(parts, 1, "map"), argInt(parts, 2, "map"),
                                argInt(parts, 3, "map"), argInt(parts, 4, "map"))
                        : commands.map();
                case "inv" -> commands.inventories();
                case "stats" -> commands.stats();
                case "pause" -> commands.togglePause();
                case "resume" -> commands.resume();
                case "tick" -> commands.stepTicks(argInt(parts, 1, "tick"));
                case "info" -> commands.info(joinFrom(need(parts, 2, "info"), 1));
                case "set" -> commands.setRecipe(parts[1], joinFrom(need(parts, 3, "set"), 2));
                case "delete", "del", "remove", "demolish" ->
                        commands.demolish(joinFrom(need(parts, 2, "delete"), 1));
                case "miner" -> commands.placeMiner(argInt(parts, 1, "miner"), argInt(parts, 2, "miner"));
                case "belt" -> commands.placeBelt(argInt(parts, 1, "belt"), argInt(parts, 2, "belt"),
                        parts.length >= 4 ? GameCommands.parseDirection(parts[3]) : 0);
                case "ins" -> commands.placeInserter(argInt(parts, 1, "belt"), argInt(parts, 2, "ins"),
                        parts.length >= 4 ? GameCommands.parseDirection(parts[3]) : 0);
                case "chest" -> commands.placeChest(argInt(parts, 1, "chest"), argInt(parts, 2, "chest"));
                case "fur" -> commands.placeFurnace(argInt(parts, 1, "fur"), argInt(parts, 2, "fur"));
                case "spl" -> {
                    need(parts, 4, "spl");
                    yield commands.placeSplitter(argInt(parts, 1, "spl"), argInt(parts, 2, "spl"),
                            argSplitMode(parts[3]),
                            parts.length >= 5 ? GameCommands.parseDirection(parts[4]) : 0);
                }
                case "assembler" ->
                        commands.placeAssembler(argInt(parts, 1, "assembler"), argInt(parts, 2, "assembler"));
                case "turret" ->
                        commands.placeTurret(argInt(parts, 1, "turret"), argInt(parts, 2, "turret"));
                case "core" ->
                        commands.placeCore(argInt(parts, 1, "core"), argInt(parts, 2, "core"));
                case "spawn" -> commands.spawnEnemy(joinFrom(need(parts, 2, "spawn"), 1));
                case "wave" -> parts.length >= 2 && parts[1].equalsIgnoreCase("go")
                        ? commands.forceWave()
                        : commands.waveStatus();
                case "under" -> {
                    need(parts, 5, "under");
                    yield commands.placeUnderground(argInt(parts, 1, "under"), argInt(parts, 2, "under"),
                            GameCommands.parseDirection(parts[3]), parts[4]);
                }
                case "rot" -> commands.rotateAt(argInt(parts, 1, "rot"), argInt(parts, 2, "rot"));
                case "save" -> saves.save(parts.length >= 2 ? parts[1] : "textario-save.json");
                case "load" -> saves.load(parts.length >= 2 ? parts[1] : "textario-save.json");
                case "quit", "exit" -> "quit";
                default -> looksLikeRef(parts[0]) && parts.length == 1
                        ? commands.info(parts[0])
                        : "не знаю команды '" + parts[0] + "', введи help";
            };
        } catch (IllegalArgumentException e) {
            return "ошибка: " + e.getMessage();
        } catch (Exception e) {
            return "ошибка: " + e.getMessage();
        }
    }

    private String index() {
        return """
                команды: map inv stats pause tick info delete recipes placeable miner belt ins chest fur spl assembler under turret core spawn wave rot save load set
                подробно: help placeable | help recipes | help <команда> (например help spl)
                <ref> - это x:y или тип@x:y, например belt@5:21 (голый <ref> показывает info)
                рецепт: <ref> set <имя> или set <ref> <имя>""";
    }

    /**
     * Похоже на адрес постройки: x:y или тип@x:y.
     */
    private static boolean looksLikeRef(String word) {
        return word.matches("(?i)([a-zа-я]+@)?\\d+[:,]\\d+");
    }

    private String helpFor(String cmd) {
        String key = cmd.toLowerCase();
        if (key.equals("placeable")) {
            return commands.placeableText();
        }
        if (key.equals("recipes")) {
            return commands.recipesText();
        }
        String usage = USAGE.get(key);
        if (usage != null) {
            return usage;
        }
        return "не знаю команды '" + cmd + "', введи help";
    }

    private static String[] need(String[] parts, int n, String cmd) {
        if (parts.length < n) {
            throw new IllegalArgumentException("использование: " + USAGE.get(cmd));
        }
        return parts;
    }

    private static int argInt(String[] parts, int i, String cmd) {
        need(parts, i + 1, cmd);
        try {
            return Integer.parseInt(parts[i]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "аргумент " + (i + 1) + " должен быть числом, использование: " + USAGE.get(cmd));
        }
    }

    private static SplitMode argSplitMode(String word) {
        try {
            return SplitMode.valueOf(word.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "MODE должен быть ROUND_ROBIN/BALANCED/PRIORITY_LEFT/PRIORITY_RIGHT, использование: "
                            + USAGE.get("spl"));
        }
    }

    private static String joinFrom(String[] parts, int from) {
        StringBuilder sb = new StringBuilder();
        for (int i = from; i < parts.length; i++) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(parts[i]);
        }
        return sb.toString();
    }
}
