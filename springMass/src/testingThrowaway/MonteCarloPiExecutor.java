package testingThrowaway;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class MonteCarloPiExecutor {
    static class PiTask implements Callable<Long> {
        private final long numPoints;
        private final Random random = new Random();

        public PiTask(long numPoints) {
            this.numPoints = numPoints;
        }

        @Override
        public Long call() {
            long insideCircle = 0;
            for (long i = 0; i < numPoints; i++) {
                double x = random.nextDouble();
                double y = random.nextDouble();
                if (x * x + y * y <= 1.0) {
                    insideCircle++;
                }
            }
            return insideCircle;
        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        final int numThreads = 4;
        final long totalPoints = 500000000L;
        final long pointsPerThread = totalPoints / numThreads;

        long startTime = System.currentTimeMillis();
        
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<Long>> results = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            results.add(executor.submit(new PiTask(pointsPerThread)));
        }

        long totalInsideCircle = 0;
        for (Future<Long> future : results) {
            totalInsideCircle += future.get(); // blocks until result is ready
        }

        executor.shutdown();

        double piEstimate = 4.0 * totalInsideCircle / totalPoints;
        System.out.println("Estimated value of pi: " + piEstimate);
        
        long endTime = System.currentTimeMillis();
        
        System.out.println((endTime - startTime) / 1000.0 + " seconds to run");
    }
}


