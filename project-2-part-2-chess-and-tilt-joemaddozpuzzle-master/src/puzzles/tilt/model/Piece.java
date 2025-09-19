package puzzles.tilt.model;

import java.util.Optional;

/**
 * The {@code Piece} enum represents the different types of pieces that can be present on the tilt board.
 * Each piece type is associated with a specific character representation.
 * The enum provides methods to check if a piece is a slider, convert a piece to its character representation,
 * and convert a character to its corresponding piece type.
 *
 * @see TiltConfig
 * @see TiltModel
 * 
 * @author Maddox Van Sickel
 */
public enum Piece {
    SLIDER_GREEN,
    SLIDER_BLUE,
    BLOCKER,
    HOLE,
    EMPTY,
    NONE;

    /**
     * Checks if the piece is a slider (either green or blue).
     * @return true if the piece is a slider, false otherwise.
     */
    public boolean isSlider() {
        return this == SLIDER_GREEN || this == SLIDER_BLUE;
    }

    /**
     * Converts the piece to its character representation.
     * @param piece the piece to convert
     * @return an Optional containing the character representation of the piece,
     * or an empty Optional if the piece is NONE.
     */
    public static Optional<Character> toChar(Piece piece) {
        switch (piece) {
            case SLIDER_GREEN: return Optional.of('G');
            case SLIDER_BLUE: return Optional.of('B');
            case BLOCKER: return Optional.of('*');
            case HOLE: return Optional.of('O');
            case EMPTY: return Optional.of('.');
            default: return Optional.empty();
        }
    }

    /**
     * Converts a character to its corresponding piece type.
     * @param chr the character to convert
     * @return the corresponding piece type, or {@link Piece#NONE} if the character is not recognized.
     */
    public static Piece toPiece(Character chr) {
        switch (chr) {
            case 'G': return SLIDER_GREEN;
            case 'B': return SLIDER_BLUE;
            case '*': return BLOCKER;
            case 'O': return HOLE;
            case '.': return EMPTY;
            default: return NONE;
        }
    }
}
