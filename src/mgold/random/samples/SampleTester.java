package mgold.random.samples;

import java.io.File;
import java.util.Random;
import mgold.random.generators.LinearCongruentialRandom;
import mgold.random.generators.MiddleSquareRandom;
import mgold.random.testing.RandomTester;

/**
 * Testing of various random number generators using the RandomTester class
 * 
 * @author Michael Goldstein
 */
public class SampleTester {

    public static void main(String[] args) {
        MiddleSquareRandom msr = new MiddleSquareRandom(0L);
        RandomTester msrTester = new RandomTester(msr::nextDouble);
        LinearCongruentialRandom lcr = new LinearCongruentialRandom();
        RandomTester lcrTester = new RandomTester(lcr::nextDouble);
        Random rand = new Random();
        RandomTester jvmrTester = new RandomTester(rand::nextDouble);

        int passedTests = 0;
        System.out.println("Testing Middle Square Random...\n");
        if(msrTester.testChiSquaredGoodnessOfFit(10, 0, 100_000_000, 19.023))
            passedTests++;
        if(msrTester.testChiSquaredGoodnessOfFit(10, 1, 100_000_000, 19.023))
            passedTests++;
        if(msrTester.testChiSquaredGoodnessOfFit(10, 3, 100_000_000, 19.023))
            passedTests++;
        if(msrTester.testEntropyWithZipRatio(33554432, 1.15))
            passedTests++;
        if(msrTester.testPiEstimateMonteCarloSimulation(1_000_000, .001))
            passedTests++;
        System.out.printf("Middle Square Random Passed %d/%d tests.\n\n\n", passedTests, 5);

        passedTests = 0;
        System.out.println("Testing Linear Congruential Random...\n");
        if(lcrTester.testChiSquaredGoodnessOfFit(10, 0, 100_000_000, 19.023))
            passedTests++;
        if(lcrTester.testChiSquaredGoodnessOfFit(10, 1, 100_000_000, 19.023))
            passedTests++;
        if(lcrTester.testChiSquaredGoodnessOfFit(10, 3, 100_000_000, 19.023))
            passedTests++;
        if(lcrTester.testEntropyWithZipRatio(33554432, 1.15))
            passedTests++;
        if(lcrTester.testPiEstimateMonteCarloSimulation(1_000_000, .001))
            passedTests++;
        System.out.printf("Linear Congruential Random Passed %d/%d tests.\n\n\n", passedTests, 5);

        passedTests = 0;
        System.out.println("Testing java.util Random...\n");
        if(jvmrTester.testChiSquaredGoodnessOfFit(10, 0, 100_000_000, 19.023))
            passedTests++;
        if(jvmrTester.testChiSquaredGoodnessOfFit(10, 1, 100_000_000, 19.023))
            passedTests++;
        if(jvmrTester.testChiSquaredGoodnessOfFit(10, 3, 100_000_000, 19.023))
            passedTests++;
        if(jvmrTester.testEntropyWithZipRatio(33554432, 1.15))
            passedTests++;
        if(jvmrTester.testPiEstimateMonteCarloSimulation(1_000_000, .001))
            passedTests++;
        System.out.printf("java.util Random Passed %d/%d tests.\n\n\n", passedTests, 5);
    }

}