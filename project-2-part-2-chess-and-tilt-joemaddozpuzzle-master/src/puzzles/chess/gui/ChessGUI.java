package puzzles.chess.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import puzzles.chess.model.ChessModel;
import puzzles.chess.model.Piece;
import puzzles.common.Observer;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;

/**
 * GUI based on chess board
 * @author Joe Paoli
 */
public class ChessGUI extends Application implements Observer<ChessModel, String> {
    /** The resources directory is located directly underneath the gui package */
    private final static String RESOURCES_DIR = "resources/";
    private ChessModel model;
    private Button[][] buttons;
    /** Images for each piece */
    private Image pawn = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"pawn.png"));
    private Image knight = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"knight.png"));
    private Image king = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"king.png"));
    private Image rook = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"rook.png"));
    private Image bishop = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"bishop.png"));
    private Image queen = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"queen.png"));
    /** All other GUI elements */
    private Label messageLabel;
    private final Button loadButton = new Button("Load");
    private final Button resetButton = new Button("Reset");
    private final Button hintButton = new Button("Hint");
    private FileChooser fileChooser;
    private GridPane gridPane;
    private BorderPane borderPane;
    private Stage currentStage;

    /**
     * Gets the initial file for the GUI and adds model as observer
     */
    public void init() {
        String filename = getParameters().getRaw().get(0);
        File chessFile = new File(filename);
        model = new ChessModel(chessFile);
        model.addObserver(this);
    }

    /**
     * Sets up the initial GUI
     * @param stage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        fileChooser = new FileChooser();
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        currentPath += File.separator + "data" + File.separator + "chess";
        fileChooser.setInitialDirectory(new File(currentPath));
        messageLabel = new Label("Welcome to Chess!");
        loadButton.setOnAction(event -> {
                    File chessFile = fileChooser.showOpenDialog(null);
                    model.loadFile(chessFile);
                });
        resetButton.setOnAction(event -> model.reset());
        hintButton.setOnAction(event -> model.getHint());
        borderPane = new BorderPane();
        gridPane = makeGridPane();
        FlowPane bottomFlowPane = new FlowPane();
        bottomFlowPane.getChildren().addAll(loadButton, resetButton, hintButton);
        borderPane.setBottom(bottomFlowPane);
        bottomFlowPane.setAlignment(Pos.CENTER);
        borderPane.setTop(messageLabel);
        borderPane.setAlignment(messageLabel, Pos.CENTER);
        borderPane.setCenter(gridPane);
        Scene scene = new Scene(borderPane);
        stage.setTitle("Chess Solitaire");
        stage.setScene(scene);
        currentStage = stage;
        stage.show();
    }

    /**
     * Gets the image for a cell based on the piece at that position
     * @param piece- the chess piece
     * @return- image of piece
     */
    public Image getPieceImage(Piece piece) {
        switch (piece) {
            case KING -> {
                return king;
            }
            case PAWN -> {
                return pawn;
            }
            case KNIGHT -> {
                return knight;
            }
            case ROOK -> {
                return rook;
            }
            case BISHOP -> {
                return bishop;
            }
            case QUEEN -> {
                return queen;
            }
            default -> {
                return new ImageView().getImage();
            }
        }
    }

    /**
     * Makes a grid pane when GUI is launched and when board changes
     * @return
     */
    public GridPane makeGridPane() {
        if (model.currentConfig == null) {
            System.err.println("Invalid file");
            System.exit(0);
        }
        GridPane newGridPane = new GridPane();
        Button[][] buttons = new Button[model.currentConfig.getHeight()][model.currentConfig.getHeight()];
        newGridPane.setMaxSize(500, 500);
        for (int x = 0; x < model.currentConfig.getHeight(); ++x) {
            for (int y = 0; y < model.currentConfig.getLength(); ++y) {
                Piece piece = model.getPiece(x, y);
                Button button = new Button();
                ImageView imgView = new ImageView(getPieceImage(piece));
                imgView.setFitHeight(500 / model.currentConfig.getHeight());
                imgView.setFitWidth(500 / model.currentConfig.getLength());
                button.setGraphic(imgView);
                if (y != 0) {
                    if (buttons[x][y - 1].getStyle().equals("-fx-background-color: #ffffff;")) {
                        button.setStyle("-fx-background-color: #000000;");
                    } else {
                        button.setStyle("-fx-background-color: #ffffff;");
                    }
                } else {
                    if (x != 0) {
                        if (buttons[x - 1][y].getStyle().equals("-fx-background-color: #ffffff;")) {
                            button.setStyle("-fx-background-color: #000000;");
                        } else {
                            button.setStyle("-fx-background-color: #ffffff;");
                        }
                    } else {
                        button.setStyle("-fx-background-color: #ffffff;");
                    }
                }
                final int finalX = x;
                final int finalY = y;
                button.setOnAction(event -> model.movePiece(finalX, finalY));
                buttons[x][y] = button;
                newGridPane.add(button, y, x);
            }
        }
        gridPane = newGridPane;
        return newGridPane;
    }


    /**
     * Updated the GUI based on the model
     * @param chessModel the object that wishes to inform this object
     *                about something that has happened.
     * @param message optional data the server.model can send to the observer
     *
     */
    @Override
    public void update(ChessModel chessModel, String message) {
        messageLabel.setText(message);
        borderPane.setCenter(null);
        GridPane newGridPane = makeGridPane();
        borderPane.setCenter(newGridPane);
        currentStage.sizeToScene();
    }

    /**
     * Main method to launch the GUI
     * @param args- command line arguments
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: java ChessGUI filename");
            System.exit(0);
        } else {
            Application.launch(args);
        }
    }
}
