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
import ru.phyllosedis.textario.logistics.port.PortSide;
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
            'F', Color.web("#e04030"),
            'A', Color.web("#30c060"),
            'U', Color.web("#707070")
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
    private ContextMenu activeMenu;

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

        Button saveBtn = new Button("Сохранить");
        saveBtn.setOnAction(e -> info.setText(parser.execute("save") + "\n---\n" + info.getText()));
        Button loadBtn = new Button("Загрузить");
        loadBtn.setOnAction(e -> {
            info.setText(parser.execute("load") + "\n---\n" + info.getText());
            redraw();
        });
        HBox saveRow = new HBox(8, pause, saveBtn, loadBtn);

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

        VBox side = new VBox(8, saveRow, statusSim, statusPos, info, cmd);
        side.setPadding(new Insets(8));
        side.setPrefWidth(340);

        HBox legend = new HBox(10,
                new Label("стрелка = куда течёт | рычаг с шарниром = рука | ЛКМ выбрать/тянуть  ПКМ построить"));  
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
        // Одно меню на всех: старое закрываем, новое показываем
        if (activeMenu != null) {
            activeMenu.hide();
            activeMenu = null;
        }
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

        Long entityId = commands.entityAt(x, y);
        if (entityId != null && commands.isRotatable(entityId)) {
            menu.getItems().add(new SeparatorMenuItem());
            MenuItem rotate = new MenuItem("Повернуть ↻");
            rotate.setOnAction(ev -> {
                statusPos.setText(commands.rotateAt(x, y));
                redraw();
            });
            menu.getItems().add(rotate);
        }
        if (entityId != null && commands.isAssembler(entityId)) {
            javafx.scene.control.Menu recipeMenu = new javafx.scene.control.Menu("Рецепт");
            for (ru.phyllosedis.textario.production.recipe.RecipeBook.Recipe recipe : commands.assemblerRecipes()) {
                MenuItem item = new MenuItem(recipe.name() + " (" + recipe.id() + ")");
                item.setOnAction(ev -> {
                    statusPos.setText(commands.setRecipe(x + ":" + y, recipe.id()));
                    redraw();
                });
                recipeMenu.getItems().add(item);
            }
            menu.getItems().add(recipeMenu);
        }
        if (entityId != null) {
            MenuItem demolish = new MenuItem("Снести ✕");
            demolish.setOnAction(ev -> {
                statusPos.setText(commands.demolish(x + ":" + y));
                redraw();
            });
            menu.getItems().add(demolish);
        }

        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().add(buildItem("Бур (руда сама)", () -> commands.placeMiner(x, y)));
        menu.getItems().add(dirMenu("Лента", (xx, yy, rot) -> commands.placeBelt(xx, yy, rot), x, y));
        menu.getItems().add(dirMenu("Рука", (xx, yy, rot) -> commands.placeInserter(xx, yy, rot), x, y));
        menu.getItems().add(buildItem("Сундук", () -> commands.placeChest(x, y)));
        menu.getItems().add(buildItem("Печь", () -> commands.placeFurnace(x, y)));
        menu.getItems().add(buildItem("Сборщик", () -> commands.placeAssembler(x, y)));
        menu.getItems().add(dirMenu("Разделитель",
                (xx, yy, rot) -> commands.placeSplitter(xx, yy, SplitMode.ROUND_ROBIN, rot), x, y));
        javafx.scene.control.Menu under = new javafx.scene.control.Menu("Подземка");
        javafx.scene.control.Menu entry = dirMenu("Вход",
                (xx, yy, rot) -> commands.placeUnderground(xx, yy, rot, "entry"), x, y);
        javafx.scene.control.Menu exit = dirMenu("Выход",
                (xx, yy, rot) -> commands.placeUnderground(xx, yy, rot, "exit"), x, y);
        under.getItems().addAll(entry, exit);
        menu.getItems().add(under);
        menu.getItems().add(new SeparatorMenuItem());
        MenuItem cancel = new MenuItem("Отмена (Esc)");
        cancel.setOnAction(ev -> menu.hide());
        menu.getItems().add(cancel);
        menu.setAutoHide(true);
        menu.setHideOnEscape(true);
        menu.setOnHidden(ev -> {
            if (activeMenu == menu) {
                activeMenu = null;
            }
        });
        activeMenu = menu;
        menu.show(canvas, e.getScreenX(), e.getScreenY());
    }

    private javafx.scene.control.Menu dirMenu(String name, Place3 place, int x, int y) {
        javafx.scene.control.Menu menu = new javafx.scene.control.Menu(name);
        String[] names = {"↓ вниз", "← влево", "↑ вверх", "→ вправо"};
        for (int rot = 0; rot < 4; rot++) {
            final int direction = rot;
            MenuItem item = new MenuItem(names[rot]);
            item.setOnAction(e -> {
                statusPos.setText(place.place(x, y, direction));
                redraw();
            });
            menu.getItems().add(item);
        }
        return menu;
    }

    private interface Place3 {
        String place(int x, int y, int rotation);
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
                drawEntityIcon(g, c, px, py, cell);
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

    /**
     * Векторный значок постройки вместо буквы. Направление портов:
     * BACK = вверх, FRONT = вниз, LEFT = влево, RIGHT = вправо.
     */
    private void drawEntityIcon(GraphicsContext g, WorldView.Cell c, double px, double py, double s) {
        double cx = px + s / 2.0;
        double cy = py + s / 2.0;
        g.setFill(Color.BLACK);
        g.setStroke(Color.BLACK);

        if (!c.inputs().isEmpty() || !c.outputs().isEmpty()) {
            drawFlowIcon(g, c, px, py, s, cx, cy);
            return;
        }
        switch (c.glyph()) {
            case 'M' -> {
                // Бур: кольцо + сверло вниз
                g.setLineWidth(Math.max(1.5, s * 0.07));
                g.strokeOval(cx - s * 0.28, cy - s * 0.32, s * 0.56, s * 0.56);
                g.fillPolygon(
                        new double[]{cx - s * 0.12, cx + s * 0.12, cx},
                        new double[]{cy - s * 0.05, cy - s * 0.05, cy + s * 0.38}, 3);
            }
            case 'F' -> {
                // Печь: пламя треугольником вверх
                g.fillPolygon(
                        new double[]{cx - s * 0.26, cx + s * 0.26, cx},
                        new double[]{cy + s * 0.32, cy + s * 0.32, cy - s * 0.36}, 3);
                g.setFill(Color.web("#ffb060"));
                g.fillPolygon(
                        new double[]{cx - s * 0.11, cx + s * 0.11, cx},
                        new double[]{cy + s * 0.24, cy + s * 0.24, cy - s * 0.14}, 3);
            }
            case 'C' -> {
                // Сундук: ящик с крышкой
                g.setLineWidth(Math.max(1.5, s * 0.07));
                g.strokeRect(cx - s * 0.3, cy - s * 0.22, s * 0.6, s * 0.5);
                g.setLineWidth(Math.max(1, s * 0.05));
                g.strokeLine(cx - s * 0.3, cy - s * 0.05, cx + s * 0.3, cy - s * 0.05);
                g.fillRect(cx - s * 0.05, cy - s * 0.1, s * 0.1, s * 0.12);
            }
            case 'A' -> {
                // Сборщик: шестерня
                g.setLineWidth(Math.max(1.5, s * 0.08));
                for (int i = 0; i < 6; i++) {
                    double a = Math.PI / 3 * i;
                    double x1 = cx + Math.cos(a) * s * 0.22;
                    double y1 = cy + Math.sin(a) * s * 0.22;
                    double x2 = cx + Math.cos(a) * s * 0.36;
                    double y2 = cy + Math.sin(a) * s * 0.36;
                    g.strokeLine(x1, y1, x2, y2);
                }
                g.strokeOval(cx - s * 0.22, cy - s * 0.22, s * 0.44, s * 0.44);
                g.fillOval(cx - s * 0.08, cy - s * 0.08, s * 0.16, s * 0.16);
            }
            case 'U' -> {
                // Подземка: тёмный зев + стрелка (вход — внутрь, выход — наружу)
                g.setLineWidth(Math.max(1.5, s * 0.08));
                g.strokeOval(cx - s * 0.24, cy - s * 0.24, s * 0.48, s * 0.48);
                g.fillOval(cx - s * 0.17, cy - s * 0.17, s * 0.34, s * 0.34);
                boolean entry = "ENTRY".equals(c.tag());
                PortSide side = entry
                        ? c.inputs().stream().findFirst().orElse(PortSide.BACK)
                        : c.outputs().stream().findFirst().orElse(PortSide.FRONT);
                double[] dir = dirOf(side);
                double[] edge = edgeCenter(side, px, py, s);
                g.setStroke(Color.BLACK);
                g.setLineWidth(Math.max(2, s * 0.12));
                if (entry) {
                    g.strokeLine(edge[0], edge[1], cx + dir[0] * s * 0.1, cy + dir[1] * s * 0.1);
                    drawHead(g, new double[]{cx + dir[0] * s * 0.1, cy + dir[1] * s * 0.1},
                            dir, Math.max(4, s * 0.18), true);
                } else {
                    g.strokeLine(cx - dir[0] * s * 0.1, cy - dir[1] * s * 0.1, edge[0], edge[1]);
                    drawHead(g, edge, dir, Math.max(4, s * 0.18), true);
                }
            }
            default -> {
                g.setFont(Font.font("Monospaced", Math.max(8, s * 0.5)));
                g.setTextAlign(TextAlignment.CENTER);
                g.fillText(String.valueOf(c.glyph()), cx, cy + s * 0.18);
                g.setTextAlign(TextAlignment.LEFT);
            }
        }
    }

    private void drawFlowIcon(GraphicsContext g, WorldView.Cell c, double px, double py, double s,
                              double cx, double cy) {
        boolean singleLane = c.inputs().size() == 1 && c.outputs().size() == 1;
        boolean isInserter = c.glyph() == 'I';

        if (singleLane) {
            // Вал от входа к выходу: у ленты толстый (ролик), у руки тонкий (рычаг)
            double[] in = edgeCenter(c.inputs().get(0), px, py, s);
            double[] out = edgeCenter(c.outputs().get(0), px, py, s);
            g.setLineWidth(isInserter ? Math.max(1.5, s * 0.1) : Math.max(2, s * 0.2));
            g.strokeLine(in[0], in[1], out[0], out[1]);
            if (isInserter) {
                // Шарнир рычага + захват на входе
                g.fillOval(cx - s * 0.13, cy - s * 0.13, s * 0.26, s * 0.26);
                g.setFill(Color.web("#e0e0e0"));
                g.fillRect(in[0] - s * 0.09, in[1] - s * 0.09, s * 0.18, s * 0.18);
                g.setFill(Color.BLACK);
            } else {
                // Ролик ленты по центру
                g.strokeOval(cx - s * 0.14, cy - s * 0.14, s * 0.28, s * 0.28);
            }
        }

        for (PortSide in : c.inputs()) {
            drawHead(g, inEdgeTip(in, px, py, s, true), dirOf(in), Math.max(3, s * 0.16), false);
        }
        for (PortSide out : c.outputs()) {
            drawHead(g, edgeCenter(out, px, py, s), dirOf(out), Math.max(4, s * 0.2), true);
        }
    }

    private double[] dirOf(PortSide side) {
        return switch (side) {
            case BACK -> new double[]{0, -1};
            case FRONT -> new double[]{0, 1};
            case LEFT -> new double[]{-1, 0};
            case RIGHT -> new double[]{1, 0};
            default -> new double[]{0, 1};
        };
    }

    private double[] edgeCenter(PortSide side, double px, double py, double s) {
        return switch (side) {
            case BACK -> new double[]{px + s / 2.0, py + 1};
            case FRONT -> new double[]{px + s / 2.0, py + s - 1};
            case LEFT -> new double[]{px + 1, py + s / 2.0};
            case RIGHT -> new double[]{px + s - 1, py + s / 2.0};
            default -> new double[]{px + s / 2.0, py + s - 1};
        };
    }

    private double[] inEdgeTip(PortSide side, double px, double py, double s, boolean inward) {
        double[] e = edgeCenter(side, px, py, s);
        double[] d = dirOf(side);
        double k = inward ? -s * 0.22 : 0;
        return new double[]{e[0] + d[0] * k, e[1] + d[1] * k};
    }

    private void drawHead(GraphicsContext g, double[] tip, double[] dir, double size, boolean filled) {
        double bx = tip[0] - dir[0] * size;
        double by = tip[1] - dir[1] * size;
        double nx = -dir[1] * size * 0.6;
        double ny = dir[0] * size * 0.6;
        if (filled) {
            g.setFill(Color.BLACK);
            g.fillPolygon(
                    new double[]{tip[0], bx + nx, bx - nx},
                    new double[]{tip[1], by + ny, by - ny}, 3);
        } else {
            g.setStroke(Color.BLACK);
            g.setLineWidth(Math.max(1, size * 0.25));
            g.strokePolyline(
                    new double[]{bx + nx, tip[0], bx - nx},
                    new double[]{by + ny, tip[1], by - ny}, 3);
        }
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
