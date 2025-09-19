package puzzles.tilt.model;

import puzzles.common.solver.Solver.SolverData;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import puzzles.common.Observer;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

/**
 * The {@code TiltModel} class represents the model for the tilt puzzle game.
 * It manages the current configuration of the tilt board and notifies observers of changes.
 * The model allows loading a tilt board from a file, tilting the board in a specified direction,
 * and getting hints for the next move.
 *
 * @see TiltConfig
 * @see Direction
 * @see Piece
 * 
 * @author Maddox Van Sickel
 */
public class TiltModel {
    /** the collection of observers of this model */
    private final List<Observer<TiltModel, String>> observers = new LinkedList<>();

    /** the current configuration */
    private TiltConfig currentConfig;
    /** the file that was loaded */
    private File tiltFile;

    /**
     * Constructor for the model. The model is initialized with a tilt file.
     * @param tiltFile the file to load
     */
    public TiltModel(File tiltFile) {
        loadBoard(tiltFile);
    }

    /**
     * Updates the current configuration of the tilt board to be the next step in the puzzle.
     * If no next step exists then the user is alerted that the puzzle is already solved or that
     * there is no solution.
     */
    public void getHint() {
        SolverData solution = Solver.searchBFS(currentConfig);
        if (solution.path().isEmpty()) alertObservers("No solution!");
        else if (solution.path().get().size() == 1) alertObservers("Already solved!");
        else {
            ArrayList<Configuration> path = new ArrayList<>(solution.path().get());
            currentConfig = (TiltConfig) path.get(1);
            alertObservers("Next step!");
        }
    }   

    /**
     * Loads a tilt board from the given file. If the file is invalid, the user is alerted.
     * @param tiltFile the file to load
     */
    public void loadBoard(File tiltFile) {
        try {
            this.currentConfig = new TiltConfig(tiltFile);
            this.tiltFile = tiltFile;
            alertObservers("Loaded: " + tiltFile.getName());
        } catch (Exception e) {
            alertObservers("Failed to load: " + tiltFile.getName());
            if (currentConfig == null) {
                System.out.println("Invalid board file, please load a valid file!");
                System.exit(0);
            }
        }
    }

    /**
     * Moves the tilt board in the given direction. If the move is invalid, the user is alerted.
     * A move is invalid if a blue slider will fall through the hole.
     * @param direction the direction to move
     */
    public void tilt(Direction direction) {
        TiltConfig next = new TiltConfig(currentConfig, direction);
        if (!next.isValid())
            alertObservers("Illegal move. A blue slider will fall through the hole!");
        else {
            currentConfig = next;
            if (currentConfig.isGoal()) alertObservers("Congratulations!");
            else alertObservers("");
        };
    }

    /** Quits the game. */
    public void quit() {
        System.exit(0);
    }

    /** Resets the puzzle to the original tilt board configuration. */
    public void reset() {
        try {
            currentConfig = new TiltConfig(tiltFile);
            alertObservers("Puzzle reset!");
        } catch (Exception e) {
            alertObservers("Must load valid puzzle first!");
        }
    }

    /** Returns the toString of the current tilt board configuation. */
    public String getStringBoard() {
        return currentConfig.toString();
    }

    /**
     * Returns the {@link Piece} at the given row and column in the current tilit board configuration.
     * @param row the row of the piece
     * @param col the column of the piece
     * @return the piece at the given row and column
     */
    public Piece getPieceAt(int row, int col) {
        return currentConfig.getPieceAt(row, col);
    }

    /**
     * The view calls this to add itself as an observer.
     * @param observer the view
     */
    public void addObserver(Observer<TiltModel, String> observer) {
        this.observers.add(observer);
    }

    /** The model's state has changed, so inform the view via the update method. */
    private void alertObservers(String data) {
        for (var observer : observers) {
            observer.update(this, data);
        }
    }
}
