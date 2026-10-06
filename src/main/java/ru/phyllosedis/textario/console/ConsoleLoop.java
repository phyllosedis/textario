package ru.phyllosedis.textario.console;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.Scanner;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@ConditionalOnProperty(name = "textario.console.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class ConsoleLoop implements CommandLineRunner {

    private final CommandParser parser;

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
                String answer = parser.execute(line.trim());
                if ("quit".equals(answer)) {
                    System.out.println("Пока!");
                    System.exit(0);
                }
                System.out.println(answer);
            }
        }
    }
}
