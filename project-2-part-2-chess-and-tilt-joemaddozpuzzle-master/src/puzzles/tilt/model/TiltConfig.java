package puzzles.tilt.model;

import puzzles.common.solver.Configuration;
import java.io.FileNotFoundException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import puzzles.common.Coordinates;
import java.util.stream.Stream;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;
import java.io.File;

/**
 * The {@code TiltConfig} class represents a configuration of the tilt puzzle game.
 * It contains the current state of the board and provides methods to manipulate and check the configuration.
 * The class implements the {@code Configuration} interface, which defines methods for checking goal state,
 * generating successors, and validating the configuration.
 *
 * @see Configuration
 * @see Direction
 * @see Piece
 * 
 * @author Maddox Van Sickel
 */
public class TiltConfig implements Configuration {
    /** The dimensions of the tilt board. */
    public static int dimensions;

    /** The number of blue sliders on the board. */
    private static int totalBlueSliders;
    /** The board configuration represented as a 2D array of {@code Piece} objects. */
    private Piece[][] board;

    /**
     * Constructor for the {@code TiltConfig} class which loads a tilt board from a file.
     * @param file the file containing the tilt board configuration
     * @throws FileNotFoundException if the file is not found
     */
    public TiltConfig(File file) throws FileNotFoundException {
        Scanner in = new Scanner(file);
        TiltConfig.dimensions = Integer.parseInt(in.nextLine());
        board = new Piece[TiltConfig.dimensions][TiltConfig.dimensions];
        for (int i = 0; i < board.length; i++)
            board[i] = Stream.of(in.nextLine().strip().split("\\s+"))
                .map(s -> Piece.toPiece(s.charAt(0)))
                .toArray(Piece[]::new);
        totalBlueSliders = countBlueSliders();
        in.close();
    }

    /**
     * Constructor for the {@code TiltConfig} class which creates a new configuration based on an existing one.
     * The new configuration is created by tilting the board in a specified direction.
     * @param oldBoard the existing tilt board configuration
     * @param direction the direction to tilt the board
     */
    public TiltConfig(TiltConfig oldBoard, Direction direction) {
        // Sorts the ArrayList of coordinates based on the direction
        BiConsumer<Direction, ArrayList<Coordinates>> sort = (dir, coords) -> {
                switch (dir) {
                    case NORTH: 
                        Collections.sort(coords, (c1, c2) -> Integer.compare(c1.row(), c2.row()));
                        break;
                    case EAST: 
                        Collections.sort(coords, (c1, c2) -> Integer.compare(c2.col(), c1.col()));
                        break;
                    case SOUTH: 
                        Collections.sort(coords, (c1, c2) -> Integer.compare(c2.row(), c1.row()));
                        break;
                    case WEST: 
                        Collections.sort(coords, (c1, c2) -> Integer.compare(c1.col(), c2.col()));
                        break;
                    default:
                }
        };

        // Moves the coordinates in the specified direction by one cell
        BiFunction<Direction, Coordinates, Coordinates> moveDirection = (dir, coord) -> {
            int r = coord.row();
            int c = coord.col();

            switch (dir) {
                case NORTH: return (r - 1 >= 0 && r - 1 < oldBoard.board.length) ? new Coordinates(r - 1, c) : coord;
                case EAST: return (c + 1 >= 0 && c + 1 < oldBoard.board.length) ? new Coordinates(r, c + 1) : coord;
                case SOUTH: return (r + 1 >= 0 && r + 1 < oldBoard.board.length) ? new Coordinates(r + 1, c) : coord;
                case WEST: return (c - 1 >= 0 && c - 1 < oldBoard.board.length) ? new Coordinates(r, c - 1) : coord;
                default: return coord;
            }
        };

        ArrayList<Coordinates> visitationOrder = new ArrayList<>();
        for (int r = 0; r < oldBoard.board.length; r++)
            for (int c = 0; c < oldBoard.board.length; c++)
                if (oldBoard.board[r][c].isSlider())
                    visitationOrder.add(new Coordinates(r, c));
        sort.accept(direction, visitationOrder);

        Piece[][] newBoard = new Piece[oldBoard.board.length][oldBoard.board.length];
        for (int r = 0; r < newBoard.length; r++)
            for (int c = 0; c < newBoard.length; c++) {
                if (oldBoard.board[r][c].isSlider())
                    newBoard[r][c] = Piece.EMPTY;
                else newBoard[r][c] = oldBoard.board[r][c];
            }

        for (Coordinates coord : visitationOrder) {
            Piece slider = oldBoard.board[coord.row()][coord.col()];
            Coordinates currentCoord = coord;
            Coordinates nextCoord = moveDirection.apply(direction, currentCoord);

            while (newBoard[nextCoord.row()][nextCoord.col()] == Piece.EMPTY
                && !currentCoord.equals(nextCoord)) {
                    currentCoord = nextCoord;
                    nextCoord = moveDirection.apply(direction, currentCoord);
                }
                
            if (newBoard[nextCoord.row()][nextCoord.col()] == Piece.HOLE)
                continue;
            newBoard[currentCoord.row()][currentCoord.col()] = slider;
        }

        this.board = newBoard;
    }

    /**
     * Returns the piece at the specified row and column in the tilt board configuration.
     * @param row the row of the piece
     * @param col the column of the piece
     * @return the piece at the specified row and column
     */
    public Piece getPieceAt(int row, int col) {
        return board[row][col];
    }

    /**
     * Returns the number of blue sliders on the board.
     * @return the number of blue sliders on the board
     */
    public int countBlueSliders() {
        int total = 0;
        for (int r = 0; r < TiltConfig.dimensions; r++)
            for (int c = 0; c < TiltConfig.dimensions; c++)
                if (board[r][c] == Piece.SLIDER_BLUE) total++;
        return total;
    }

    @Override
    public boolean isGoal() {
        for (int r = 0; r < board.length; r++)
            for (int c = 0; c < board.length; c++)
                if (board[r][c] == Piece.SLIDER_GREEN)
                    return false;
        if (totalBlueSliders != countBlueSliders())
            return false;
        return true;
    }

    @Override
    public Collection<Configuration> getSuccessors() {
        Collection<Configuration> successors = new ArrayList<>();
        TiltConfig northConfig = new TiltConfig(this, Direction.NORTH);
        TiltConfig eastConfig = new TiltConfig(this, Direction.EAST);
        TiltConfig southConfig = new TiltConfig(this, Direction.SOUTH);
        TiltConfig westConfig = new TiltConfig(this, Direction.WEST);

        if (totalBlueSliders == northConfig.countBlueSliders())
            successors.add(northConfig);
        if (totalBlueSliders == eastConfig.countBlueSliders())
            successors.add(eastConfig);
        if (totalBlueSliders == southConfig.countBlueSliders())
            successors.add(southConfig);
        if (totalBlueSliders == westConfig.countBlueSliders())
            successors.add(westConfig);

        return successors;
    }

    @Override
    public boolean isValid() {
        return totalBlueSliders == countBlueSliders();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TiltConfig otherBoard)
            return Arrays.deepEquals(board, otherBoard.board);
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < board.length; r++) {
            sb.append("|");
            for (int c = 0; c < board.length; c++) {
                sb.append(Piece.toChar(board[r][c]).get())
                    .append("|");
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }
}
