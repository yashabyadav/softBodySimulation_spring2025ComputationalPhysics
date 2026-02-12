package SpringMassNoGravity;

import org.opensourcephysics.controls.*;
import org.opensourcephysics.frames.*;
import org.opensourcephysics.numerics.*;

public class SpringMassApp extends AbstractSimulation {
  SpringMass springMass = new SpringMass();
  ODESolver solver = new EulerRichardson(springMass);
  DisplayFrame displayFrame = new DisplayFrame("x", "y", "Spring-Mass Animation");

  public SpringMassApp() {
    displayFrame.addDrawable(springMass);
    displayFrame.setPreferredMinMax(-2, 2, -1, 1);
  }

  public void initialize() {
    double x0 = control.getDouble("Initial position");
    double v0 = control.getDouble("Initial velocity");
    double dt = control.getDouble("Time step");
    springMass.setInitialState(x0, v0);
    solver.setStepSize(dt);
    displayFrame.clearDrawables();
    displayFrame.addDrawable(springMass);
  }

  public void doStep() {
    solver.step();
    displayFrame.repaint();
  }

  public void reset() {
    control.setValue("Initial position", 1.0);
    control.setValue("Initial velocity", 0.0);
    control.setValue("Time step", 0.05);
  }

  public static void main(String[] args) {
    SimulationControl.createApp(new SpringMassApp());
  }
}