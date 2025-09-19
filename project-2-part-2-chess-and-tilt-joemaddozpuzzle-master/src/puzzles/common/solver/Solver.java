package puzzles.common.solver;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;
import java.util.HashMap;

/**
 * The {@code Solver} class provides methods to solve a search problem using different search algorithms.
 * It implements both Depth-First Search (DFS) and Breadth-First Search (BFS) strategies.
 *
 * <p>
 * The DFS method explores the search space by recursively visiting neighboring configurations, while the BFS
 * method uses a queue to explore all configurations at the present depth before moving on to the next level.
 * </p>
 *
 * @see Configuration
 * @author Maddox Van Sickel
 */
public class Solver {
    /**
     * An immutable record that holds the results of the BFS search.
     *
     * @param path          the collection of configurations representing the solution path.
     *                      The path starts with the initial configuration and ends with the solution.
     * @param totalConfigs  the total number of configurations examined during the search,
     *                      including duplicate configurations.
     * @param uniqueConfigs the number of unique configurations encountered.
     */
    public record SolverData(Optional<Collection<Configuration>> path, int totalConfigs, int uniqueConfigs) {}

    /**
     * Executes a Depth-First Search (DFS) starting from the given configuration.
     *
     * <p>
     * The method searches for a solution by recursively exploring neighboring configurations until a configuration
     * satisfies the {@link Configuration#isGoal()} condition. It uses a depth-first approach to traverse the
     * search space.
     * </p>
     *
     * @param startConfig the starting configuration for the search.
     * @return an {@code Optional} containing the solution configuration if found,
     *         or an empty {@code Optional} if no solution exists.
     */
    public static Optional<Configuration> searchDFS(Configuration startConfig) {
        if (startConfig.isGoal()) return Optional.of(startConfig);
        else {
            Collection<Configuration> successors = startConfig.getSuccessors();
            for (Configuration child : successors) {
                if (child.isValid()) {
                    Optional<Configuration> solution = searchDFS(child);
                    if (solution.isPresent()) return solution;
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Executes a Breadth-First Search (BFS) starting from the given configuration.
     *
     * <p>
     * The method searches for a solution by expanding neighboring configurations until a configuration
     * satisfies the {@link Configuration#isGoal()} condition. It maintains a queue for BFS and a predecessor
     * map to reconstruct the solution path.
     * </p>
     *
     * @param startConfig the starting configuration of the puzzle.
     * @return an {@code Optional} containing {@code SolverData} if a solution is found,
     *         or an empty {@code Optional} if no solution exists.
     */
    public static SolverData searchBFS(Configuration startConfig) {
        LinkedList<Configuration> queue = new LinkedList<>();
        HashMap<Configuration, Configuration> predecessorMap = new HashMap<>();

        int totalConfigs = 1;
        int uniqueConfigs = 1;

        predecessorMap.put(startConfig, null);
        queue.add(startConfig);

        while(!queue.isEmpty() && !queue.getFirst().isGoal()) {
            Configuration thisConfig = queue.removeFirst();
            for (Configuration neighbor : thisConfig.getSuccessors()) {
                totalConfigs += 1;
                if (!predecessorMap.containsKey(neighbor)) {
                    uniqueConfigs += 1;
                    predecessorMap.put(neighbor, thisConfig);
                    queue.add(neighbor);
                }
            }
        }

        if (queue.isEmpty()) return new SolverData(Optional.empty(), totalConfigs, uniqueConfigs);
        else {
            LinkedList<Configuration> path = new LinkedList<>();
            Configuration config = queue.getFirst();
            path.add(0, config);
            Configuration predecessorConfig = predecessorMap.get(config);
            while (predecessorConfig != null) {
                path.add(0, predecessorConfig);
                predecessorConfig = predecessorMap.get(predecessorConfig);
            }
            return new SolverData(Optional.of(path), totalConfigs, uniqueConfigs);
        }
    }
}
