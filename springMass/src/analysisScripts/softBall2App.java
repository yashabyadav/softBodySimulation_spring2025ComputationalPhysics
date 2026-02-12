package analysisScripts;

import utils.vec2;
import utils.colorPalletes;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JLabel;

import org.opensourcephysics.controls.AbstractSimulation;
import org.opensourcephysics.controls.SimulationControl;
import org.opensourcephysics.display.DrawingFrame;
import org.opensourcephysics.display.DrawingPanel;
import org.opensourcephysics.display.OSPLayout;
import org.opensourcephysics.frames.PlotFrame;
import org.opensourcephysics.media.gif.GifVideoRecorder;

import SoftBall.softBall2;



public class softBall2App extends AbstractSimulation{
    vec2 ballCenter = new vec2();

    softBall2 ball2;

    DrawingFrame frame = new DrawingFrame();
    DrawingPanel animPanel = frame.getDrawingPanel();
    
    GifVideoRecorder recorder = new GifVideoRecorder();


    PlotFrame areaVsTimeFrame = new PlotFrame("t", "Area", "Area vs time of the soft ball");
    DrawingPanel areaFramePanel = areaVsTimeFrame.getDrawingPanel();

    PlotFrame yPhaseSpace = new PlotFrame("y", "p_y", "Phase space (y coordinate)");
    //PlotFrame xyPhaseSpace = new PlotFrame("sqrt((x_i)^2+(y_i)^2)", "sqrt((px_i)^2+(py_i)^2)", "Phase space (y coordinate)");
    PlotFrame xyPhaseSpace;
    DrawingPanel yPhaseSpacePanel = yPhaseSpace.getDrawingPanel();
    DrawingPanel xyPhaseSpacePanel;

    colorPalletes pallete = new colorPalletes();
    //ArrayList<Color> metro = colorPalletes.Metro_Colors;
    ArrayList<Color> kanagawa = colorPalletes.Kanagawa;
    ArrayList<Color> plotColors = colorPalletes.dataPlot1;
    //Color[] metro = colorPalletes.Metro_Colors.toArray(new Color[0]);

    int nodeIndex;
    double magR;
    double magV;

    int stepCounter;
    JLabel label2;
    JLabel label3;
    String timeStep;



    boolean drawAnalysisPlots;


    String outputFile = "bouncingBallAnimation.gif";
    public void initialize(){


        ball2 = null;
        frame.clearDrawables();
        frame.clearData();
        this.delayTime = 0;
        drawAnalysisPlots = control.getBoolean("draw analysis plots?");
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

        nodeIndex = control.getInt("nodeIndex for phase plots");
        xyPhaseSpace = new PlotFrame("r","p", "Phase space"+String.valueOf(nodeIndex));
        xyPhaseSpacePanel = xyPhaseSpace.getDrawingPanel();



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
        stepCounter = 0;


        animPanel.setBuffered(true);
        try {
            recorder.createVideo();
            recorder.setFrameDuration(300);
            recorder.getGifEncoder().setRepeat(0);
        } catch (IOException e) {}

        if (drawAnalysisPlots){
        areaFramePanel.setSize(500,500);
        areaVsTimeFrame.setInteriorBackground(plotColors.get(1));
        areaVsTimeFrame.setBackground(plotColors.get(5));
        areaVsTimeFrame.setMarkerColor(0, plotColors.get(6)); // Set the color of the plot
        areaVsTimeFrame.setVisible(true);

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


        //areaVsTimeFrame.setMessage("Inner Spring damping = 0.1");
        JLabel label= new JLabel("<html>Inner Spring damping = 0.1<br>Surface Spring damping = 10</html>");
        label.setOpaque(true);
        label.setBackground(plotColors.get(5));
        label.setForeground(plotColors.get(4));
        label.setFont(new Font("Arial", Font.BOLD, 9));

        areaFramePanel.getGlassPanel().add(label, OSPLayout.TOP_RIGHT_CORNER);
         
        double currTime = ball2.getStateVec()[this.ball2.getStateVec().length-1].x;

        String timeStep = "timestep" + currTime;
        label2 = new JLabel(timeStep + " | Inner Spring Damping = 0.5");
        //label2 = new JLabel(timeStep+"<br> Inner Spring Damping = 0.5");
        label2.setOpaque(true);
        label2.setBackground(plotColors.get(5));
        label2.setForeground(plotColors.get(4));
        label2.setFont(new Font("Arial", Font.BOLD, 11));

        yPhaseSpacePanel.getGlassPanel().add(label2, OSPLayout.TOP_RIGHT_CORNER);

        label3= new JLabel(timeStep+ " | Inner Spring Damping = "+String.valueOf(ball2.getcInner()));
        label3.setOpaque(true);
        label3.setBackground(plotColors.get(5));
        label3.setForeground(plotColors.get(4));
        label3.setFont(new Font("Arial", Font.BOLD, 11));

        yPhaseSpacePanel.getGlassPanel().add(label2, OSPLayout.TOP_RIGHT_CORNER);
        xyPhaseSpacePanel.getGlassPanel().add(label3, OSPLayout.TOP_RIGHT_CORNER);
        }
    }
    public void doStep(){
        
        ball2.applyGroundCollision(-3, 0.0, 0.0);
        ball2.step();
        //frame.repaint();
        frame.render();
        double currTime = ball2.getStateVec()[this.ball2.getStateVec().length-1].x;
        if (drawAnalysisPlots){
        if(currTime<10.0) areaVsTimeFrame.append(0, currTime, ball2.A);
        //areaVsTimeFrame.append(0, currTime, ball2.A);

        timeStep = "timestep = " + String.valueOf(currTime);
        label2.setText(timeStep);
        //label3.setText(timeStep+ " | Inner Spring Damping = "+String.valueOf(ball2.getcInner()));
        label3.setText(timeStep+ " | Surface Spring Damping = "+String.valueOf(ball2.getcInner()));
        //yPhaseSpace.append(1, ball2.getStateVec()[1].y,ball2.getStateVec()[1].y);

        magR = Math.sqrt(Math.pow((ball2.getStateVec()[2*nodeIndex].x),2)+Math.pow((ball2.getStateVec()[2*nodeIndex].y),2));
        magV = Math.sqrt(Math.pow((ball2.getStateVec()[2*nodeIndex+1].x),2)+Math.pow((ball2.getStateVec()[2*nodeIndex+1].y),2));
        //xyPhaseSpace.append(1, magR, magV);
        stepCounter++;
        }

        BufferedImage image = animPanel.render();
        Graphics g = image.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 1, 1);
        g.dispose();

        try {
            recorder.addFrame(image);
        
        }catch (IOException e) {}
        try{
            Thread.sleep(100);
        } catch (InterruptedException ex1) {
            Thread.currentThread().interrupt();
        }
        
        /*
        /*if (currTime > 1) {
            try {
                recorder.saveVideo(outputFile);
                System.out.println("animation saved at: "+System.getProperty("user.dir")+"/"+outputFile);
         } catch (IOException e) {}

        */
        //if (stepCounter%10 == 0){
        //    frame.repaint();
    }

    @Override
    public void stop() {
        try {
            recorder.saveVideo(outputFile);
            System.out.println("animation saved at: "+System.getProperty("user.dir")+"/"+outputFile);
        } catch (IOException e) {
            System.err.println("Failed to save animation: " + e.getMessage());
        }
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
        control.setValue("nodeIndex for phase plots",0);
        control.setValue("draw analysis plots?", false);

    }

    public static void main(String[] args){
        SimulationControl.createApp(new softBall2App()); 
    }
    
}