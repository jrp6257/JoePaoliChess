package puzzles.water;

import puzzles.common.solver.Solver.SolverData;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Main class for the water buckets puzzle.
 *
 * @author Maddox Van Sickel
 */
public class Water {

    /**
     * Run an instance of the water buckets puzzle.
     *
     * @param args [0]: desired amount of water to be collected;
     *             [1..N]: the capacities of the N available buckets.
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println(
                    ("Usage: java Water amount bucket1 bucket2 ...")
            );
        } else {
            try {
                int endAmount = Integer.parseInt(args[0]);
                int[] bucketCapacities = Arrays.copyOfRange(
                        Stream.of(args).mapToInt(Integer::parseInt).toArray(), 1, args.length);
                WaterConfig.endAmount = endAmount;
                WaterConfig.numBuckets = bucketCapacities.length;
                WaterConfig.bucketCapacities = bucketCapacities;

                StringBuilder sb = new StringBuilder();
                sb.append("Amount: ").append(WaterConfig.endAmount)
                    .append(", Buckets: [");
                for (int i = 0; i < bucketCapacities.length; i++) {
                    if (i < bucketCapacities.length - 1)
                        sb.append(bucketCapacities[i]).append(", ");
                    else sb.append(bucketCapacities[i]).append("]");
                }
                System.out.println(sb);

                int[] buckets = new int[WaterConfig.numBuckets];
                WaterConfig start = new WaterConfig(buckets);

                SolverData solution = Solver.searchBFS(start);
                
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
