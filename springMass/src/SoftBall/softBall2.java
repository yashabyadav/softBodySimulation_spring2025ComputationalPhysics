package SoftBall;

import java.util.ArrayList;

import org.opensourcephysics.display.Drawable;
import org.opensourcephysics.display.DrawingPanel;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;

import utils.ODE2D;
import utils.vec2;
import utils.Verlet2D;
import utils.colorPalletes;



public class softBall2 implements Drawable, ODE2D{
public double x, y;
    vec2 center = new vec2(x,y);
    vec2 initVel;
    public int n; //number of beads and springs that make the surface of the ball
    public double R; //radius of the soft ball
    ArrayList<vec2> massList = new ArrayList<>();
    public double k; // spring constant for springs connecting the surface nodes
    public double k2 = 25.0; // spring constant for"springs" connecting surface nodes and the center of the bead
    public double cSurface;
    public double cInner;
    public double[][] allSpringsEquilibriumLen;

    double m = 1; //"mass" of the surface nodes
    vec2 currMassCoordinate;

    double A0 = 0;//initial (equilibrium) area of the soft body
    public double A;
    double kArea = 50.0;//spring constant for area restoring force
    double dampingFactor;
    int pixRadius = 3;

    double dt;
    vec2 g = new vec2(0,-9.8);
    vec2[] stateVec;
    vec2[] acclrnVec;
    Verlet2D odeSolver;

    boolean displayForceVectors = false;
   
    

    colorPalletes pallate = new colorPalletes();
    ArrayList<Color> metro = pallate.Metro_Colors;
    ArrayList<Color> azure = pallate.Color_Azure;
    ArrayList<Color> kanagawa = pallate.Kanagawa;
    ArrayList<Color> dataPlot1 = pallate.dataPlot1;

