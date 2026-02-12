package analysisScripts;

import SoftBall.softBall2;

import utils.vec2;
import utils.colorPalletes;

import org.opensourcephysics.frames.PlotFrame;
import org.opensourcephysics.controls.AbstractCalculation;
import org.opensourcephysics.controls.CalculationControl;
import java.awt.Color;
import java.util.ArrayList;

/**
 * Computes the time taken to simulate fixed number of steps for softBall2 with varying number of surface nodes.
 */
public class computeTimeNumNodesApp extends AbstractCalculation {

    PlotFrame computeTimePlot = new PlotFrame("Number of Surface Nodes (n)", "Compute Time (ms)", "Compute Time vs n");

    public void calculate() {
        computeTimePlot.clearData();

        double dt = 0.01;
        int steps = 100;

        // simulation parameters
        vec2 ballCenter = new vec2(0, 0);
        double R = 1.0;
        double k = 100.0;
        double k2 = 100.0;
        double kArea = 2.0;
        double b = 1.0;
        double c = 0.5;
        double c2 = 0.9;

        colorPalletes pallate = new colorPalletes();
        ArrayList<Color> plotColors = pallate.dataPlot1;

        for (int n = 10; n <= steps; n += 10) {
            softBall2 ball = new softBall2(ballCenter, n, k, k2, kArea, R, b, c, c2, false);
            ball.setStepSize(dt);

            long startTime = System.nanoTime();
            for (int i = 0; i < steps; i++) {
                ball.applyGroundCollision(-3, 0.5, 0.0);
                ball.step();
            }
            long endTime = System.nanoTime();
            double elapsedMs = (endTime - startTime) / 1e6;

            computeTimePlot.append(0, n, elapsedMs);
            System.out.printf("n = %d\tTime = %.3f ms%n", n, elapsedMs);
        }
        computeTimePlot.setInteriorBackground(plotColors.get(1));
        computeTimePlot.setBackground(plotColors.get(5));
        computeTimePlot.setSize(500,500);
        computeTimePlot.setVisible(true);
        computeTimePlot.setMarkerSize(0, 2);
        computeTimePlot.setConnected(true);
        computeTimePlot.setLineColor(0, plotColors.get(7));
        computeTimePlot.setMarkerColor(0, plotColors.get(6));
        control.calculationDone("Benchmark complete.");
    }

    public static void main(String[] args) {
        CalculationControl.createApp(new computeTimeNumNodesApp());
    }
}
