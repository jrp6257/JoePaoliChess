package puzzles.chess.model;

/*
 * Contains functions for making character into piece and vice versa
 * @author Joe Paoli
 */
public enum Piece {
    KING,
    QUEEN,
    KNIGHT,
    PAWN,
    ROOK,
    BISHOP,
    EMPTY;

    public static char toChar(Piece piece) {
        switch (piece) {
            case KING: return 'K';
            case QUEEN: return 'Q';
            case KNIGHT: return 'N';
            case ROOK: return 'R';
            case BISHOP: return 'B';
            case PAWN: return 'P';
            case EMPTY: return '.';
            default: return 'E';
        }
    }

    public static Piece toPiece(char c) {
        switch (c) {
            case 'K': return KING;
            case 'Q': return QUEEN;
            case 'N': return KNIGHT;
            case 'R': return ROOK;
            case 'B': return BISHOP;
            case 'P': return PAWN;
            case '.': return EMPTY;
            default: return null;
        }
    }
}
