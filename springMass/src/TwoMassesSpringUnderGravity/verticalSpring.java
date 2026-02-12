package TwoMassesSpringUnderGravity;

import org.opensourcephysics.numerics.*;
import org.opensourcephysics.display.*;
import java.awt.Color;
import java.awt.Graphics;

public class verticalSpring implements Drawable, ODE{
    static double g = 9.8;
    double k = 3.0; //spring constant
    double l = 2; //eqbm length of the spring
    double b = 1.0;
    double k_ground = 5000;
    double b_ground = 50;
    double k_massCollision = 10000;
    double b_massCollision = 100;
    boolean massCollisionFlag = false;
    double[] state = new double[5];
    double springLengthCurrTime = 2.0;
    Color colorRed = Color.RED;
    int pixRadius = 3;
    double xCoord = 5.0;
    //Verlet odeSolver = new Verlet(this);
    EulerRichardson odeSolver = new EulerRichardson(this);

    double[] rateReturn = new double[5];
    public void setStepSize(double dt){
        odeSolver.setStepSize(dt);
    }
    /*public boolean detectCollisionBetweenMasses(){
       if(springLengthCurrTime<=0){ 
        return true;
       }
       else{
        return false;
       }
    }*/ 
    
    public void step(){
        springLengthCurrTime = state[0]-state[2];
        odeSolver.step();
        //detectCollisionWithGround();
    }
    public void setXCoord(double x){
        this.xCoord = x;
    }
    public double getXCoord(){
        return this.xCoord;
    }
    public void setState(double y1, double y2, double y1Dot, double y2Dot){
        state[0] = y1;
        state[1] = y1Dot;
        state[2] = y2;
        state[3] = y2Dot;
        state[4] = 0;
    }
    public double[] getState(){
        return state;
    }
    public double[] getRate(){
        return rateReturn;
    }
    
    public void getRate(double[] state, double[] rate){
        /*if(state[0]<0){
            rate[1] += -k_ground*state[0]-b_ground*state[1];
        }
        */
        rate[0] = state[1];
        rate[2] = state[3];


        rate[1] = -g+k*((state[0]-state[2])-l)+b*(state[1]-state[3]);
        rate[3] = -g-k*((state[0]-state[2])-l)-b*(state[1]-state[3]);

        rate[4] = 1;
        
        if(springLengthCurrTime <=0.01){
            massCollisionFlag = true;
            //double penetration = Math.abs(state[2] - state[0]); // how much y2 is "above" y1
            double penetration = -springLengthCurrTime;// how much y2 is "above" y1
            double relVel = state[1]-state[3];
            /*if(state[0]<0){
                rate[1] += k_massCollision * penetration - b_massCollision * state[1]; // repel y1
            }
            else{
                rate[1] += -k_massCollision * penetration - b_massCollision * state[1]; // repel y1
            }
            if(state[2]<0){
                rate[3] += -k_massCollision * penetration - b_massCollision * state[3];  // repel y2
            }
            else{
                rate[3] += k_massCollision * penetration - b_massCollision * state[3];  // repel y2
            }*/
            rate[1] += k_massCollision * penetration * penetration - b_massCollision * relVel; // repel y1
            rate[3] += -k_massCollision * penetration * penetration + b_massCollision * relVel; // repel y2
        }
        if(state[2]<=0.1){
            //rate[3] += -k_ground*state[2]*state[2]-b_ground*state[3];
            rate[3] += k_ground*state[2]*state[2]-b_ground*Math.abs(state[3]);
        }

        rateReturn[1] = rate[1];
        rateReturn[3] = rate[3];
    }
    public void draw(DrawingPanel drawingPanel, Graphics g){
        int x = drawingPanel.xToPix(this.xCoord);
        int y1pix = drawingPanel.yToPix(state[0]);
        int y2pix = drawingPanel.yToPix(state[2]);
        //spring represented as a line---//
        g.setColor(Color.green);
        //spring represented as a line---//
        //masses------//
        g.drawLine(x, y1pix, x, y2pix);
        g.setColor(Color.RED);
        //masses------//
        g.fillOval(x-pixRadius, y1pix-pixRadius, 2*pixRadius, 2*pixRadius);
        g.setColor(Color.BLUE);
        g.fillOval(x-pixRadius, y2pix-pixRadius, 2*pixRadius, 2*pixRadius);

    }
}
