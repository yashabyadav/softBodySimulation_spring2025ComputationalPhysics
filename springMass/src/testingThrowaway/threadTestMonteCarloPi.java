package testingThrowaway;

import java.util.Random;
public class threadTestMonteCarloPi {

    static class PiEstimator extends Thread {
        private final long numPoints;
        private long insideCircle = 0;
        private final Random random = new Random();

        public PiEstimator(long numPoints) {
            this.numPoints = numPoints;
        }

        public void run() {
            for (long i = 0; i < numPoints; i++) {
                double x = random.nextDouble();
                double y = random.nextDouble();
                if (x * x + y * y <= 1.0) {
                    insideCircle++;
                }
            }
        }

        public long getInsideCircle() {
            return insideCircle;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final int numThreads = 4;
        final long totalPoints = 500000000L;
        final long pointsPerThread = totalPoints / numThreads;

        long startTime = System.currentTimeMillis();
        
        PiEstimator[] estimators = new PiEstimator[numThreads];

        for (int i = 0; i < numThreads; i++) {
            estimators[i] = new PiEstimator(pointsPerThread);
            estimators[i].start();
        }

        long totalInsideCircle = 0;
        for (PiEstimator estimator : estimators) {
            estimator.join();
            totalInsideCircle += estimator.getInsideCircle();
        }

        double piEstimate = 4.0 * totalInsideCircle / totalPoints;
        System.out.println("Estimated value of pi: " + piEstimate);
        
        long endTime = System.currentTimeMillis();
        
        System.out.println((endTime - startTime) / 1000.0 + " seconds to run");
    }
}