    public softBall2(vec2 centerCoordinateVec2, int numBeads, double springConstBlue, double springConstGreen, double springConstArea, double ballRadius, double airDragFactor, double surfaceSpringDampingConst, double centerToSurfaceSpringDampingConst, boolean displayForceVectors){
        this.center = centerCoordinateVec2;
        this.n = numBeads;
        this.k = springConstBlue;
        this.k2 = springConstGreen;
        this.kArea = springConstArea;
        this.R = ballRadius;
        this.dampingFactor = airDragFactor;
        this.cSurface = surfaceSpringDampingConst;
        this.cInner = centerToSurfaceSpringDampingConst;
        this.displayForceVectors = displayForceVectors;

        initializeSoftBall();   
        odeSolver = new Verlet2D(this);
        
    }
    public void initializeSoftBall(){
        this.stateVec = new vec2[2*(this.n)+1];//n position vectors for the surface nodes as well as n velocity vectors and 1 for the center of mass 
        this.acclrnVec = new vec2[this.n];
        this.initializeStateVecObjectsWithZeros();
        this.initializeAcclrnVecObjectsWithZeros();
        for(int i=0; i<this.n;i++){
           currMassCoordinate = new vec2(
                (R*Math.cos((i)*2*Math.PI/n)), //x coordinate of the ith node from center
                (R*Math.sin((i)*2*Math.PI/n)) //y coordinate of the ith node from the center
                );
            massList.add(currMassCoordinate);
            this.stateVec[2*i].assign(currMassCoordinate);
        }
        
        
        // why was the second element n-1 before?
        this.allSpringsEquilibriumLen = new double[this.n][this.n];
        
        double[] xCoordArr = new double[this.n], yCoordArr = new double[this.n];
        for(int i=0; i<this.n;i++){
            xCoordArr[i] = this.stateVec[2*i].x;            
            yCoordArr[i] = this.stateVec[2*i].y;
        }
        this.A0 = polygonAreaFromVerticesCoordinates(xCoordArr, yCoordArr, this.n);

        //ai genereated------------------------------//
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                // the j-th neighbor of i on the circle has angular separation (j+1)*2*pi/n
                double ang = ((j - i) * 2.0 * Math.PI / n);
                allSpringsEquilibriumLen[i][j] = Math.abs( R * 2.0 * Math.sin(ang / 2.0));
            }
        }
        //ai genereated------------------------------//


        this.odeSolver = new Verlet2D(this);
    }
    public void setStepSize(double dt){
        odeSolver.setStepSize(dt);
    }
    public void step(){
        odeSolver.step();
    }
    public vec2[] getStateVec(){
        return this.stateVec;
    }
    public vec2[] getAcclrnVec(){
        return this.acclrnVec;
    }
    public double calcCurrSpringLen(vec2 springNodeCoordinate1, vec2 springNodeCoordinate2){
        vec2 p1 = springNodeCoordinate1;
        vec2 p2 = springNodeCoordinate2;

        return vec2.distanceFormula(p1, p2);
    }
    private vec2 calcUnitVecAlongSpring(vec2 springNodeCoordinate1, vec2 springNodeCoordinate2){
        /**
         * coord1 -> coord2
         */
        vec2 p1 = springNodeCoordinate1;
        vec2 p2 = springNodeCoordinate2;

        return vec2.unitVec2(p1, p2);
    }
    private vec2 calcSpringForceVector(double eqbmSpringLen, double currSpringLen, double k, vec2 unitVecAlongSpring, vec2 vel_i,vec2 vel_j, double springDampingFactor){
        double delL = (currSpringLen-eqbmSpringLen); //compression in the spring
        //return unitVecAlongSpring.scalerMult(delL);
        //not sure about the signs yet, will try to both positive and negative

        if(Math.abs(delL)<10E-6){
            return new vec2(0,0);
        }
        else{
            double relativeVelocityMag = (vel_i.x - vel_j.x) * unitVecAlongSpring.x + (vel_i.y - vel_j.y) * unitVecAlongSpring.y;

            double dampingTerm = springDampingFactor * relativeVelocityMag;
            return unitVecAlongSpring.scalerMult(k * delL - dampingTerm);
        }
    }



    private double polygonAreaFromVerticesCoordinates(double[] xCoordArr, double[] yCoordArr, int numPoints) //https://www.mathopenref.com/coordpolygonarea2.html
        { 
        double area = 0;   // Accumulates area 
        int j = numPoints-1; 

        for (int i=0; i<numPoints; i++){
            area +=  (xCoordArr[j]+xCoordArr[i]) * (yCoordArr[j]-yCoordArr[i]); 
            j = i;  //j is previous vertex to i
        }
        return area/2;
        }



    private vec2 calcAreaConsvForceOnThisNode(int nodeIndex){
        int nNext = (nodeIndex == this.n-1 ? 0 : nodeIndex + 1);
        int nPrev = (nodeIndex == 0 ? this.n-1 : nodeIndex - 1);

        vec2 fArea = new vec2(this.stateVec[2*(nNext)].y-this.stateVec[2*(nPrev)].y, -(this.stateVec[2*(nNext)].x-this.stateVec[2*(nPrev)].x));
        
        return fArea.scalerMult((this.A-this.A0)*this.kArea/(2*this.A0));
    }


    private void initializeStateVecObjectsWithZeros(){
        for(int i=0;i<stateVec.length;i++){
            this.stateVec[i] = new vec2(0,0);
        }
    }
    private void initializeAcclrnVecObjectsWithZeros(){
        for(int i=0;i<acclrnVec.length;i++){
            this.acclrnVec[i] = new vec2(0,0);
        }
    }


    public void applyGroundCollision(double groundY, double bounceDamping, double friction) {
    for (int i = 0; i < this.n; i++) {
        vec2 pos = stateVec[2*i];
        vec2 vel = stateVec[2*i+1];
        if (pos.y < groundY) {
            //System.out.println("bounceDamping = "+bounceDamping);
            pos.y = groundY;
            if (vel.y < 0) vel.y *= -bounceDamping;
            vel.x *= friction; // friction in x direction to stop nodes from sliding in the horizontal direction due to the surface force
            }
        }
    }

    public double getcInner(){
        return this.cInner;
    }
    public double getcSurface(){
        return this.cSurface;
    }


    public void computeAcceleration(){

        this.initializeAcclrnVecObjectsWithZeros();

        double[] xCoordArr = new double[this.n], yCoordArr = new double[this.n];
        for(int i=0; i<this.n;i++){
            xCoordArr[i] = this.stateVec[2*i].x;            
            yCoordArr[i] = this.stateVec[2*i].y;
        }
        this.A = polygonAreaFromVerticesCoordinates(xCoordArr, yCoordArr, this.n);

        for(int i=0; i< this.n; i++){
            
            vec2 fiSpring = new vec2(0,0);


            int j = (i+1) % this.n; //next immediate neighbor
            int l = (i+this.n-1)%this.n;//prev immediate neighbor


            double springLenNext = calcCurrSpringLen(this.stateVec[2*i], this.stateVec[2*j]);
            double springLenPrev = calcCurrSpringLen(this.stateVec[2*i], this.stateVec[2*l]);


            vec2 nHatNext = calcUnitVecAlongSpring(this.stateVec[2*i], this.stateVec[2*j]);
            vec2 nHatPrev = calcUnitVecAlongSpring(this.stateVec[2*i], this.stateVec[2*l]);
            this.cInner = 0.9;
            this.cSurface = 0.5;
            fiSpring.addSelf(calcSpringForceVector(this.allSpringsEquilibriumLen[i][j], springLenNext, this.k, nHatNext, this.stateVec[2*i+1], this.stateVec[2*l+1], this.cSurface));
            fiSpring.addSelf(calcSpringForceVector(this.allSpringsEquilibriumLen[i][l], springLenPrev, this.k, nHatPrev, this.stateVec[2*i+1], this.stateVec[2*j+1], this.cSurface));


            for (int loopVar = 0; loopVar < this.n; loopVar++) {
                if (loopVar == j || loopVar == l || loopVar == i) continue; // skip immediate neighbors
                
                double l_eqbm = this.allSpringsEquilibriumLen[i][loopVar];
                double l_curr = calcCurrSpringLen(this.stateVec[2*i], this.stateVec[2*loopVar]);
                
                
                vec2 nHat = calcUnitVecAlongSpring(this.stateVec[2*i], this.stateVec[2*loopVar]);
                vec2 vel_i = this.stateVec[2*i + 1];
                vec2 vel_j = this.stateVec[2*loopVar + 1];
                

//                vec2 fij = calcSpringForceVector(l_eqbm, l_curr, this.k, nHat, vel_i, vel_j, 0.0);
                
                vec2 fij = calcSpringForceVector(l_eqbm, l_curr, this.k, nHat, vel_i, vel_j, this.cInner);
                fiSpring.addSelf(fij);



            }
            fiSpring.addSelf(this.g);
            this.acclrnVec[i].assign(fiSpring.scalerMult(1/this.m));

            this.acclrnVec[i].addSelf(this.calcAreaConsvForceOnThisNode(i));



        }
            


    }           

    public void getRateVec(vec2[] stateVec, vec2[] rateVec){
        if(odeSolver.getRateCounter()==1){
            this.computeAcceleration();
        }

        for(int i=0;i<this.n;i++){
            rateVec[2*i].assign(this.stateVec[2*i+1]);
            rateVec[2*i+1].assign(this.acclrnVec[i]);
            //this.stateVec[2*i+1].subSelf(vcm);
        }
        rateVec[this.stateVec.length-1].assign(1.0,1.0);
    }
           
           
           
    public void draw(DrawingPanel panel, Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        String timestep = "timestep=" + String.valueOf(this.stateVec[this.stateVec.length-1].x);
        g2.setColor(dataPlot1.get(3));
        g2.drawString(timestep, 20,10);
        g2.setColor(Color.BLACK);
        //g2.setColor(metro.get(2));

        int rectWidth = panel.getWidth()-panel.xToPix(0.1);
       
        int rectHeight = panel.getHeight() / 50; // Adjust height relative to panel size
        g2.fillRect(panel.xToPix(-3), panel.yToPix(-3), rectWidth, rectHeight);


        //drawing surface nodes and connecting springs
        for(int i=0; i<this.n; i++){
            int xipix = panel.xToPix(this.stateVec[2*i].x);
            int yipix = panel.yToPix(this.stateVec[2*i].y);
            /*for(int j = 0; j<this.innerSpringsEquilibriumLen[i].length;j++){
                int xjpix = panel.xToPix(this.innerSpringsEquilibriumLen[i][j]);
                int yjpix = panel.yToPix(this.innerSpringsEquilibriumLen[i][j]);

                g2.setColor(Color.GREEN);
                g2.drawLine(xipix, yipix, xjpix, yjpix);

            }*/


            for(int j=i+1;j<this.n;j++){
                int next = (i+1)%this.n;
                int prev = (i-1+this.n)%this.n;
                if(j!= next && j != prev){ //j>i so that one spring between a given node pair is drawn only once
                    int xjpix = panel.xToPix(this.stateVec[2*j].x);
                    int yjpix = panel.yToPix(this.stateVec[2*j].y);
                    
                    //g2.setColor(Color.GREEN);
                    g2.setColor(dataPlot1.get(7));
                    g2.drawLine(xipix, yipix, xjpix, yjpix);

                }
        }

            int k =(i+1)%this.n;
            int xkpix = panel.xToPix(this.stateVec[2*k].x);
            int ykpix = panel.yToPix(this.stateVec[2*k].y);

            //g2.setColor(Color.BLACK);
            g2.setColor(dataPlot1.get(6));
            g2.drawLine(xipix, yipix, xkpix, ykpix);

            //g2.setColor(Color.RED);
            g2.setColor(dataPlot1.get(3));
            g2.fillOval(xipix-pixRadius, yipix-pixRadius, 2*pixRadius, 2*pixRadius);

            if(displayForceVectors){
                int xPix = panel.xToPix(this.stateVec[2*i].x);
                int yPix = panel.yToPix(this.stateVec[2*i].y);

                double fx = this.acclrnVec[i].x;
                double fy = this.acclrnVec[i].y;

                // Scale the vector for visibility
                double scale = Math.sqrt(fx*fx+fy*fy)*0.01;
                int fxPix = panel.xToPix(this.stateVec[2*i].x + fx * scale);
                int fyPix = panel.yToPix(this.stateVec[2*i].y + fy * scale);
                //g2.setColor(Color.BLUE);
                g2.setColor(kanagawa.get(1));
                g2.drawLine(xPix, yPix, fxPix, fyPix);
            }
        }
    }
}
