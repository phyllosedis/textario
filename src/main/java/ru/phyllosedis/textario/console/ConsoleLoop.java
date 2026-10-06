package ru.phyllosedis.textario.console;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.phyllosedis.textario.logistics.splitter.SplitMode;
import ru.phyllosedis.textario.resource.ResourceType;

import java.util.NoSuchElementException;
import java.util.Scanner;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@ConditionalOnProperty(name = "textario.console.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class ConsoleLoop implements CommandLineRunner {

    private final GameCommands commands;

    @Override
    public void run(String... args) {
        Thread repl = new Thread(this::loop, "textario-console");
        repl.setDaemon(true);
        repl.start();
    }

    private void loop() {
        System.out.println("Введи 'help' для списка команд.");
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("> ");
                String line;
                try {
                    line = scanner.nextLine();
                } catch (NoSuchElementException | IllegalStateException e) {
                    return;
                }
                System.out.println(handle(line.trim()));
            }
        }
    }

    private String handle(String line) {
        if (line.isEmpty()) {
            return "";
        }
        String[] parts = line.split("\\s+");
        try {
            return switch (parts[0].toLowerCase()) {
                case "help" -> """
                        map [x y w h] — показать карту
                        inv — показать склады и буферы
                        miner x y ORE — бур (IRON_ORE/COPPER_ORE/COAL)
                        belt x y | ins x y | chest x y | fur x y
                        spl x y MODE — разделитель (ROUND_ROBIN/BALANCED/...)
                        quit — выход""";
                case "map" -> parts.length >= 5
                        ? commands.map(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                                Integer.parseInt(parts[3]), Integer.parseInt(parts[4]))
                        : commands.map();
                case "inv" -> commands.inventories();
                case "miner" -> commands.placeMiner(
                        Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                        ResourceType.valueOf(parts[3].toUpperCase()));
                case "belt" -> commands.placeBelt(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                case "ins" -> commands.placeInserter(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                case "chest" -> commands.placeChest(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                case "fur" -> commands.placeFurnace(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                case "spl" -> commands.placeSplitter(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                        SplitMode.valueOf(parts[3].toUpperCase()));
                case "quit", "exit" -> {
                    System.out.println("Пока!");
                    System.exit(0);
                    yield "";
                }
                default -> "не знаю команды '" + parts[0] + "', введи help";
            };
        } catch (Exception e) {
            return "ошибка: " + e.getMessage();
        }
    }
}
