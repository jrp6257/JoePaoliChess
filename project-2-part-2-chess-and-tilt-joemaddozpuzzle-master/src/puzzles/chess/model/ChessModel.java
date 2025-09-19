package puzzles.chess.model;

import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import puzzles.tilt.model.TiltConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A model for the chess GUI and PTUI
 */
public class ChessModel {
    /** the collection of observers of this model */
    private final List<Observer<ChessModel, String>> observers = new LinkedList<>();
    private File currentFile;
    private int[] currentCell;
    /** the current configuration */
    public ChessConfig currentConfig;

    /**
     * Loads chess file
     * @param chessFile- the chess file
     */
    public ChessModel(File chessFile) {
        loadFile(chessFile);
    }

    /**
     * Gives hint based on next step in path
     */
    public void getHint() {
        Solver.SolverData solution = Solver.searchBFS(currentConfig);
        if (solution.path().isPresent() && solution.path().get().size() > 1) {
            ArrayList<Configuration> path = new ArrayList<>(solution.path().get());
            currentConfig = (ChessConfig) path.get(1);
            if (currentConfig.isGoal()) {
                alertObservers("Solved!");
            } else {
                alertObservers("Next step in solution");
            }
        } else {
            if (currentConfig.isGoal()) {
                alertObservers("Solved!");
            } else {
                alertObservers("No solution");
            }
        }
    }

    /**
     * Loads a board based on a chess file
     * @param chessFile- the chess file
     */
    public void loadFile(File chessFile) {
        try {
            ChessConfig newConfig = new ChessConfig(chessFile);
            currentFile = chessFile;
            currentConfig = newConfig;
            currentCell = null;
            alertObservers("Loaded puzzle file " + chessFile);
        } catch (FileNotFoundException e) {
            alertObservers("Failed to read file " + chessFile);
        } catch (Exception e) {
            alertObservers("Error opening file: " + e.getMessage());
        }
    }

    /**
     * Moves/selects a piece from a certain row and column
     * @param row- selected row
     * @param col- selected column
     */
    public void movePiece(int row, int col) {
        if (currentCell == null) {
            if (currentConfig.getPieceChar(row, col) != '.') {
                currentCell = new int[]{row, col};
                alertObservers("Selected piece at (" + row + ", " + col + ")");
            } else {
                alertObservers("No piece at " + "(" + row + "," + col + ")");
            }
        } else {
            int fromRow = currentCell[0];
            int fromCol = currentCell[1];
            char pieceChar = currentConfig.getPieceChar(fromRow, fromCol);
            List<Configuration> successors = new ArrayList<>(currentConfig.getSuccessors());
            boolean moved = false;
            for (Configuration successor : successors) {
                ChessConfig config = (ChessConfig) successor;
                if (config.getPieceChar(row, col) == pieceChar && config.getPieceChar(fromRow, fromCol) == '.') {
                    currentConfig = config;
                    moved = true;
                    break;
                }
            }
            if (moved) {
                alertObservers("Captured from " + "(" + fromRow +"," + fromCol + ")" + " to " + "(" + row + ", " + col + ")");
                if (currentConfig.isGoal()) {
                    alertObservers("Solved!");
                }
            } else {
                alertObservers("Invalid move.");
            }
            currentCell = null;
        }
    }

    /**
     * Exits the GUI/PTUI
     */
    public void quit() {
        System.exit(0);
    }

    /**
     * Resets board back to original state
     */
    public void reset() {
        try {
            currentConfig = new ChessConfig(currentFile);
            alertObservers("Board reset");
        } catch (Exception e) {
            alertObservers("Load a valid puzzle before resetting!");
        }
    }

    /**
     * Generates the string representation of a board with row and column numbers
     * for easier navigation
     * @return- representation of board
     */
    public String ptuiString() {
        if (currentConfig == null) {
            return "No board to load";
        }else {
            StringBuilder sb = new StringBuilder();
            sb.append("  ");
            for (int i=0; i < currentConfig.getHeight(); ++i) {
                sb.append(i + " ");
            }
            sb.append('\n');
            sb.append("  ");
            for (int i=0; i < currentConfig.getHeight()*2; ++i) {
                sb.append('-');
            }
            sb.append('\n');
            for (int i=0; i < currentConfig.getHeight(); ++i) {
                sb.append(i + "| ");
                for(int j=0; j < currentConfig.getLength(); ++j) {
                    sb.append(currentConfig.getBoard()[i][j] + " ");
                }
                sb.append('\n');
            }
            return sb.toString();
        }
    }

    /**
     * Returns piece at a row and column
     * @param r- row
     * @param c- column
     * @return- the piece
     */
    public Piece getPiece(int r, int c) {
        return Piece.toPiece(currentConfig.getPieceChar(r,c));
    }

    /**
     * The view calls this to add itself as an observer.
     *
     * @param observer the view
     */
    public void addObserver(Observer<ChessModel, String> observer) {
        this.observers.add(observer);
    }

    /**
     * The model's state has changed (the counter), so inform the view via
     * the update method
     */
    private void alertObservers(String data) {
        for (var observer : observers) {
            observer.update(this, data);
        }
    }
}
