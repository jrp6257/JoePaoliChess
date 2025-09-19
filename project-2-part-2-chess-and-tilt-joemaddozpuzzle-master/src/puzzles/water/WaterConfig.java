package puzzles.water;

import puzzles.common.solver.Configuration;
import java.util.LinkedList;
import java.util.Collection;
import java.util.Arrays;

/**
 * Represents a configuration of a water jug puzzle.
 * <p>
 * The water configuration is defined by the current amount of water in each bucket.
 * The puzzle is solved when any bucket reaches the designated end amount of water.
 * </p>
 * @author Maddox Van Sickel
 */
public class WaterConfig implements Configuration {
    /** The end amount of water in a bucket trying to be reached. */
    public static int endAmount;
    /** The number of total buckets. */
    public static int numBuckets;
    /** The capacity of water each bucket can hold. */
    public static int[] bucketCapacities;
    /** The current amount of water in each bucket. */
    private int[] buckets;

    /** Constructs a WaterConfig with the specified amounts of water in each bucket. 
     *
     * @param buckets the current amounts of water in each bucket. 
     */
    public WaterConfig(int[] buckets) {
        this.buckets = buckets;
    }

    @Override
    public boolean isGoal() {
        for (int bucket : buckets)
            if (bucket == WaterConfig.endAmount)
                return true;
        return false;
    }

    @Override
    public Collection<Configuration> getSuccessors() {
        LinkedList<Configuration> neighbors = new LinkedList<>();
        
        for (int i = 0; i < WaterConfig.numBuckets; i++) {
            // fill
            int[] newBuckets = buckets.clone();
            newBuckets[i] = bucketCapacities[i];
            Configuration fillConfig = new WaterConfig(newBuckets);
            neighbors.add(fillConfig);

            // dump
            newBuckets = buckets.clone();
            newBuckets[i] = 0;
            Configuration dumpConfig = new WaterConfig(newBuckets);
            neighbors.add(dumpConfig);

            // pour
            for (int j = 0; j < WaterConfig.numBuckets; j++) {
                if (i == j) continue;

                newBuckets = buckets.clone();
                
                int pourAmount = Math.min(newBuckets[j], bucketCapacities[i] - buckets[i]);
                newBuckets[i] += pourAmount;
                newBuckets[j] -= pourAmount;

                Configuration pourConfig = new WaterConfig(newBuckets);
                neighbors.add(pourConfig);
            }
        }

        return neighbors;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof WaterConfig otherConfig)
            return Arrays.equals(buckets, otherConfig.buckets);
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(buckets);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < WaterConfig.numBuckets; i++) {
            if (i < WaterConfig.numBuckets - 1) 
                sb.append(buckets[i])
                    .append(", ");
            else sb.append(buckets[i])
                .append("]");
        }
        return sb.toString();
    }
}