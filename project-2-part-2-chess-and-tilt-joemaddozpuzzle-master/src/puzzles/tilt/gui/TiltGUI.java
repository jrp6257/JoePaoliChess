package puzzles.tilt.gui;

import javafx.application.Application;
import puzzles.tilt.model.TiltConfig;
import puzzles.tilt.model.Direction;
import puzzles.tilt.model.TiltModel;
import javafx.scene.layout.GridPane;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import puzzles.tilt.model.Piece;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import puzzles.common.Observer;
import javafx.geometry.Insets;
import java.nio.file.Paths;
import javafx.geometry.Pos;
import java.nio.file.Path;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.io.File;

/**
 * The {@code TiltGUI} class represents the graphical user interface for the tilt puzzle game.
 * It allows the user to interact with the game by tilting the board, loading a new puzzle,
 * getting hints, and resetting the game.
 *
 * @see TiltModel
 * @see Direction
 * @see Piece
 * 
 * @author Maddox Van Sickel
 */
public class TiltGUI extends Application implements Observer<TiltModel, String> {
    /** The size of the board in pixels */
    private final int BOARD_SIZE = 600;
    /** The resources directory is located directly underneath the gui package */
    private final static String RESOURCES_DIR = "resources/";
    /** The location of the board grid pane within the outer root grid pane */
    private final int BOARD_ROW = 2;
    private final int BOARD_COL = 1;

    /** The model for the tilt game */
    private TiltModel model;

    /** The images for the pieces on the board */
    private Image greenDiskImg = new Image(getClass().getResourceAsStream(RESOURCES_DIR + "green.png"));
    private Image blueDiskImg = new Image(getClass().getResourceAsStream(RESOURCES_DIR + "blue.png"));
    private Image holeImg = new Image(getClass().getResourceAsStream(RESOURCES_DIR + "hole.png"));
    private Image blockImg = new Image(getClass().getResourceAsStream(RESOURCES_DIR + "block.png"));

    /** The nodes for creating the GUI */
    private FileChooser fileChooser;
    private Label messageLabel;
    private GridPane boardGridPane;
    private Button northButton;
    private Button eastButton;
    private Button southButton;
    private Button westButton;
    private Button loadButton;
    private Button resetButton;
    private Button hintButton;
    private VBox controlsVBox;
    private GridPane root;

    @Override
    public void init() {
        File tiltFile = new File(getParameters().getRaw().get(0));
        model = new TiltModel(tiltFile);
        model.addObserver(this);
    }

