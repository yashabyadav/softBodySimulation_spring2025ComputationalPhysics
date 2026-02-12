package SoftBall;
import utils.*;

public interface softBody{
    void intitialize();

    double calcCurrSpringLen(vec2 springNodeCoordinate1, vec2 springNodeCoordinate2);

    vec2 calcUnitVecAlongSpring(vec2 springNodeCoordinate1, vec2 springNodeCoordinatVec2);

    vec2 calcSpringForceVector(double eqbmSpringLen, double currSpringLen, double k, vec2 unitVecAlongSpring, vec2 vel_i, vec2 vel_j, double springDampingFactor);

    vec2 calcAreaConsvForceOnThisNode(int nodeIndex);

    void initializeStateVecObjectsWithZeros();

    void initializeAcclrnVecObjectWithZeros();

    void computeAcceleration();

}
