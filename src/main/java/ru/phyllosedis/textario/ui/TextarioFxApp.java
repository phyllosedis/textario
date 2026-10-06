package ru.phyllosedis.textario.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import ru.phyllosedis.textario.console.CommandParser;
import ru.phyllosedis.textario.console.GameCommands;
import ru.phyllosedis.textario.engine.loop.TickGate;

import java.util.List;
import java.util.Map;

/**
 * Векторный вьювер мира на Canvas. Только читает WorldView
 * и шлёт команды через CommandParser — логики симуляции тут нет.
 * Тяни мышью чтобы двигать карту, колесо — зум.
 */
public class TextarioFxApp extends Application {

    private static final Map<String, Color> TERRAIN = Map.of(
            "EARTH", Color.web("#2b2b2b"),
            "IRON_ORE", Color.web("#8a4d2a"),
            "COPPER_ORE", Color.web("#2a8a74"),
            "COAL", Color.web("#4a4a4a"),
            "WATER", Color.web("#2a4d8f"),
            "UNDEFINED", Color.web("#111111")
    );

    private static final Map<Character, Color> ENTITY = Map.of(
            'M', Color.web("#e0a030"),
            'B', Color.web("#e0d030"),
            'I', Color.web("#30a0e0"),
            'S', Color.web("#a030e0"),
            'C', Color.web("#c08040"),
            'F', Color.web("#e04030")
    );

    private WorldView worldView;
    private GameCommands commands;
    private CommandParser parser;
    private TickGate tickGate;

    private Canvas canvas;
    private TextArea info;
    private Label status;
    private int cell = 28;
    private int offX;
    private int offY;
    private double dragStartX;
    private double dragStartY;
    private int dragOffX;
    private int dragOffY;

    @Override
    public void start(Stage stage) {
        var ctx = UiContext.get();
        worldView = ctx.getBean(WorldView.class);
        commands = ctx.getBean(GameCommands.class);
        parser = ctx.getBean(CommandParser.class);
        tickGate = ctx.getBean(TickGate.class);

        offX = 0;
        offY = 15;

        canvas = new Canvas(960, 700);
        canvas.setOnMousePressed(e -> {
            dragStartX = e.getX();
            dragStartY = e.getY();
            dragOffX = offX;
            dragOffY = offY;
        });
        canvas.setOnMouseDragged(e -> {
            offX = dragOffX - (int) ((e.getX() - dragStartX) / cell);
            offY = dragOffY - (int) ((e.getY() - dragStartY) / cell);
            redraw();
        });
        canvas.setOnScroll(e -> {
            if (e.getDeltaY() > 0 && cell < 56) {
                cell += 2;
            } else if (e.getDeltaY() < 0 && cell > 10) {
                cell -= 2;
            }
            redraw();
        });
        canvas.widthProperty().addListener((o, a, b) -> redraw());
        canvas.heightProperty().addListener((o, a, b) -> redraw());

        Button pause = new Button("Пауза");
        pause.setOnAction(e -> {
            boolean nowPaused = !tickGate.isPaused();
            tickGate.setPaused(nowPaused);
            pause.setText(nowPaused ? "Продолжить" : "Пауза");
            status.setText(nowPaused ? "пауза" : "тикает");
        });

        info = new TextArea();
        info.setEditable(false);
        info.setFont(Font.font("Monospaced", 12));
        VBox.setVgrow(info, Priority.ALWAYS);

        TextField cmd = new TextField();
        cmd.setPromptText("команда: help, inv, miner 5 40 IRON_ORE ...");
        cmd.setOnAction(e -> {
            String answer = parser.execute(cmd.getText());
            if ("quit".equals(answer)) {
                Platform.exit();
            } else {
                info.setText(answer + "\n---\n" + info.getText());
            }
            cmd.clear();
        });

        status = new Label("тикает");

        VBox side = new VBox(8, pause, status, info, cmd);
        side.setPadding(new Insets(8));
        side.setPrefWidth(340);

        HBox legend = new HBox(10, new Label("M бур  B лента  I рука  S разделитель  C сундук  F печь"));
        legend.setPadding(new Insets(4));

        BorderPane root = new BorderPane(canvas, null, side, legend, null);
        Scene scene = new Scene(root, 1320, 760);
        stage.setTitle("Textario");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> Platform.exit());
        stage.show();

        Timeline ticker = new Timeline(new KeyFrame(Duration.millis(400), e -> {
            redraw();
            info.setText(commands.inventories());
        }));
        ticker.setCycleCount(Timeline.INDEFINITE);
        ticker.play();
    }

    private void redraw() {
        if (canvas == null || worldView == null) {
            return;
        }
        int cols = Math.max(1, (int) (canvas.getWidth() / cell) + 1);
        int rows = Math.max(1, (int) (canvas.getHeight() / cell) + 1);
        List<WorldView.Cell> cells;
        try {
            cells = worldView.snapshot(offX, offY, cols, rows);
        } catch (Exception e) {
            return;
        }
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill(Color.web("#161616"));
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (WorldView.Cell c : cells) {
            double px = (c.x() - offX) * cell;
            double py = (c.y() - offY) * cell;
            Color base = TERRAIN.getOrDefault(c.terrain(), Color.web("#111111"));
            g.setFill(base);
            g.fillRect(px + 1, py + 1, cell - 2, cell - 2);
            if (c.occupied()) {
                Color entity = ENTITY.getOrDefault(c.glyph(), Color.WHITE);
                g.setFill(entity);
                double pad = Math.max(2, cell * 0.12);
                g.fillRoundRect(px + pad, py + pad, cell - pad * 2, cell - pad * 2, 6, 6);
                g.setFill(Color.BLACK);
                g.setFont(Font.font("Monospaced", Math.max(8, cell * 0.5)));
                g.fillText(String.valueOf(c.glyph()), px + cell * 0.32, py + cell * 0.68);
            }
        }
    }
}