    @Override
    public void start(Stage stage) throws Exception {
        fileChooser = new FileChooser();
        fileChooser.setTitle("Load board from file");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Tilt Board Files", "*.txt"));
        Path tiltFilesPath = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        File tiltFile = tiltFilesPath
                            .resolve("data")
                            .resolve("tilt")
                            .toFile();
        fileChooser.setInitialDirectory(tiltFile);

        messageLabel = new Label("Message: Welcome to Tilt!");
        messageLabel.setStyle("-fx-font-weight: bold; -fx-font-family: Arial; -fx-font-size: 20px;");
        messageLabel.setPadding(new Insets(10));

        boardGridPane = generateGridPaneFromModel();

        northButton = new Button("^");
        northButton.setStyle("-fx-font-weight: bold; -fx-font-family: Arial; -fx-font-size: 20px;");
        northButton.prefWidthProperty().bind(boardGridPane.widthProperty());
        addTiltEvent(northButton, Direction.NORTH);

        eastButton = new Button(">");
        eastButton.setStyle("-fx-font-weight: bold; -fx-font-family: Arial; -fx-font-size: 20px;");
        eastButton.prefHeightProperty().bind(boardGridPane.heightProperty());
        addTiltEvent(eastButton, Direction.EAST);

        southButton = new Button("v");
        southButton.setStyle("-fx-font-weight: bold; -fx-font-family: Arial; -fx-font-size: 20px;");
        southButton.prefWidthProperty().bind(boardGridPane.widthProperty());
        addTiltEvent(southButton, Direction.SOUTH);

        westButton = new Button("<");
        westButton.setStyle("-fx-font-weight: bold; -fx-font-family: Arial; -fx-font-size: 20px;");
        westButton.prefHeightProperty().bind(boardGridPane.heightProperty());
        addTiltEvent(westButton, Direction.WEST);
        
        loadButton = new Button("Load");
        loadButton.setStyle("-fx-font-weight: bold; -fx-font-family: Arial; -fx-font-size: 20px;");
        addLoadEvent(loadButton);

        resetButton = new Button("Reset");
        resetButton.setStyle("-fx-font-weight: bold; -fx-font-family: Arial; -fx-font-size: 20px;");
        addResetEvent(resetButton);

        hintButton = new Button("Hint");
        hintButton.setStyle("-fx-font-weight: bold; -fx-font-family: Arial; -fx-font-size: 20px;");
        addHintEvent(hintButton);

        controlsVBox = new VBox();
        controlsVBox.setSpacing(10);
        controlsVBox.setPadding(new Insets(10));
        controlsVBox.getChildren()
            .addAll(loadButton, resetButton, hintButton);
        controlsVBox.setAlignment(Pos.CENTER);

        root = new GridPane();
        root.add(messageLabel, 1, 0);
        root.add(northButton, 1, 1);
        root.add(eastButton, 2, 2);
        root.add(southButton, 1, 3);
        root.add(westButton, 0, 2);
        root.add(boardGridPane, BOARD_COL, BOARD_ROW);
        root.add(controlsVBox, 3, 2);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Returns the image corresponding to the given piece.
     * @param piece the piece to get the image for
     * @return the image for the given piece
     */
    private Image getPieceImage(Piece piece) {
        switch (piece) {
            case SLIDER_GREEN: return greenDiskImg;
            case SLIDER_BLUE: return blueDiskImg;
            case BLOCKER: return blockImg;
            case HOLE: return holeImg;
            default: return new ImageView().getImage();
        }
    }

    /**
     * Generates a GridPane from the current model's tilt board configuration.
     * @return a GridPane representing the tilt board
     */
    private GridPane generateGridPaneFromModel() {
        GridPane gridPane = new GridPane();
        for (int r = 0; r < TiltConfig.dimensions; r++) {
            for (int c = 0; c < TiltConfig.dimensions; c++) {
                Piece piece = model.getPieceAt(r, c);
                ImageView imgView = new ImageView(getPieceImage(piece));
                imgView.setFitHeight(BOARD_SIZE / TiltConfig.dimensions);
                imgView.setFitWidth(BOARD_SIZE / TiltConfig.dimensions);
                gridPane.add(imgView, c, r);
            }
        }
        return gridPane;
    }

    /**
     * Adds an event handler to the given button that tilts the board in the specified direction.
     * @param button the button to add the event handler to
     * @param direction the direction to tilt the board
     */
    private void addTiltEvent(Button button, Direction direction) {
        button.setOnAction(event -> model.tilt(direction));
    }

    /**
     * Adds an event handler to the given button that loads a new tilt board from a file.
     * @param button the button to add the event handler to
     */
    private void addLoadEvent(Button button) {
        button.setOnAction(event -> {
            File tiltFile = fileChooser.showOpenDialog(null);
            model.loadBoard(tiltFile);
        });
    }

    /**
     * Adds an event handler to the given button that resets the tilt board to its original configuration.
     * @param button the button to add the event handler to
     */
    private void addResetEvent(Button button) {
        button.setOnAction(event -> model.reset());
    }

    /**
     * Adds an event handler to the given button that provides a "hint" for the next move (updates the board to the next move).
     * @param button the button to add the event handler to
     */
    private void addHintEvent(Button button) {
        button.setOnAction(event -> model.getHint());
    }

    @Override
    public void update(TiltModel tiltModel, String message) {
        messageLabel.setText("Message: " + message);
        root.getChildren().removeIf(node ->
            GridPane.getColumnIndex(node) != null && GridPane.getRowIndex(node) != null &&
            GridPane.getColumnIndex(node) == BOARD_COL && GridPane.getRowIndex(node) == BOARD_ROW);
        root.add(generateGridPaneFromModel(), BOARD_COL, BOARD_ROW);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java TiltGUI filename");
            System.exit(0);
        } else {
            Application.launch(args);
        }
    }
}
