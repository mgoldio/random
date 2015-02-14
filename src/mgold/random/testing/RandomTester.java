package mgold.random.testing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.Random;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;

/**
 * A collection of methods used to test the randomness of a random number
 * generator.
 *
 * @author Michael Goldstein
 * @license Apache 2.0
 * @version 1.0
 */
public class RandomTester {

    private boolean verbose = true;
    private DoubleSupplier supplier;
    private long timerStartNanos;

    /**
     * Constructs a new RandomTester
     * 
     * @param supplier
     *            a source of random doubles from 0 to 1
     */
    public RandomTester(DoubleSupplier supplier) {
        this.supplier = supplier;
    }

    /**
     * @return whether or not the RandomTester should print
     */
    public boolean isVerbose() {
        return verbose;
    }

    /**
     * @param isVerbose
     *            whether or not the RandomTester should print
     */
    public void setVerbose(boolean isVerbose) {
        verbose = isVerbose;
    }

    /**
     * Runs a Monte Carlo Simulation to approximate Pi using the RandomTester's
     * random number generator.
     * 
     * @param points
     *            the number of points to generate for the simulation
     * @param acceptedError
     *            the maximum epsilon to allow as a pass
     * @return whether or not the generator passed
     */
    public boolean testPiEstimateMonteCarloSimulation(final int points, final double acceptedError) {
        startTimer();
        println("Running Monte Carlo Simulation");
        printLineSeparator();
        println(String.format("Approximating pi using %d points", points));
        int pointsWithinQuadrant = 0;
        for(int i = 0; i < points; i++) {
            double x = supplier.getAsDouble();
            double y = supplier.getAsDouble();
            if(Math.hypot(x, y) < 1)
                pointsWithinQuadrant++;
        }
        double pi = (4.0 * pointsWithinQuadrant) / points;
        println(String.format("Pi was calculated to be %f", pi));
        double percentDiff = (pi - Math.PI) / Math.PI;
        println(String.format("Percent difference between actual value is %f%%",
                percentDiff * 100.0));
        if(percentDiff < acceptedError) {
            println(String.format("Calculated value is within accepted error of %.3f%%; "
                    + "test passed.", acceptedError * 100.0));
            finishTimer();
            return false;
        }
        println(String.format("Calculated value is not within accepted error of %.3f%%; "
                + "test failed.", acceptedError * 100.0));
        finishTimer();
        return true;
    }

    /**
     * Runs a Chi-squared goodness of fit test on the RandomTester's generator
     * 
     * @param k
     *            the number of cells to test (i.e. generate numbers with values
     *            from 0 to k)
     * @param independenceDepth
     *            how many repeated numbers to calculate the expected value for
     * @param n
     *            the number of random numbers to generate
     * @param critValue
     *            the chi-squared critical value to test against
     * @return whether or not the generator passed
     */
    public boolean testChiSquaredGoodnessOfFit(final int k, final int independenceDepth, final int n, final double critValue) {
        startTimer();
        println("Running Chi-Squared Goodness of Fit Test");
        printLineSeparator();
        IntSupplier intSupplier = () -> {
            return (int) (supplier.getAsDouble() * k);
        };
        println(String.format("Running test with k=%d, independenceDepth=%d, n=%d, critVal=%.3f", k,
                independenceDepth, n, critValue));
        int[] observed = new int[k];
        double expected = n / Math.pow(k, independenceDepth + 1);
        int independenceCounter = 0;
        int last = intSupplier.getAsInt();
        for(int i = 0; i < n; i++) {
            if(independenceCounter >= independenceDepth)
                observed[last]++;
            int next = intSupplier.getAsInt();
            if(next == last)
                independenceCounter++;
            else
                independenceCounter = 0;
            last = next;
        }
        double chiSquared = 0.0;
        for(int i = 0; i < k; i++) {
            chiSquared += (observed[i] - expected) * (observed[i] - expected) / expected;
        }

        println(String.format("Chi-squared=%f", chiSquared));
        if(chiSquared < critValue)
        {
            println(String.format("Chi-squared is less than the critical value of %.3f; test passed.", critValue));
            finishTimer();
            return true;
        }
        println(String.format("Chi-squared is greater than the critical value of %.3f; test failed.", critValue));
        finishTimer();
        return false;
    }

