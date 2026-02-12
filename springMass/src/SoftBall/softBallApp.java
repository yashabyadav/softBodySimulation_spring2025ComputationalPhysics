package SoftBall;

import utils.vec2;
import utils.colorPalletes;


import java.awt.Color;
import java.util.ArrayList;

import org.opensourcephysics.controls.AbstractSimulation;
import org.opensourcephysics.controls.SimulationControl;
import org.opensourcephysics.display.DrawingFrame;
import org.opensourcephysics.frames.PlotFrame;



public class softBallApp extends AbstractSimulation{
    vec2 ballCenter = new vec2();
    
    softBall ball1;

//    softBall2 ball2;
    //PlotFrame frame = new PlotFrame("x","y","Soft Ball");
    DrawingFrame frame = new DrawingFrame();
    PlotFrame areaVsTimeFrame = new PlotFrame("t", "Area", "Area vs time of the soft ball");
    int g_n;

    colorPalletes pallete = new colorPalletes();
    ArrayList<Color> metro = colorPalletes.Metro_Colors;
    ArrayList<Color> kanagawa = colorPalletes.Kanagawa;
    //Color[] metro = colorPalletes.Metro_Colors.toArray(new Color[0]);
    int stepCounter;

    public void initialize(){
        ball1 = null;
        frame.clearDrawables();
        frame.clearData();
        this.delayTime = 0;
        double dt = control.getDouble("dt");
        double x = control.getDouble("initial x");
        double y = control.getDouble("initial y");
        int n = control.getInt("Number of surface nodes");
        g_n = n;
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
        ball1 = new softBall(ballCenter,n,k,k2,kArea,R,b,c,c2);
        //ball2 = new softBallNoCenterNode(ballCenter, n, k, k2, kArea, R, b, c, c2, dispFVecs);
        //ball2 = new softBall2(ballCenter, n, k, k2, kArea, R, b, c, c2, dispFVecs);
        //ball1.setStepSize(dt);
        ball1.setStepSize(dt);

        frame.setPreferredMinMax(-5, 5, -5, 5);
        frame.setInteriorBackground(kanagawa.get(3));
        //frame.setBackground(Color.BLACK);
        frame.setSize(400,400);
        frame.setSquareAspect(true);
        frame.setVisible(true);
        frame.repaint();
        //frame.addDrawable(ball1);
        frame.addDrawable(ball1);
        stepCounter = 0;

        areaVsTimeFrame.setVisible(true);
        
    }
    public void doStep(){
        
        //ball2.applyGroundCollision(-3, 0.0, 0.0);
        //ball2.step();
        ball1.applyGroundCollision(-3.0, 0.5,0.5);
        ball1.step();
        //areaVsTimeFrame.append(0, currTime, ball1.A);
        //int stateVecLen = ball1.getStateVec().length;
        //System.out.println("Center position at time "+ball1.getStateVec()[stateVecLen-1].x+" is = ("+ball1.getStateVec()[0].x+","+ball1.getStateVec()[0].y+")");
        //System.out.println("Center velocity at time "+ball1.getStateVec()[stateVecLen-1].x+" is = ("+ball1.getStateVec()[1].x+","+ball1.getStateVec()[1].y+")");
        /*for(int i=0; i<g_n+1;i++){
            System.out.println("accelaration at time for node "+i+" at time t = "+ball1.getStateVec()[stateVecLen-1].x+" is = ("+ball1.getAcclrnVec()[i].x+","+ball1.getAcclrnVec()[i].y+")");
    }*/
        stepCounter++;
        frame.repaint();
        //if (stepCounter%10 == 0){
        //    frame.repaint();
        //}
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
        control.setValue("surface spring damping constant",0.05);
        control.setValue("center to surface spring damping constant",0.1);
        control.setValue("(bool) display force vectors",false);

    }

    public static void main(String[] args){
        SimulationControl.createApp(new softBallApp()); 
    }
    
}