package massUnderGravityAndAirDrag;
import org.opensourcephysics.numerics.*;
import org.opensourcephysics.display.*;
import java.awt.Color;
import java.awt.Graphics;
public class fallingMass implements Drawable, ODE{
    static double g = 9.8;
    double b = 5.0;
    double k_ground = 10;
    double b_ground = 2;
    double xCoord = 5.0;
    double[] state = new double[3];
    boolean flagGroundCollision = false;
    //double[] currPrevNextState = new double[3]; //to store prev and next state w.r.t curr state to see if mass is going up or down
    double prevY;
    int pixRadius = 3;
    double m = 1;
    //EulerRichardson odeSolver = new EulerRichardson(this);
    //RK45 odeSolver = new RK45(this);
    Verlet odeSolver = new Verlet(this);

    public void setStepSize(double dt){
        odeSolver.setStepSize(dt);
    }
    public void step(){
    odeSolver.step();}
    
    public void setXCoord(double x){
        this.xCoord = x;
    }
    public void setState(double y, double ydot){
        state[0] = y; //y
        state[1] = ydot; //vy
        state[2] = 0; //t
    }
    public void setGroundConstants(double k, double b){
        this.k_ground = k;
        this.b_ground = b;
    }
    
    public double[] getState(){
        return state;
    }
    /*public boolean goingDown(){
        return state[1]>0;
    }*/
    public void getRate(double[] state, double[] rate){
        double y = state[0];
        double vy = state[1];
        rate[0] = vy;
        if(vy>0){
            //rate[1] = g -(b/m)*Math.pow(vy,1);
            rate[1] = -g +(b/m)*Math.pow(vy,1);
        }
        /*else if(vy==0 && flagGroundCollision==true){
            rate[1]-=0.001;
            flagGroundCollision = false;
        }*/
        else{
            //rate[1] = -g -(b/m)*Math.pow(vy,1);
            rate[1] = -g -(b/m)*Math.pow(vy,1);
        }
        if(y<0){
            rate[1] = 0;
            //rate[1] += -k_ground * y/m+b;
            //rate[1] += -k_ground * y/m+b_ground * vy/m;
            rate[1] += -k_ground * y/m + b_ground * Math.abs(vy)/m;
            flagGroundCollision = true;
        }
        rate[2]=1;

    }
    public void draw(DrawingPanel drawingPanel, Graphics g){
        int x = drawingPanel.xToPix(this.xCoord);
        int y = drawingPanel.yToPix(state[0]);
        g.setColor(Color.RED);
        g.fillOval(x-pixRadius, y-pixRadius, 2*pixRadius, 2*pixRadius);
    }
}