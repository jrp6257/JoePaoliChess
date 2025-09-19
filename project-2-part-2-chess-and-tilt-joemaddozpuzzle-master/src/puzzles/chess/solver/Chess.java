package puzzles.chess.solver;

import puzzles.chess.model.ChessConfig;
import puzzles.clock.ClockConfig;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;

/**
 * Starts the automatic ches solver
 */
public class Chess {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: java Chess filename");
        } else {
            File file = new File(args[0]);
            ChessConfig start = new ChessConfig(file);
            Solver.SolverData solution = Solver.searchBFS(start);
            System.out.println("Total configs: " + solution.totalConfigs());
            System.out.println("Unique configs: " + solution.uniqueConfigs());
            if (!solution.path().isPresent()) System.out.println("No solution!");
            else {
                ArrayList<Configuration> path = new ArrayList<>(solution.path().get());
                for (int i = 0; i < path.size(); i++) {
                    System.out.println("Step " + i + ":");
                    System.out.println(path.get(i));
                }
            }
            if (solution.path().equals(Optional.empty())) {
                System.out.println("No solution");
            }
        }
    }
}
