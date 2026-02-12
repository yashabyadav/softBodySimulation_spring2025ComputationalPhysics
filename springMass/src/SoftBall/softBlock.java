package SoftBall;

import java.util.ArrayList;

import org.opensourcephysics.display.Drawable;

import utils.*;

public class softBlock implements Drawable, ODE2D, softBody{

        private vec2 topLeftCoordinate;
        private double subUnitSideLen;
        private int numSubUnitsAlongLen;
        private int numSubUnitsAlongWid;
        private double circumSpringConst;
        private double diagSpringConst;
        private double areaSpringConst;
        private double airDragFactor;
        private double circumSpringDamping;
        private double diagSpringDamping;

        private double numBeads;

        private double initArea = 0;
        private double currArea;

        ArrayList<double[][]> springList = new ArrayList<>();

        private int pixRadius;

        private vec2 g = new vec2(0,-9.8);
        private vec2[] stateVec;
        private vec2[] acclrnVec;
        Verlet2D odeSolver;

        int numX,numY;

        public softBlock(vec2 topLeftCoordinate, double subUnitSideLen, int numSubUnitsAlongLen,int numSubUnitsAlongWid, double circumSpringConst, double diagSpringConst, double areaSpringConst, double airDragFactor, double circumSpringDamping, double diagSpringDamping) {
            this.topLeftCoordinate = topLeftCoordinate;
            this.subUnitSideLen = subUnitSideLen;
            this.numSubUnitsAlongLen = numSubUnitsAlongLen;
            this.numSubUnitsAlongWid = numSubUnitsAlongWid;
            this.circumSpringConst = circumSpringConst;
            this.diagSpringConst = diagSpringConst;
            this.areaSpringConst = areaSpringConst;
            this.airDragFactor = airDragFactor;
            this.circumSpringDamping = circumSpringDamping;
            this.diagSpringDamping = diagSpringDamping;


            this.initialize();


        }
        public void initialize(){
            this.numBeads = (this.numSubUnitsAlongLen+1)*(this.numSubUnitsAlongWid+1);
            this.stateVec = new vec2[2*(int)this.numBeads];
            this.acclrnVec = new vec2[(int)this.numBeads];
            this.initializeStateVecObjectsWithZeros();
            this.initializeAcclrnVecObjectWithZeros();

            int tempIndex = 0;
            this.stateVec[0].assign(this.topLeftCoordinate);
            for(int i=0;i<(this.numSubUnitsAlongLen + 1);i++){
                for(int j=0; j<(this.numSubUnitsAlongWid + 1);j++){
                    vec2 pos = new vec2(
                        this.topLeftCoordinate.x + i * this.subUnitSideLen,
                        this.topLeftCoordinate.y - j * this.subUnitSideLen
                    );
                    this.stateVec[2 * tempIndex] = pos; //initial positions
                    this.stateVec[2 * tempIndex + 1] = new vec2(0,0); // initial velocities = 0
                    tempIndex++;
                }
            }

            this.numX = numSubUnitsAlongLen + 1;
            this.numY = numSubUnitsAlongWid + 1;

            
        }

        public void initializeStateVecObjectsWithZeros(){
            for(int i=0;i<2*(this.numBeads+1);i++){
                this.stateVec[i] = new vec2(0,0);
            }
        }

        private void initializeSprings(){
            int nx = this.numSubUnitsAlongLen;
            int ny = this.numSubUnitsAlongWid;
            for(int i = 0;i < nx; i++){
                for(int j = 0;j < ny; j++){
                    //corners
                    if(i != 0 && j != 0){
                        if( i != 0 || j != 0){
                            spring
                        }
                    }
                }
            }
        }
    }
