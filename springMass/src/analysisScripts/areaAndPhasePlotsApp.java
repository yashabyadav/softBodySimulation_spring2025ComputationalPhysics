package analysisScripts;

import java.awt.Color;
import java.util.ArrayList;


import org.opensourcephysics.controls.AbstractCalculation;
import org.opensourcephysics.controls.CalculationControl;
import org.opensourcephysics.frames.PlotFrame;

import SoftBall.softBall2;
import utils.colorPalletes;
import utils.vec2;

public class areaAndPhasePlotsApp extends AbstractCalculation{
    PlotFrame xPhaseSpace = new PlotFrame("x", "p_x", "x phase plot");
    PlotFrame yPhaseSpace = new PlotFrame("y", "p_y", "y phase plot");
    PlotFrame xyPhaseSpace = new PlotFrame("r", "p", "phase plot");
    colorPalletes pallate = new colorPalletes();
    ArrayList<Color> plotColors = pallate.dataPlot1;



    public void calculate() {
        xPhaseSpace.clearData();
        yPhaseSpace.clearData();
        xyPhaseSpace.clearData();

        xPhaseSpace.setSize(400,400);
        xPhaseSpace.setInteriorBackground(plotColors.get(1));
        xPhaseSpace.setBackground(plotColors.get(5));
        xPhaseSpace.setMarkerColor(1, plotColors.get(6)); // Set the color of the plot
        xPhaseSpace.setVisible(true);

        yPhaseSpace.setSize(400,400);
        yPhaseSpace.setInteriorBackground(plotColors.get(1));
        yPhaseSpace.setBackground(plotColors.get(5));
        yPhaseSpace.setMarkerColor(1, plotColors.get(6)); // Set the color of the plot
        yPhaseSpace.setVisible(true);

        xyPhaseSpace.setSize(400,400);
        xyPhaseSpace.setInteriorBackground(plotColors.get(1));
        xyPhaseSpace.setBackground(plotColors.get(5));
        xyPhaseSpace.setMarkerColor(1, plotColors.get(6)); // Set the color of the plot
        xyPhaseSpace.setVisible(true);


        double dt = 0.01;
        double steps = 10000;
        // simulation parameters
        vec2 ballCenter = new vec2(0, 0);
        int n = 10;
        double R = 1.0;
        double k = 100.0;
        double k2 = 100.0;
        double kArea = 2.0;
        double b = 1.0;
        double c = 0.5;
        double c2 = 0.9;

        softBall2 ball = new softBall2(ballCenter, n, k, k2, kArea, R, b, c, c2, false);
        ball.setStepSize(dt);


        double r,p, x, vx, y, vy;
        int nodeIndex = 3;

        for (int i = 0; i<steps; i++){
            ball.applyGroundCollision(-3, 0.5, 0.5);
            ball.step();
            x = ball.getStateVec()[2*nodeIndex].x;
            vx = ball.getStateVec()[2*nodeIndex+1].x;

            y = ball.getStateVec()[2*nodeIndex].y;
            vy = ball.getStateVec()[2*nodeIndex+1].y;

            r = Math.sqrt(Math.pow((ball.getStateVec()[2*nodeIndex].x),2)+Math.pow((ball.getStateVec()[2*nodeIndex].y),2));
            p = Math.sqrt(Math.pow((ball.getStateVec()[2*nodeIndex+1].x),2)+Math.pow((ball.getStateVec()[2*nodeIndex+1].y),2));


            xPhaseSpace.append(1, x,vx);
            yPhaseSpace.append(1, y,vy);
            xyPhaseSpace.append(1, r,p);
        }

    }
    public static void main(String[] args){
        CalculationControl.createApp(new areaAndPhasePlotsApp()); 
    }
}
