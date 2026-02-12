package utils;
//import org.opensourcephysics.numerics;

public interface ODE2D {
    vec2[] getStateVec();
    void getRateVec(vec2[] state, vec2[] rate);

}
