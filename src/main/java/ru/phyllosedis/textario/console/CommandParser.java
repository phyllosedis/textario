package ru.phyllosedis.textario.console;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.phyllosedis.textario.logistics.splitter.SplitMode;
import ru.phyllosedis.textario.resource.ResourceType;

/**
 * Один парсер команд для всех фронтендов (REPL и GUI).
 * Возвращает текст ответа, ничего не печатает сам.
 */
@Service
@RequiredArgsConstructor
public class CommandParser {

    private final GameCommands commands;

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
                case "help" -> parts.length >= 2 && parts[1].equalsIgnoreCase("placeable")
                        ? commands.placeableText()
                        : parts.length >= 2 && parts[1].equalsIgnoreCase("recipes")
                        ? commands.recipesText()
                        : """
                        команды: map inv info delete recipes placeable miner belt ins chest fur spl assembler rot
                        подробно: help placeable | help recipes
                        <ref> — это x:y или тип@x:y, например belt@5:21
                        <ref> set <рецепт> — выбрать рецепт сборщика""";
                case "placeable" -> commands.placeableText();
                case "recipes" -> commands.recipesText();
                case "info" -> commands.info(joinFrom(parts, 1));
                case "delete", "del", "remove", "demolish" -> commands.demolish(joinFrom(parts, 1));
                case "assembler" -> commands.placeAssembler(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                case "map" -> parts.length >= 5
                        ? commands.map(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                                Integer.parseInt(parts[3]), Integer.parseInt(parts[4]))
                        : commands.map();
                case "inv" -> commands.inventories();
                case "miner" -> commands.placeMiner(
                        Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                        ResourceType.valueOf(parts[3].toUpperCase()));
                case "belt" -> commands.placeBelt(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                        parts.length >= 4 ? GameCommands.parseDirection(parts[3]) : 0);
                case "ins" -> commands.placeInserter(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                        parts.length >= 4 ? GameCommands.parseDirection(parts[3]) : 0);
                case "rot" -> commands.rotateAt(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                case "chest" -> commands.placeChest(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                case "fur" -> commands.placeFurnace(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                case "spl" -> commands.placeSplitter(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                        SplitMode.valueOf(parts[3].toUpperCase()));
                case "quit", "exit" -> "quit";
                default -> "не знаю команды '" + parts[0] + "', введи help";
            };
        } catch (Exception e) {
            return "ошибка: " + e.getMessage();
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
