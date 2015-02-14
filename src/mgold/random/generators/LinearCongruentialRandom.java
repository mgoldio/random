package mgold.random.generators;
 
/**
 * <p>
 * This random number generator is high-speed, low-memory Linear Congruential PRNG. It can be used
 * as an alternative to the Oracle JRE bundled PRNG. In testing, this generator's methods were found
 * to be around 10x faster than those in the bundled implementation while maintaining similar
 * randomness.
 * </p>
 * <p>
 * This implementation should not be used as a replacement in applications that require
 * thread-safety as it is not thread-safe. Consider using
 * {@link java.util.concurrent.ThreadLocalRandom} instead.
 * </p>
 * <p>
 * Instances of {@code Random} should not be considered cryptographically secure. For
 * security-sensitive applications, consider using {@link java.security.SecureRandom} or an
 * implementation of the Blum Blum Shub algorithm.
 * </p>
 * <p>
 * This generator may be used in accordance with the terms specified in the <a
 * href="http://www.apache.org/licenses/LICENSE-2.0.html" target="_blank"> Apache 2.0 License.</a>
 * </p>
 *
 * @author Michael Goldstein
 * @version 1.0
 */
public class LinearCongruentialRandom {
 
    private static final long MULTIPLIER = 1103515245; // factor chosen because it is used
    // in many other Linear Congruential PRNGs (source: http://bit.ly/17OACBx)
    private static final long INC = 0xbeef; // somewhat arbitrary, chosen for amusement
    private long seed;
 
    private static final float MAX_FLOAT = 5.36871012E8f; // we only use 29 bits to
    // prevent degradation so we divide by the max 29-bit float
    private static final double MAX_DOUBLE = 2.81474976710656E14; // we only use 48
    // bits to prevent degradation so we divide by the max 48-bit double
 
    /**
     * Creates an instance of the random number generator.
     */
    public LinearCongruentialRandom() {
        // attempts to seed the Random with an unpredictable seed
        // 7744144276301 chosen because it's a large prime
        seed = System.nanoTime() % 7744144276301L;
    }
 
    /**
     * Creates an instance of the random number generator using the {@code long} seed.
     */
    public LinearCongruentialRandom(long seed) {
        this.seed = seed;
    }
 
    /**
     * Generates a sequence of pseudorandom bits.
     *
     * @param bits the number of bits to generate
     */
    protected long next(int bits) {
        seed = seed * MULTIPLIER + INC;
        return(seed >>> (64 - bits));
    }
 
    /**
     * Returns a uniformly distributed, pseudorandom {@code boolean} value.
     *
     * @return the next pseudorandom {@code boolean} value from the sequence
     */
    public boolean nextBoolean() {
        return next(1) == 0L;
    }
 
    /**
     * Returns a uniformly distributed, pseudorandom {@code double} value. It contains only 48
     * significant bits rather than 64 to prevent degradation.
     *
     * @return the next pseudorandom {@code double} value from the sequence
     * @see #nextFloat()
     */
    public double nextDouble() {
        // 48 significant bits
        return (double)((next(16) << 32L) + next(32)) / MAX_DOUBLE;
    }
 
    /**
     * Returns a uniformly distributed, pseudorandom {@code float} value. It contains only 29
     * significant bits rather than 32 to prevent degradation.
     *
     * @return the next pseudorandom {@code float} value from the sequence
     * @see #nextDouble()
     */
    public float nextFloat() {
        // 29 significant bits
        return next(29) / MAX_FLOAT;
    }
 
    /**
     * Returns an approximately normally distributed Z-score {@code double} value (value with a mean
     * of 0 and standard deviation of 1.0).
     *
     * @return the next pseudorandom Z-score from the sequence
     * @see #nextDouble()
     */
    public double nextGaussian() {
        // converts two doubles into a z-score (method is not exact)
        return (nextBoolean() ? 1 : -1) * Math.log10(nextDouble() * nextDouble());
    }
 
    /**
     * Returns a uniformly distributed, pseudorandom {@code int} value.
     *
     * @return the next pseudorandom {@code int} value from the sequence
     * @see #nextLong()
     */
    public int nextInt() {
        return (int)next(32);
    }
 
    /**
     * Returns a uniformly distributed, pseudorandom {@code int} value from 0 (inclusive) to n
     * (exclusive).
     *
     * @param n the exclusive bound on the random number returned
     * @return the next pseudorandom {@code int} value between 0 (inclusive) and n (exclusive) from
     *         the sequence
     * @throws IllegalArgumentException if n is not positive
     */
    public int nextInt(int n) { // input 0 to Integer.MAX_VALUE
        if(n < 0)
            throw new IllegalArgumentException("n must be positive");
        return (int)(nextFloat() * n);
    }
 
    /**
     * Returns a uniformly distributed, pseudorandom {@code long} value.
     *
     * @return the next pseudorandom {@code long} value from the sequence
     * @see #nextInt()
     */
    public long nextLong() {
        return next(32) << 32L + next(32);
    }
}