    /**
     * Tests the randomness of the generator by measuring the compression ratio
     * of zipped data
     * 
     * @param characters
     *            the number of characters to generate
     * @param passingCompressionRatio
     *            the compression ratio critical value at which we should fail
     *            generators
     * @return whether or not the generator passed
     */
    public boolean testEntropyWithZipRatio(int characters, double passingCompressionRatio) {
        startTimer();
        println("Running Zip Compression Ratio Entropy Test");
        printLineSeparator();
        println(String.format("Running test with %d bytes and comparing compression ratio against"
                + " critical value of %.3f", characters, passingCompressionRatio));
        Supplier<Character> charSupplier = () -> {
            return (char) (supplier.getAsDouble() * 128); // random ASCII char
        };

        try {
            final File zipFile = File.createTempFile("tmp", ".zip");
            final File txtFile = File.createTempFile("tmp", ".txt");
            final ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
            zos.putNextEntry(new ZipEntry("chars.txt"));
            OutputStreamWriter zipWriter = new OutputStreamWriter(zos);
            FileWriter txtWriter = new FileWriter(txtFile);
            for(int i = 0; i < characters; i++) {
                char c = charSupplier.get();
                zipWriter.write(c);
                txtWriter.write(c);
            }
            zipWriter.close();
            txtWriter.close();
            long zipSize = Files.size(zipFile.toPath());
            long txtSize = Files.size(txtFile.toPath());
            println(String.format("Uncompressed size: %d bytes", txtSize));
            println(String.format("Compressed size: %d bytes", zipSize));
            double ratio = (double) txtSize / zipSize;
            println(String.format("Compression ratio: %f", ratio));
            if(ratio < passingCompressionRatio) {
                println("Compression ratio is less than critical value; test passed.");
                finishTimer();
                return true;
            }
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }
        println("Compression ratio is greater than critical value; test failed.");
        finishTimer();
        return false;
    }

    /**
     * Generate a random bitmap
     * 
     * @param width
     *            the width of the image to generate
     * @param height
     *            the height of the image to generate
     * @return the generated image
     */
    public RenderedImage generateRandomBitmap(final int width, final int height) {
        startTimer();
        println("Generating Random Bitmap");
        printLineSeparator();
        println(String.format("Image size %dx%d", width, height));
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D g = bi.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        println("Generating image...");
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                if(supplier.getAsDouble() < 0.5) {
                    g.fillRect(x, y, 1, 1);
                }
            }
        }
        finishTimer();
        return bi;
    }

    /**
     * Generates a random bitmap and write the result to the specified file.
     * 
     * @param width
     *            the width of the image to generate
     * @param height
     *            the height of the image to generate
     * @param file
     *            the file to write to
     * @return whether or not the file was written
     */
    public boolean generateAndWriteRandomBitmap(final int width, final int height, final File file) {
        RenderedImage image = generateRandomBitmap(width, height);
        try {
            ImageIO.write(image, "png", file);
        } catch(IOException e) {
            println("Failed to write generated image.");
            return false;
        }
        println(String.format("Generated image written to %s", file.toString()));
        return true;
    }

    /**
     * Starts the nanosecond timer
     */
    private void startTimer() {
        timerStartNanos = System.nanoTime();
    }

    /**
     * Prints the time taken since the timer was started
     */
    private void finishTimer() {
        double secondsTaken = (System.nanoTime() - timerStartNanos) / 1E9;
        println(String.format("Test executed in %f seconds", secondsTaken));
        println("\n");
    }

    private void printLineSeparator() {
        println("------------------------------------------------------------");
    }

    private void println(String s) {
        if(verbose)
            System.out.println(s);
    }

}
