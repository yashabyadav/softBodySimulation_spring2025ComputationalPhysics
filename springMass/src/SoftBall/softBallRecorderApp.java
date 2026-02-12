package SoftBall;

import utils.*;


import org.opensourcephysics.controls.AbstractCalculation;
import org.opensourcephysics.controls.CalculationControl;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class softBallRecorderApp extends AbstractCalculation {
    softBall ball;
    BufferedWriter writer;
    int numSteps;
    double dt;

    public void initialize() {
        vec2 center = new vec2(control.getDouble("initial x"), control.getDouble("initial y"));
        int n = control.getInt("Number of surface nodes");
        double R = control.getDouble("Radius");
        double k = control.getDouble("surface spring constant");
        double k2 = control.getDouble("center surface spring constant");
        double kArea = control.getDouble("area consv spring constant");
        double b = control.getDouble("velocity dependent drag factor");
        double c = control.getDouble("surface spring damping constant");
        double c2 = control.getDouble("center to surface spring damping constant");


        ball = new softBall(center, n, k, k2, kArea, R, b, c, c2);
        dt = control.getDouble("dt");
        ball.odeSolver.setStepSize(dt);
        numSteps = control.getInt("Number of steps");

        try {
            writer = new BufferedWriter(new FileWriter("softball_trajectory.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void calculate() {
        try {
            for (int step = 0; step < numSteps; step++) {
                ball.step();
                ball.applyGroundCollision(-3.0, 0.5, 0.5);
                writeCurrentState();
                control.println("Written state in .txt file at "+ball.getStateVec()[ball.stateVec.length-1].x);
            }
            writer.close();
            control.println("Finished writing softball_trajectory.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeCurrentState() throws IOException {
        for (vec2 v : ball.stateVec) {
            writer.write(v.x + "," + v.y + ",");
        }
        writer.newLine();
    }
    public void reset(){
        control.setValue("dt",0.01);
        control.setValue("initial x", 0);
        control.setValue("initial y", 0);
        control.setValue("Number of surface nodes", 6);
        control.setValue("surface spring constant", 2);
        control.setValue("Radius", 1);
        control.setValue("center surface spring constant",1.0);
        control.setValue("area consv spring constant",2.0);
        control.setValue("velocity dependent drag factor",0.2);
        control.setValue("surface spring damping constant",0.05);
        control.setValue("center to surface spring damping constant",0.1);
    }

    public static void main(String[] args) {
        CalculationControl.createApp(new softBallRecorderApp());
    }
}
