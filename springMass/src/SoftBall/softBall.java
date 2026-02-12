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


//import java.util.stream.IntStream;

public class softBall implements Drawable,ODE2D{
    public double x, y;
    vec2 center = new vec2(x,y);
    public int n; //number of beads and springs that make the surface of the ball
    public double R; //radius of the soft ball
    ArrayList<vec2> massList = new ArrayList<>();
    public double k; // spring constant for springs connecting the surface nodes
    public double k2 = 25.0; // spring constant for"springs" connecting surface nodes and the center of the bead
    public double cSurface;
    public double cCentSurf;
    public double[] springEquilibriumLen;

    double M = 1; //mass of the softball (center of mass coordinate's mass)
    double m = 1; //"mass" of the surface nodes
    vec2 currMassCoordinate;

    double A0 = 0;//initial (equilibrium) area of the soft body
    double A;
    double kArea = 50.0;//spring constant for area restoring force
    double dampingFactor;
    int pixRadius = 4;

    double dt;
    vec2 g = new vec2(0,-9.8);
    vec2[] stateVec;
    vec2[] acclrnVec;
    Verlet2D odeSolver;
    

    public softBall(vec2 centerCoordinateVec2, int numBeads, double springConstBlue, double springConstGreen, double springConstArea, double ballRadius, double airDragFactor, double surfaceSpringDampingConst, double centerToSurfaceSpringDampingConst){
        this.center = centerCoordinateVec2;
        this.n = numBeads;
        this.k = springConstBlue;
        this.k2 = springConstGreen;
        this.kArea = springConstArea;
        this.R = ballRadius;
        this.dampingFactor = airDragFactor;
        this.cSurface = surfaceSpringDampingConst;
        this.cCentSurf = centerToSurfaceSpringDampingConst;

        this.M = this.n;
        initializeSoftBall();   
        odeSolver = new Verlet2D(this);
        
    }
    public void initializeSoftBall(){
        System.out.println("statevec len at initialization = "+(2*(this.n+1)+1));
        this.stateVec = new vec2[2*(this.n+1)+1];//n position vectors for the surface nodes and 1 for the center of mass 
        //2 * (n+1), 2 because each particle has a (x,y) and (vx,vy) vec2
        this.acclrnVec = new vec2[this.n+1];
        this.initializeStateVecObjectsWithZeros();
        this.initializeAcclrnVecObjectsWithZeros();
        System.out.println("debug print from intitializeSoftBall() method");
        for(int i=0; i<(this.n+1);i++){
            System.out.println("i= "+i);
            //adds "masses" at the circumference of the softbody. The
            //coordinates are calculated by how many beads are input during object
            //declaration.
            if(i==0){
                currMassCoordinate = new vec2();
                currMassCoordinate.assign(this.center);
            }
            else if(i!=0){
            currMassCoordinate = new vec2(
                (R*Math.cos((i-1)*2*Math.PI/n)), //x coordinate of the ith node from center
                (R*Math.sin((i-1)*2*Math.PI/n)) //y coordinate of the ith node from the center
                );
            }
            massList.add(center.add(currMassCoordinate));
            this.stateVec[2*i].assign(currMassCoordinate);
        }
        //calculating the equilibrium len of the springs. Since the masses
        //are uniformly distributed on the circumference at initialization,
        //the spring lenghts are also equal at initialization
        //this.springEquilibriumLen = (2*Math.PI*R/n);
        this.springEquilibriumLen = new double[this.n+1];
        for (int i=1;i<(this.n+1);i++){
            int j = (i % this.n) + 1;
            this.springEquilibriumLen[i] = calcCurrSpringLen(stateVec[2*i], stateVec[2*j]);
            this.A0 += vec2.areaOfTriangleBetweenThreePoints(this.stateVec[0], this.stateVec[2*i], this.stateVec[2*j]); //area of triangle joining center with surface nodes i and j
        }
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
        double delL = currSpringLen-eqbmSpringLen; //compression in the spring
        //return unitVecAlongSpring.scalerMult(delL);
        //not sure about the signs yet, will try to both positive and negative

        double relativeVelocity = (vel_i.x - vel_j.x) * unitVecAlongSpring.x + (vel_i.y - vel_j.y) * unitVecAlongSpring.y;

        double dampingTerm = springDampingFactor * relativeVelocity;
        return unitVecAlongSpring.scalerMult(k * delL - dampingTerm);
        //return unitVecAlongSpring.scalerMult(delL*k);
    }
    private vec2 calcAreaConsvForceOnThisNode(int nodeIndex){
        int nNext = (nodeIndex == this.n ? 1 : nodeIndex + 1);
        int nPrev = (nodeIndex == 1 ? this.n : nodeIndex - 1);

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
    /*public void setVelocities(){
        vec2 vel = new vec2(0,0);
        for(int i=0;i<=this.n;i++){
            this.stateVec[2*i+1].assign(vel);        
        }

    }*/
    public void applyGroundCollision(double groundY, double bounceDamping, double friction) {
    for (int i = 1; i < (this.n+1); i++) {
        vec2 pos = stateVec[2*i];
        vec2 vel = stateVec[2*i+1];
        if (pos.y < groundY) {
            pos.y = groundY;
            if (vel.y < 0) vel.y *= -bounceDamping;
            vel.x *= friction; // friction in x direction to stop nodes from sliding in the horizontal direction due to the surface force
            }
        }
    }

/*    private void computeAccelerationForNode(int i){
        if (i == 0) {
        // center node forces
        for (int j = 1; j <= this.n; j++) {
            double l_center_j = calcCurrSpringLen(this.stateVec[0], this.stateVec[2*j]);
            vec2 n_center_j = calcUnitVecAlongSpring(this.stateVec[0], this.stateVec[2*j]);

            vec2 centerForce = calcSpringForceVector(this.R, l_center_j, this.k2,
                                                     n_center_j, this.stateVec[1], this.stateVec[2*j+1], this.cCentSurf);
            this.acclrnVec[0] = this.acclrnVec[0].add(centerForce);
        }
        // Add gravity
        this.acclrnVec[0] = this.acclrnVec[0].add(this.g);
        this.acclrnVec[0] = this.acclrnVec[0].scalerMult(1.0/this.M);
    } else {
        // surface node forces
        int j = (i == this.n ? 1 : i + 1); // next node (wrap-around)
        int l = (i == 1 ? this.n : i - 1); // previous node (wrap-around)

        double springLenNext   = calcCurrSpringLen(this.stateVec[2*i], this.stateVec[2*j]);
        double springLenPrev   = calcCurrSpringLen(this.stateVec[2*i], this.stateVec[2*l]);
        double springLenCenter = calcCurrSpringLen(this.stateVec[2*i], this.stateVec[0]);

        vec2 nHatNext   = calcUnitVecAlongSpring(this.stateVec[2*i], this.stateVec[2*j]);
        vec2 nHatPrev   = calcUnitVecAlongSpring(this.stateVec[2*l], this.stateVec[2*i]);
        vec2 nHatCenter = calcUnitVecAlongSpring(this.stateVec[0], this.stateVec[2*i]);

        vec2 springForceNext = calcSpringForceVector(this.springEquilibriumLen[i], springLenNext, this.k,nHatNext, this.stateVec[2*i+1], this.stateVec[2*j+1], this.cSurface);
        vec2 springForcePrev = calcSpringForceVector(this.springEquilibriumLen[l], springLenPrev, this.k,nHatPrev, this.stateVec[2*i+1], this.stateVec[2*l+1], this.cSurface);
        vec2 springForceCenter = calcSpringForceVector(this.R, springLenCenter, this.k2,nHatCenter, this.stateVec[2*i+1], this.stateVec[1], this.cCentSurf);

        vec2 fArea = calcAreaConsvForceOnThisNode(i);

        // total force accumulation
        this.acclrnVec[i] = this.acclrnVec[i]
                                .add(springForceNext)
                                .add(springForcePrev)
                                .add(springForceCenter)
                                .add(fArea)
                                .add(this.g)
                                .add(this.stateVec[2*i+1].scalerMult(-this.dampingFactor));
        this.acclrnVec[i] = this.acclrnVec[i].scalerMult(1.0/this.m);
        }
    }
*/
    public void computeAcceleration(){
        this.initializeAcclrnVecObjectsWithZeros();
        this.A = 0;
        for(int i=1; i<(this.n+1);i++){
            int j = (i % this.n) + 1;
            this.A += vec2.areaOfTriangleBetweenThreePoints(this.stateVec[0], this.stateVec[2*i], this.stateVec[2*j]);
        }
/*        IntStream.rangeClosed(0,this.n).parallel().forEach(i -> {
            computeAccelerationForNode(i);
        });*/
        for(int i = 0; i<this.acclrnVec.length; i++){
            if(i==0){//center of mass
                for(int j = 1; j<this.acclrnVec.length;j++){ //sum of spring forces from all the nodes on center of mass
                    double springLenNodeToCenter = calcCurrSpringLen(this.stateVec[0], this.stateVec[2*j]);
                    vec2 nHatNodeToCenter = calcUnitVecAlongSpring(this.stateVec[2*j], this.stateVec[0]);
                    vec2 acclnDueToJthSurfaceNode = calcSpringForceVector(this.R, springLenNodeToCenter,this.k2,nHatNodeToCenter, this.stateVec[1],this.stateVec[2*j+1], this.cCentSurf).scalerMult(1.0/M);

                    //this.acclrnVec[0] = this.acclrnVec[0].add(acclnDueToJthSurfaceNode);
                    this.acclrnVec[0].addSelf(acclnDueToJthSurfaceNode);
                }

                System.out.println("acclrnVec[0]="+this.acclrnVec[0].x+","+this.acclrnVec[0].y+" at time "+this.stateVec[this.stateVec.length-1].x);
            } else if(i!=0){
                //int j = (i+1) % this.n; //index of (i+1)th node
                //int l = (i+this.n-1) % this.n; //index of (i-1)th node
                //if (l==0) l=this.n; // so that l equals this.n (not 0) for i = 1
                int j = (i == this.n ? 1 : i+1);
                int l = (i == 1 ? this.n : i-1);
                //System.out.println("l="+l+",i="+i+",j="+j);
                double springLenNext = calcCurrSpringLen(this.stateVec[2*i], this.stateVec[2*j]);
                double springLenPrev = calcCurrSpringLen(this.stateVec[2*i], this.stateVec[2*l]);
                double springLenCenter = calcCurrSpringLen(this.stateVec[2*i], this.stateVec[0]);
                vec2 nHatNext = calcUnitVecAlongSpring(this.stateVec[2*i], this.stateVec[2*j]);
                vec2 nHatPrev = calcUnitVecAlongSpring(this.stateVec[2*l], this.stateVec[2*i]);
                vec2 nHatCenter = calcUnitVecAlongSpring(this.stateVec[0], this.stateVec[2*i]);
                vec2 springForceNext = calcSpringForceVector(this.springEquilibriumLen[i], springLenNext, this.k, nHatNext, this.stateVec[2*i+1], this.stateVec[2*j+1], this.cSurface);
                vec2 springForcePrev = calcSpringForceVector(this.springEquilibriumLen[l], springLenPrev, this.k, nHatPrev, this.stateVec[2*i+1], this.stateVec[2*l+1], this.cSurface);
                vec2 springForceCenter = calcSpringForceVector(this.R, springLenCenter, this.k2, nHatCenter, this.stateVec[2*i+1], this.stateVec[1], this.cCentSurf);
                this.acclrnVec[i] = this.acclrnVec[i].addAll(springForceNext, springForcePrev,springForceCenter);

                //this.acclrnVec[i] = this.acclrnVec[i].add(this.calcAreaConsvForceOnThisNode(i));
                this.acclrnVec[i].addSelf(this.calcAreaConsvForceOnThisNode(i));
            }
            //this.acclrnVec[i] = this.acclrnVec[i].add(g);
            //this.acclrnVec[i] = this.acclrnVec[i].add(this.stateVec[2*i+1].scalerMult(-this.dampingFactor));
            this.acclrnVec[i].addSelf(g);
            this.acclrnVec[i].addSelf(this.stateVec[2*i+1].scalerMult(-this.dampingFactor));
            //System.out.println("2 acclrnVec[0]="+this.acclrnVec[0].x+","+this.acclrnVec[0].y);
        }

    }           

    public void getRateVec(vec2[] stateVec, vec2[] rateVec){
        this.computeAcceleration();
        for(int i=0;i<(this.n+1);i++){
            rateVec[2*i].assign(this.stateVec[2*i+1]);
            rateVec[2*i+1].assign(this.acclrnVec[i]);
        }
        rateVec[this.stateVec.length-1].assign(1.0,1.0);
    }
           
           
           
    public void draw(DrawingPanel panel, Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.BLACK);
        int rectWidth = panel.getWidth()-200;
       
        int rectHeight = panel.getHeight() / 50; // Adjust height relative to panel size
        g2.drawRect(panel.xToPix(-3), panel.yToPix(-3), rectWidth, rectHeight);
        //drawing center of mass
        int xCenterPix = panel.xToPix(stateVec[0].x);
        int yCenterPix = panel.yToPix(stateVec[0].y);
        //drawing center of mass

        //drawing surface nodes and connecting springs
        for(int i=1; i<=n; i++){
            int xpix = panel.xToPix(stateVec[2*i].x);
            int ypix = panel.yToPix(stateVec[2*i].y);
            g2.setColor(Color.GREEN);
            g2.drawLine(xCenterPix, yCenterPix, xpix, ypix);

            int j = (i%n)+1;
            //System.out.println("i = "+i+",j = "+j);
            int xpix1 = panel.xToPix(stateVec[2*j].x);
            int ypix1 = panel.yToPix(stateVec[2*j].y);

            g2.setColor(Color.BLACK);
            g2.drawLine(xpix, ypix, xpix1, ypix1);

            g2.setColor(Color.RED);
            g2.fillOval(xpix-pixRadius, ypix-pixRadius, 2*pixRadius, 2*pixRadius);
        }
        //drawing surface nodes and connecting springs
            g2.setColor(Color.BLUE);
            g2.fillOval(xCenterPix-pixRadius, yCenterPix-pixRadius, 2*pixRadius, 2*pixRadius);
    }

}