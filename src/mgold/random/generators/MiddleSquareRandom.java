package mgold.random.generators;

/**
 * This random number generator is an implementation of the Middle Square Random
 * method first suggested by John von Neumann. It serves as an example of a
 * low-quality (by todays standards) random number generator.
 *
 * @author Michael Goldstein
 * @version 1.0
 */
public class MiddleSquareRandom {

    private static final double MULTIPLIER = 1E-8;
    private static final int INCREMENT = 0xbeef;
    private static final long MAX_SEED = 100000000L;
    
    private long seed;

    public MiddleSquareRandom() {
        this(System.nanoTime());
    }

    public MiddleSquareRandom(long seed) {
        this.seed = seed % MAX_SEED; // take last 8 digits
    }
    
    public double nextDouble() {
        seed = seed * seed / 10000;
        seed = (seed + INCREMENT) % MAX_SEED;
        return seed * MULTIPLIER;
    }
}