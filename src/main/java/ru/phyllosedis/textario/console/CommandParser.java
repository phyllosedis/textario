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
        try {
            return switch (parts[0].toLowerCase()) {
                case "help" -> """
                        map [x y w h] — показать карту
                        inv — показать склады и буферы
                        miner x y ORE — бур (IRON_ORE/COPPER_ORE/COAL)
                        belt x y [направление] | ins x y [направление]
                        направление: down/left/up/right (выход смотрит туда)
                        chest x y | fur x y
                        spl x y MODE — разделитель (ROUND_ROBIN/BALANCED/...)
                        rot x y — повернуть ленту/руку на 90°
                        quit — выход""";
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
}
