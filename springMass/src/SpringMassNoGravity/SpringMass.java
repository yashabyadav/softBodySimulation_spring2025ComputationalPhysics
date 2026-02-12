package SpringMassNoGravity;
import org.opensourcephysics.numerics.ODE;
import org.opensourcephysics.display.*;

public class SpringMass implements ODE, Drawable {
  double[] state = new double[3]; // x, v, t
  double kOverM = 1.0;
  double massRadius = 0.05;

  public void getRate(double[] state, double[] rate) {
    rate[0] = state[1];
    rate[1] = -kOverM * state[0];
    rate[2] = 1;
  }

  public double[] getState() {
    return state;
  }

  public void draw(DrawingPanel panel, java.awt.Graphics g) {
    int xPix = panel.xToPix(state[0]);
    int yPix = panel.yToPix(0);
    int wallPix = panel.xToPix(0);
    int springY = panel.yToPix(0);

    // Draw spring line
    g.setColor(java.awt.Color.BLACK);
    g.drawLine(wallPix, springY, xPix, springY);

    // Draw mass
    g.setColor(java.awt.Color.RED);
    int size = (int)(massRadius * panel.getWidth());
    g.fillOval(xPix - size/2, yPix - size/2, size, size);
  }

  public void setInitialState(double x0, double v0) {
    state[0] = x0;
    state[1] = v0;
    state[2] = 0;
  }
}

