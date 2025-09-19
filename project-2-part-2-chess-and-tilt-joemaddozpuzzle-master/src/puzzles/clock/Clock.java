package puzzles.clock;

import puzzles.common.solver.Solver.SolverData;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import java.util.ArrayList;

/**
 * Main class for the clock puzzle.
 *
 * @author Maddox Van Sickel
 */
public class Clock {
    /**
     * Run an instance of the clock puzzle.
     *
     * @param args [0]: the number of hours in the clock;
     *             [1]: the starting hour;
     *             [2]: the finish hour.
     */
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println(("Usage: java Clock hours start finish"));
        } else {
            try {
                int hours = Integer.parseInt(args[0]);
                int start = Integer.parseInt(args[1]);
                int end = Integer.parseInt(args[2]);

                if (hours < 0 || start < 0 || end < 0)
                    throw new IllegalArgumentException("Hours, start, and end must be non-negative.");

                StringBuilder sb = new StringBuilder()
                    .append("Hours: " + hours).append(System.lineSeparator())
                    .append("Start: " + start).append(System.lineSeparator())
                    .append("End: " + end).append(System.lineSeparator());
                System.out.println(sb);

                ClockConfig.hours = hours;
                ClockConfig.end = end;

                ClockConfig startConfig = new ClockConfig(start);

                SolverData solution = Solver.searchBFS(startConfig);

                System.out.println("Total configs: " + solution.totalConfigs());
                System.out.println("Unique configs: " + solution.uniqueConfigs());

                if (!solution.path().isPresent()) System.out.println("No solution found.");
                else {
                    ArrayList<Configuration> path = new ArrayList<>(solution.path().get());
                    for (int i = 0; i < path.size(); i++)
                        System.out.println("Step " + i + ": " + path.get(i));
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
