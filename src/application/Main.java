package application;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;

public class Main extends Application {

    private TextArea inputArea;
    private TextArea outputArea;
    private TableView<String[]> tableView;
    private Canvas canvas;

    @Override
    public void start(Stage stage) {

        Label title = new Label("Max LED Lighting Project");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: beige;");

        inputArea = new TextArea();
        inputArea.setPrefRowCount(7);

        Button solveBtn = new Button("Solve");
        Button fileBtn = new Button("Load Text File");
        Button sampleBtn = new Button("Sample");

        solveBtn.setOnAction(e -> solve());
        fileBtn.setOnAction(e -> loadFile(stage));
        sampleBtn.setOnAction(e -> inputArea.setText("10\n3\n1\n8\n2\n5\n4\n7\n6\n10\n9"));

        HBox buttons = new HBox(12, solveBtn, fileBtn, sampleBtn);

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefRowCount(8);

        tableView = new TableView<>();
        tableView.setPrefHeight(90);
        tableView.setMaxHeight(90);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        canvas = new Canvas(700, 700);

        VBox left = new VBox(
                8,
                label("Input"),
                inputArea,
                buttons,
                label("Output"),
                outputArea,
                label("1D DP Array"),
                tableView
        );
        left.setPadding(new Insets(20));
        left.setPrefWidth(650);

        VBox right = new VBox(
                8,
                label("GUI Drawing"),
                canvas
        );
        right.setPadding(new Insets(20));
        right.setPrefWidth(730);

        HBox content = new HBox(15, left, right);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0b1320;");
        root.setTop(title);
        root.setCenter(content);

        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane.setMargin(title, new Insets(10));

        styleButtons(solveBtn, fileBtn, sampleBtn);

        Scene scene = new Scene(root, 1350, 820);
        stage.setScene(scene);
        stage.setTitle("Max LED Lighting");
        stage.setMaximized(false);
        stage.setResizable(true);
        stage.show();
    }

    private Label label(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #777777; -fx-font-size: 15px;");
        return l;
    }

    private void solve() {
        try {
            int[] leds = InputReader.readInput(inputArea.getText());
            MaxLedLightingSolver.Result result = MaxLedLightingSolver.solveLed(leds);

            outputArea.setText(
                    "Maximum LEDs lighted: " + result.count + "\n" +
                    "Selected LEDs: " + MaxLedLightingSolver.toText(result.chosen) + "\n" +
                    "Input order: " + MaxLedLightingSolver.toText(result.input) + "\n" +
                    "DP Array: " + MaxLedLightingSolver.toText(result.dpArray) + "\n\n" +
                    result.info
            );

            drawDpArray(result);
            drawCircuit(result);

        } catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage());

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText("Invalid Input");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    private void loadFile(Stage stage) {
        try {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Choose Text File");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Text Files", "*.txt")
            );

            File file = chooser.showOpenDialog(stage);

            if (file != null) {
                inputArea.setText(Files.readString(file.toPath()));
            }

        } catch (Exception ex) {
            outputArea.setText("File Error: " + ex.getMessage());

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("File Error");
            alert.setHeaderText("Could not load file");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    private void drawDpArray(MaxLedLightingSolver.Result r) {
        tableView.getColumns().clear();
        tableView.getItems().clear();

        TableColumn<String[], String> indexCol = new TableColumn<>("i");
        indexCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue()[0])
        );
        tableView.getColumns().add(indexCol);

        TableColumn<String[], String> dpCol = new TableColumn<>("DP[i]");
        dpCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue()[1])
        );
        tableView.getColumns().add(dpCol);

        for (int i = 0; i < r.dpArray.length; i++) {
            String[] row = new String[2];

            row[0] = String.valueOf(i);
            row[1] = String.valueOf(r.dpArray[i]);

            tableView.getItems().add(row);
        }
    }
    private void drawCircuit(MaxLedLightingSolver.Result r) {
        GraphicsContext g = canvas.getGraphicsContext2D();

        double w = canvas.getWidth();
        double h = canvas.getHeight();

        g.setFill(Color.web("#0b1320"));
        g.fillRect(0, 0, w, h);

        int n = r.input.length;

        double leftX = 220;
        double rightX = 500;
        double topY = 55;
        double bottomY = h - 160;
        double gap = n == 1 ? 0 : (bottomY - topY) / (n - 1);

        g.setFont(Font.font(16));
        g.setFill(Color.BEIGE);
        g.fillText("Sources: 1 to n", leftX - 20, 35);
        g.fillText("LEDs input order", rightX - 55, h - 85);

        g.setStroke(Color.BEIGE);
        g.setLineWidth(2);
        g.strokeLine(leftX, topY, leftX, bottomY);
        g.strokeLine(rightX, topY, rightX, bottomY);

        for (int i = 0; i < n; i++) {
            double y = topY + i * gap;

            g.setFill(Color.BEIGE);
            g.fillOval(leftX - 16, y - 16, 32, 32);

            boolean ledSelected = isSelected(r.chosen, r.input[i]);

            if (ledSelected) {
                g.setFill(Color.LIGHTGREEN);
            } else {
                g.setFill(Color.BEIGE);
            }

            g.fillOval(rightX - 16, y - 16, 32, 32);

            g.setFill(Color.BLACK);
            g.fillText(String.valueOf(i + 1), leftX - 5, y + 5);
            g.fillText(String.valueOf(r.input[i]), rightX - 5, y + 5);
        }

        for (int i = 0; i < r.chosen.length; i++) {
            int ledValue = r.chosen[i];

            int sourceIndex = ledValue - 1;
            int ledIndex = findIndex(r.input, ledValue);

            double y1 = topY + sourceIndex * gap;
            double y2 = topY + ledIndex * gap;

            g.setStroke(Color.GREEN);
            g.setLineWidth(2);
            g.strokeLine(leftX + 16, y1, rightX - 16, y2);
        }
    }

    private boolean isSelected(int[] arr, int value) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == value) {
                return true;
            }
        }

        return false;
    }

    private int findIndex(int[] arr, int value) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == value) {
                return i;
            }
        }

        return -1;
    }

    private void styleButtons(Button... buttons) {
        for (Button b : buttons) {
            b.setStyle(
                    "-fx-background-color: beige;" +
                    "-fx-text-fill: black;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 8;"
            );
            b.setMinHeight(35);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}