package puzzles.common.solver;

import java.util.Collection;

/**
 * Interface for a configuration in a search problem.
 * This interface defines the methods that any configuration class must implement.
 * 
 * <em>Note: Classes that impliment this configuration base should also override the {@code equals()} and {@code hashCode()} methods.
 * 
 * @author Maddox Van Sickel
 */
public interface Configuration {
    /**
     * Returns whether this configuration is a goal configuration.
     * @return true if this configuration is a goal, false otherwise.
     */
    public boolean isGoal();

    /**
     * Returns whether this configuration is valid.
     * @return true if this configuration is valid, false otherwise.
     */
    public boolean isValid();

    /**
     * Returns a collection of successor configurations stemming from this configuration.
     * @return a collection of successor configurations.
     */
    public Collection<Configuration> getSuccessors();
}
