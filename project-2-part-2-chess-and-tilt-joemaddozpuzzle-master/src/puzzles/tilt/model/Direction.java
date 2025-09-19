package puzzles.tilt.model;

/**
 * The {@code Direction} enum represents the four possible directions in which the tilt board can be tilted.
 * It provides a method to convert a string representation of a direction to its corresponding enum value.
 * The enum also includes a NONE value to represent no direction.
 *
 * @see TiltConfig
 * @see Piece
 * @see TiltModel
 * 
 * @author Maddox Van Sickel
 */
public enum Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST,
    NONE;

    /**
     * Converts a string representation of a direction to its corresponding enum value.
     * The method is case-insensitive and accepts both full names and abbreviations of the directions.
     * <h>
     *      <h4>N / NORTH</h4>
     *      <h4>E / EAST</h4>
     *      <h4>S / SOUTH</h4>
     *      <h4>W / WEST</h4>
     * </h>
     * @param string the string representation of the direction
     * @return the corresponding {@code Direction} enum value, or {@link Direction#NONE} if the string is not recognized.
     */
    public static Direction fromString(String string) {
        switch (string.toUpperCase()) {
            case "N":
            case "NORTH": return NORTH;
            case "E":
            case "EAST": return EAST;
            case "S":
            case "SOUTH": return SOUTH;
            case "W":
            case "WEST": return WEST;
            default: return NONE;
        }
    }
}
