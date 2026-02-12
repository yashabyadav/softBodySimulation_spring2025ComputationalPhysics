package TwoMassesSpringUnderGravity;

import org.opensourcephysics.controls.*;
import org.opensourcephysics.frames.PlotFrame;

public class verticalSpringApp extends AbstractSimulation{
    PlotFrame animationFrame = new PlotFrame("x","y","Freely Falling Vertical Spring");
    verticalSpring spring = new verticalSpring();
    PlotFrame positionFrame = new PlotFrame("t","y", "Positions of the masses over time");
    PlotFrame velocityFrame = new PlotFrame("t", "y_dot", "Velocities of masses over time");
    //PlotFrame animationFrame = new PlotFrame("x", "y", "Trajectory")
    double currSpringLen;
    double time, dt;
    public void initialize(){
        double dt = control.getDouble("dt");
        double x = control.getDouble("x coordinate");
        double y1 = control.getDouble("initial y1");
        double y2 = control.getDouble("initial y2");
        double vy1 = control.getDouble("initial vy1");
        double vy2 = control.getDouble("initial vy2");
        spring.setState(y1, y2, vy1, vy2);
        spring.setXCoord(x);
        spring.setStepSize(dt);
        animationFrame.setPreferredMinMax(-10.0, 10.0,-1, y1+10.0);
       /*
        spring = new verticalSpring;
        frame.addDrawable(verticalSpring.masses);
        frame.addDrawble(verticalSpring.spring);
        */
    }

    public void doStep(){
 
        animationFrame.clearDrawables();
        animationFrame.addDrawable(spring);
        this.delayTime = 0; //controls the time between two frames (milli-seconds), from Lecture slide 3
        spring.step();
      
        if(spring.massCollisionFlag){
            control.println("Masses collide at t="+spring.getState()[4]);
        }
        spring.massCollisionFlag =false;
        if(spring.getState()[0]<=0){
            control.println("Mass 1 hit the ground at time step: "+spring.getState()[4]);
        }
        if(spring.getState()[2]<=0){
            control.println("Mass 2 hit the ground at time step: "+spring.getState()[4]);
        }
        positionFrame.append(0, spring.getState()[4], spring.getState()[0]);
        positionFrame.append(1, spring.getState()[4], spring.getState()[2]);
        velocityFrame.append(0, spring.getState()[4], spring.getState()[1]);
        velocityFrame.append(1, spring.getState()[4], spring.getState()[3]);
    }
    public void reset(){
        control.setValue("dt", 0.01);
        control.setValue("x coordinate", 2.0);
        control.setValue("initial y1", 9.0);
        control.setValue("initial y2", 9.0-spring.l);
        control.setValue("initial vy1", 0);
        control.setValue("initial vy2", 0);
        enableStepsPerDisplay(true);
    }

    public static void main(String[] args){
        SimulationControl.createApp(new verticalSpringApp());
    }

}