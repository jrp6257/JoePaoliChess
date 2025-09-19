package puzzles.clock;

import puzzles.common.solver.Configuration;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Represents a configuration of a clock puzzle.
 * <p>
 * The clock configuration is defined by the current hour position.
 * The puzzle is solved when the current hour equals the designated end hour.
 * </p>
 * @author Maddox Van Sickel
 */
public class ClockConfig implements Configuration {
    /** The total number of hours on the clock. */
    public static int hours;
    /** The target hour that represents the solution. */
    public static int end;
    /** The current hour position in this configuration. */
    private int current;

    /**
     * Constructs a ClockConfig with the specified current hour.
     *
     * @param current the current hour position.
     */
    public ClockConfig(int current) {
        this.current = current;
    }

    @Override
    public boolean isGoal() {
        return current == end;
    }

    @Override
    public Collection<Configuration> getSuccessors() {
        LinkedList<Configuration> neighbors = new LinkedList<>();

        int backwardHour = current - 1;
        int forwardHour = current + 1;

        if (backwardHour < 1) backwardHour = hours;
        if (forwardHour > hours) forwardHour = 1;

        ClockConfig backwardConfig = new ClockConfig(backwardHour);
        ClockConfig forwardConfig = new ClockConfig(forwardHour);

        neighbors.add(backwardConfig);
        neighbors.add(forwardConfig);

        return neighbors;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ClockConfig otherConfig)
            return current == otherConfig.current;
        return false;
    }

    @Override
    public int hashCode() {
        return current;
    }

    @Override
    public String toString() {
        return new StringBuilder().append(current).toString();
    }
}
