package SoftBall;

import utils.vec2;
import utils.colorPalletes;
import java.awt.Color;
import java.util.ArrayList;


import org.opensourcephysics.controls.AbstractSimulation;
import org.opensourcephysics.controls.SimulationControl;
import org.opensourcephysics.display.DrawingFrame;
import org.opensourcephysics.display.DrawingPanel;




public class softBall2App extends AbstractSimulation{
    vec2 ballCenter = new vec2();

    softBall2 ball2;

    DrawingFrame frame = new DrawingFrame();
    DrawingPanel animPanel = frame.getDrawingPanel();
    colorPalletes pallete = new colorPalletes();
    ArrayList<Color> kanagawa = colorPalletes.Kanagawa;
    ArrayList<Color> plotColors = colorPalletes.dataPlot1;

    public void initialize(){
        ball2 = null;
        frame.clearDrawables();
        frame.clearData();
        this.delayTime = 0;
        double dt = control.getDouble("dt");
        double x = control.getDouble("initial x");
        double y = control.getDouble("initial y");
        int n = control.getInt("Number of surface nodes");
        double k = control.getDouble("surface springs' stiffness");
        double k2 = control.getDouble("inner springs' stiffness");
        double kArea = control.getDouble("area consv spring constant");
        double R = control.getDouble("Radius");
        double b = control.getDouble("velocity dependent drag factor");
        double c = control.getDouble("surface spring damping constant");
        double c2 = control.getDouble("inner springs' stiffness");
        boolean dispFVecs = control.getBoolean("(bool) display force vectors");




        ballCenter.x = x;
        ballCenter.y = y;
        ball2 = new softBall2(ballCenter, n, k, k2, kArea, R, b, c, c2, dispFVecs);
        ball2.setStepSize(dt);

        frame.setPreferredMinMax(-5, 5, -5, 5);
        frame.setInteriorBackground(plotColors.get(1));
        frame.setSize(400,400);
        frame.setSquareAspect(true);
        frame.setVisible(true);
        frame.repaint();
        frame.addDrawable(ball2);
    }
    public void doStep(){
        ball2.applyGroundCollision(-3, 0.0, 0.0);
        ball2.step();
        frame.repaint();
    }
    public void reset(){
        control.setValue("dt",0.01);
        control.setValue("initial x", 0);
        control.setValue("initial y", 0);
        control.setValue("Number of surface nodes", 10);
        control.setValue("surface springs' stiffness", 100);
        control.setValue("Radius", 1);
        control.setValue("inner springs' stiffness",100);
        control.setValue("area consv spring constant",2.0);
        control.setValue("velocity dependent drag factor",0.2);
        control.setValue("surface spring damping constant",0.0);
        control.setValue("center to surface spring damping constant",0.0);
        control.setValue("(bool) display force vectors",false);

    }

    public static void main(String[] args){
        SimulationControl.createApp(new softBall2App()); 
    }
    
}