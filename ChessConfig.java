package puzzles.chess.model;

import puzzles.common.solver.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.ObjectInputFilter;
import java.util.*;

/**
 * A single configuration for a chess board
 * @author Joe Paoli
 */
public class ChessConfig implements Configuration {
    private int length;
    private int height;
    private char[][] board;

    /**
     * Loads initial board from file
     * @param file- the file to load from
     * @throws Exception- if file doesn't exist
     */
    public ChessConfig(File file) throws Exception {
        Scanner in = new Scanner(file);
        if (!in.hasNextLine()) {
            throw new FileNotFoundException("Empty file");
        }
        String[] dimensions = in.nextLine().trim().split("\\s+");
        if (dimensions.length != 2) {
            throw new IllegalArgumentException("First line should only contain 2 numbers");
        }
        height = Integer.parseInt(dimensions[0]);
        length = Integer.parseInt(dimensions[1]);
        board = new char[height][length];
        for (int i=0; i < height; i++) {
            String line = in.nextLine();
            int charCount = 0;
            char[] chars = line.toCharArray();
            char[] realChars = new char[length];
            for (char c : chars) {
                if (c != ' ') {
                    realChars[charCount] = c;
                    charCount++;
                }
            }
            for (int j=0; j < length ; j++) {
                board[i][j] = realChars[j];
            }
        }
    }

    /**
     * Loads a new chessconfig based off of the previous one
     * @param previous- previous configuration
     */
    private ChessConfig(ChessConfig previous) {
        this.height = previous.height;
        this.length = previous.length;
        this.board = new char[height][length];
        for (int i = 0; i < height; i++) {
            this.board[i] = Arrays.copyOf(previous.board[i], length);
        }
    }

    /**
     * Helper function used in the movement for each piece
     * @param successors- the list of successors
     * @param seen- set of successors that are already generated
     * @param fromRow- row coming from
     * @param fromCol- column coming from
     * @param toRow- row going to
     * @param toCol- column going to
     */
    public void tryMove(List<Configuration> successors, Set<String> seen, int fromRow, int fromCol, int toRow, int toCol) {
        if (toRow < 0 || toRow >= height || toCol < 0 || toCol >= length) return;
        if (board[toRow][toCol] == '.') return;
        ChessConfig newConfig = new ChessConfig(this);
        newConfig.board[toRow][toCol] = board[fromRow][fromCol];
        newConfig.board[fromRow][fromCol] = '.';
        String boardString = Arrays.deepToString(newConfig.board);
        if (seen.add(boardString)) {
            successors.add(newConfig);
        }
    }


    /**
     * Checks if only one piece is left on the board, returns boolean
     * @return- boolean of if one piece is left
     */
    @Override
    public boolean isGoal() {
        int pieceCount = 0;
        for (int i=0; i < height; ++i) {
            for (int j=0; j < length; ++j) {
                if (board[i][j] != '.') {
                    pieceCount += 1;
                }
            }
        }
        if (pieceCount == 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the desired location for a piece has a piece on it
     * @return true if there is a piece, false if there isn't
     */
    @Override
    public boolean isValid() {
        for (int i=0; i < height; ++i) {
            for (int j = 0; j < length; ++j) {
                if (board[i][j] != '.') {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Generates possible moves for each possible piece
     * @param successors- list of successors
     * @param seen- already generated successors
     * @param row- row coming from
     * @param col- column coming from
     * @param piece- the piece that's moving
     */
    public void movePerPiece(List<Configuration> successors, Set<String> seen, int row, int col, Piece piece) {
        int[][] moves;
        switch (piece) {
            case PAWN -> {
                tryMove(successors, seen, row, col, row - 1, col - 1);
                tryMove(successors, seen, row, col, row - 1, col + 1);
            }
            case KNIGHT -> {
                moves = new int[][] {
                        {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
                        {1, -2}, {1, 2}, {2, -1}, {2, 1}
                };
                for (int[] d : moves) {
                    tryMove(successors, seen, row, col, row + d[0], col + d[1]);
                }
            }
            case KING -> {
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        if (dr != 0 || dc != 0)
                            tryMove(successors, seen, row, col, row + dr, col + dc);
                    }
                }
            }
            case ROOK -> {
                int[][] directions = {
                        {-1, 0},
                        {1, 0},
                        {0, -1},
                        {0, 1}
                };
                for (int[] dir : directions) {
                    int newRow = row;
                    int newCol = col;
                    while (true) {
                        newRow += dir[0];
                        newCol += dir[1];
                        if (newRow < 0 || newRow >= height || newCol < 0 || newCol >= length) {
                            break;
                        }
                        if (board[newRow][newCol] == '.') {
                            continue;
                        }
                        tryMove(successors, seen, row, col, newRow, newCol);
                        break;
                    }
                }
            }
            case BISHOP -> {
                int[][] directions = { {-1, -1}, {-1, 1}, {1, -1}, {1, 1} };
                for (int[] dir : directions) {
                    int r = row + dir[0], c = col + dir[1];
                    while (r >= 0 && r < height && c >= 0 && c < length) {
                        if (board[r][c] != '.') {
                            tryMove(successors, seen, row, col, r, c);
                            break;
                        }
                        r += dir[0];
                        c += dir[1];
                    }
                }
            }
            case QUEEN -> {
                movePerPiece(successors, seen, row, col, Piece.ROOK);
                movePerPiece(successors, seen, row, col, Piece.BISHOP);
            }
        }
    }

    /**
     * Returns length of board
     * @return length
     */
    public int getLength() {
        return this.length;
    }

    /**
     * Returns height of board
     * @return height
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Gets the character at a specific cell
     * @param row- the row
     * @param col- the column
     * @return character at cell
     */
    public char getPieceChar(int row, int col) {
        return board[row][col];
    }

    /**
     * Gets list of successors for a configuration
     * @return list of successors
     */
    @Override
    public Collection<Configuration> getSuccessors() {
        ArrayList<Configuration> successors = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < length; j++) {
                char pieceChar = board[i][j];
                if (pieceChar == '.') continue;
                Piece piece = Piece.toPiece(pieceChar);
                movePerPiece(successors, seen, i, j, piece);
            }
        }

        return successors;
    }

    /**
     * Returns board
     * @return the board
     */
    public char[][] getBoard() {
        return this.board;
    }

    /**
     * Checks if 2 boards are equal
     * @param other- other object
     * @return if boards are equal
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof ChessConfig) {
            return Arrays.deepEquals(this.board, ((ChessConfig) other).board);
        }
        return false;
    }

    /**
     * Generates hash code for board
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    /**
     * String representation of board
     * @return- string of board
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                sb.append(board[i][j]).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
