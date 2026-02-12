package massUnderGravityAndAirDrag;

import java.awt.Color;

import org.opensourcephysics.controls.*;
import org.opensourcephysics.frames.PlotFrame;

public class fallingMassApp extends AbstractSimulation{
    PlotFrame animationFrame = new PlotFrame("x", "y", "Freely Falling Vertical Mass");
    PlotFrame positionFrame = new PlotFrame("t","y", "Positions of the masses over time");
    PlotFrame velocityFrame = new PlotFrame("t", "y_dot", "Velocities of masses over time");
    fallingMass mass = new fallingMass();

    Color bgColor = Color.decode("#1F1F28");
    Color fgColor = Color.decode("#DCD7BA");
    Color accent1 = Color.decode("#957FB8");    // Accent Purple
    Color accent2 = Color.decode("#7FB4CA");    // Accent Blue
    Color accent3 = Color.decode("#98BB6C");    // Accent Green
    Color accent4 = Color.decode("#E6C384");    // Accent Yellow
    Color accent5 = Color.decode("#C34043");    // Accent Red

    public void initialize(){
        double dt = control.getDouble("dt");
        double x = control.getDouble("x coordinate");
        double y = control.getDouble("initial y");
        double vy = control.getDouble("initial vy");
        double k_ground = control.getDouble("k_ground");
        double b_ground = control.getDouble("b_ground");
        mass.setGroundConstants(k_ground, b_ground);
        mass.setState(y,vy);
        mass.setXCoord(x);
        mass.setStepSize(dt);

        //animationFrame.getPlottingPanel().setBackground(bgColor);
        animationFrame.setPreferredMinMax(-10.0, 10.0,-1, y+10.0);
    } 
    public void doStep(){
        positionFrame.append(0, mass.getState()[2], mass.getState()[0]);
        velocityFrame.append(0, mass.getState()[2], mass.getState()[1]);
        animationFrame.clearDrawables();
        animationFrame.addDrawable(mass);
        mass.step();
        animationFrame.repaint();
        if(mass.getState()[0]<=2){
            control.println("y ="+mass.getState()[0]+" ,ydot "+mass.getState()[2]);
        }
        
    }
    public void reset(){
        control.setValue("dt", 0.001);
        control.setValue("x coordinate", 2.0);
        control.setValue("initial y", 5.0);
        control.setValue("initial vy", 0.0);
        control.setValue("k_ground", 100.0);
        control.setValue("b_ground", 30.0);
        enableStepsPerDisplay(true);
        setStepsPerDisplay(500);
    }
    public static void main(String[] args){
        SimulationControl.createApp(new fallingMassApp());
    }

}
