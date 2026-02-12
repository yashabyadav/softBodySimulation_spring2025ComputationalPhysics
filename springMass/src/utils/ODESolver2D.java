package utils;

public interface ODESolver2D {
    void initialize(double stepSize);

    double step();

    void setStepSize(double stepSize);

    double getStepSize();
    
}

