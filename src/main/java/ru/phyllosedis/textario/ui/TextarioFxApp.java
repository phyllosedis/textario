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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import ru.phyllosedis.textario.console.CommandParser;
import ru.phyllosedis.textario.console.GameCommands;
import ru.phyllosedis.textario.engine.loop.TickGate;
import ru.phyllosedis.textario.logistics.splitter.SplitMode;
import ru.phyllosedis.textario.resource.ResourceType;

import java.util.List;
import java.util.Map;

/**
 * Векторный вьювер мира на Canvas. Только читает WorldView
 * и шлёт команды через CommandParser/GameCommands — логики
 * симуляции тут нет.
 * ЛКМ — выбрать клетку, ПКМ — подменю постройки,
 * тяни с ЛКМ — двигать карту, колесо — зум.
 */
public class TextarioFxApp extends Application {

    private static final double RULER = 44;

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
    private Label statusSim;
    private Label statusPos;
    private int cell = 28;
    private int offX;
    private int offY;
    private int selX = Integer.MIN_VALUE;
    private int selY = Integer.MIN_VALUE;
    private double pressX;
    private double pressY;
    private int pressOffX;
    private int pressOffY;
    private boolean dragged;

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
            pressX = e.getX();
            pressY = e.getY();
            pressOffX = offX;
            pressOffY = offY;
            dragged = false;
        });
        canvas.setOnMouseDragged(e -> {
            int dx = (int) ((e.getX() - pressX) / cell);
            int dy = (int) ((e.getY() - pressY) / cell);
            if (dx != 0 || dy != 0) {
                dragged = true;
            }
            offX = pressOffX - dx;
            offY = pressOffY - dy;
            redraw();
        });
        canvas.setOnMouseClicked(e -> {
            if (dragged || e.getButton() != javafx.scene.input.MouseButton.PRIMARY) {
                return;
            }
            int[] cellPos = toWorld(e.getX(), e.getY());
            if (cellPos != null) {
                selX = cellPos[0];
                selY = cellPos[1];
                statusPos.setText(safeDescribe(selX, selY));
                redraw();
            }
        });
        canvas.setOnMouseMoved(e -> {
            int[] cellPos = toWorld(e.getX(), e.getY());
            if (cellPos != null) {
                statusPos.setText(safeDescribe(cellPos[0], cellPos[1]));
            }
        });
        canvas.setOnScroll(e -> {
            if (e.getDeltaY() > 0 && cell < 56) {
                cell += 2;
            } else if (e.getDeltaY() < 0 && cell > 10) {
                cell -= 2;
            }
            redraw();
        });
        canvas.setOnContextMenuRequested(this::showBuildMenu);
        canvas.widthProperty().addListener((o, a, b) -> redraw());
        canvas.heightProperty().addListener((o, a, b) -> redraw());

        Button pause = new Button("Пауза");
        pause.setOnAction(e -> {
            boolean nowPaused = !tickGate.isPaused();
            tickGate.setPaused(nowPaused);
            pause.setText(nowPaused ? "Продолжить" : "Пауза");
            statusSim.setText(nowPaused ? "пауза" : "тикает");
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

        statusSim = new Label("тикает");
        statusPos = new Label("?:?");

        VBox side = new VBox(8, pause, statusSim, statusPos, info, cmd);
        side.setPadding(new Insets(8));
        side.setPrefWidth(340);

        HBox legend = new HBox(10,
                new Label("M бур  B лента  I рука  S разделитель  C сундук  F печь | ЛКМ выбрать/тянуть  ПКМ построить"));
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

    private void showBuildMenu(ContextMenuEvent e) {
        int[] cellPos = toWorld(e.getX(), e.getY());
        if (cellPos == null) {
            return;
        }
        int x = cellPos[0];
        int y = cellPos[1];
        selX = x;
        selY = y;

        ContextMenu menu = new ContextMenu();
        MenuItem title = new MenuItem("Клетка " + x + ":" + y + " — " + safeDescribe(x, y));
        title.setDisable(true);
        menu.getItems().add(title);
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().add(buildItem("Бур (Fe)", () -> commands.placeMiner(x, y, ResourceType.IRON_ORE)));
        menu.getItems().add(buildItem("Бур (Cu)", () -> commands.placeMiner(x, y, ResourceType.COPPER_ORE)));
        menu.getItems().add(buildItem("Бур (уголь)", () -> commands.placeMiner(x, y, ResourceType.COAL)));
        menu.getItems().add(buildItem("Лента", () -> commands.placeBelt(x, y)));
        menu.getItems().add(buildItem("Рука", () -> commands.placeInserter(x, y)));
        menu.getItems().add(buildItem("Сундук", () -> commands.placeChest(x, y)));
        menu.getItems().add(buildItem("Печь", () -> commands.placeFurnace(x, y)));
        menu.getItems().add(buildItem("Разделитель", () -> commands.placeSplitter(x, y, SplitMode.ROUND_ROBIN)));
        menu.show(canvas, e.getScreenX(), e.getScreenY());
    }

    private MenuItem buildItem(String name, BuildAction action) {
        MenuItem item = new MenuItem(name);
        item.setOnAction(e -> {
            String result = action.build();
            statusPos.setText(result);
            redraw();
        });
        return item;
    }

    private interface BuildAction {
        String build();
    }

    private int[] toWorld(double mouseX, double mouseY) {
        if (mouseX < RULER || mouseY < RULER) {
            return null;
        }
        int x = offX + (int) ((mouseX - RULER) / cell);
        int y = offY + (int) ((mouseY - RULER) / cell);
        if (x < 0 || y < 0 || x >= worldView.mapWidth() || y >= worldView.mapHeight()) {
            return null;
        }
        return new int[]{x, y};
    }

    private String safeDescribe(int x, int y) {
        try {
            return worldView.describe(x, y);
        } catch (Exception e) {
            return x + ":" + y + " ?";
        }
    }

    private void redraw() {
        if (canvas == null || worldView == null) {
            return;
        }
        int cols = Math.max(1, (int) ((canvas.getWidth() - RULER) / cell) + 1);
        int rows = Math.max(1, (int) ((canvas.getHeight() - RULER) / cell) + 1);
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
            double px = RULER + (c.x() - offX) * cell;
            double py = RULER + (c.y() - offY) * cell;
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

        if (selX != Integer.MIN_VALUE) {
            double px = RULER + (selX - offX) * cell;
            double py = RULER + (selY - offY) * cell;
            g.setStroke(Color.WHITE);
            g.setLineWidth(2);
            g.strokeRect(px + 1, py + 1, cell - 2, cell - 2);
        }

        drawRuler(g, cols, rows);
    }

    private void drawRuler(GraphicsContext g, int cols, int rows) {
        g.setFill(Color.web("#0d0d0d"));
        g.fillRect(0, 0, canvas.getWidth(), RULER);
        g.fillRect(0, 0, RULER, canvas.getHeight());
        g.setFill(Color.web("#9a9a9a"));
        g.setFont(Font.font("Monospaced", 11));
        g.setTextAlign(TextAlignment.CENTER);

        int step = cell >= 20 ? 1 : cell >= 12 ? 2 : 5;
        for (int i = 0; i < cols; i++) {
            int x = offX + i;
            if (x % step != 0) {
                continue;
            }
            g.fillText(String.valueOf(x), RULER + i * cell + cell / 2.0, 18);
            g.fillText(String.valueOf(x), RULER + i * cell + cell / 2.0, 32);
        }
        g.setTextAlign(TextAlignment.RIGHT);
        for (int j = 0; j < rows; j++) {
            int y = offY + j;
            if (y % step != 0) {
                continue;
            }
            g.fillText(String.valueOf(y), RULER - 6, RULER + j * cell + cell / 2.0 + 4);
        }
        g.setTextAlign(TextAlignment.LEFT);
    }
}
