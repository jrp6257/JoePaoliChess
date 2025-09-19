package puzzles.tilt.solver;

import puzzles.common.solver.Solver.SolverData;
import puzzles.common.solver.Configuration;
import puzzles.tilt.model.TiltConfig;
import java.io.FileNotFoundException;
import puzzles.common.solver.Solver;
import java.util.ArrayList;
import java.io.File;

/**
 * Tilt.java
 * This class is the main entry point for the Tilt puzzle solver.
 * It reads a configuration file, initializes the puzzle, and finds a solution using BFS.
 * It also prints the total number of configurations and the unique configurations encountered.
 * 
 * @author Maddox Van Sickel
 */
public class Tilt {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Tilt filename");
        } else {
            File tiltFile = new File(args[0]);
            try {
                TiltConfig startConfig = new TiltConfig(tiltFile);
                System.out.println("File: " + tiltFile.getAbsolutePath());
                System.out.println(startConfig);
                SolverData solution = Solver.searchBFS(startConfig);
                System.out.println("Total configs: " + solution.totalConfigs());
                System.out.println("Unique configs: " + solution.uniqueConfigs());
                if (!solution.path().isPresent()) System.out.println("No solution!");
                else if (solution.path().get().size() == 1) System.out.println("Already solved!");
                else {
                    ArrayList<Configuration> path = new ArrayList<>(solution.path().get());
                    for (int i = 0; i < path.size(); i++) {
                        System.out.println("Step " + i + ":");
                        System.out.println(path.get(i));
                    }
                }
            } catch (FileNotFoundException e) {
                System.err.println("File not found: " + tiltFile.getAbsolutePath());
                e.printStackTrace();
            }
        }
    }
}
