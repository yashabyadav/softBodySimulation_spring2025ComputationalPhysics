package utils;


public abstract class AbstractODESolver2D implements ODESolver2D{
    protected double stepSize = 0.1;
    protected int numEqn = 0;
    protected ODE2D ode;

   public AbstractODESolver2D(ODE2D var1) {
      this.ode = var1;
      this.initialize(0.1);
   }

   public abstract double step();

   public void setStepSize(double dt) {
      this.stepSize = dt;
   }

   public void initialize(double dt) {
      this.stepSize = dt;
      vec2[] stateVec = this.ode.getStateVec();
      if (stateVec == null) {
         this.numEqn = 0;
      } else {
         this.numEqn = stateVec.length;
      }

   }

   public double getStepSize() {
      return this.stepSize;
   }

}
