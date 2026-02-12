package utils;

public class Verlet2D extends AbstractODESolver2D {
    private vec2[] rate1;  // acceleration at current position
    private vec2[] rate2;  // acceleration at updated position
    private int rateCounter = -1;

    public Verlet2D(ODE2D ode) {
        super(ode);
    }

    @Override
    public void initialize(double stepSize) {
        super.initialize(stepSize);
        int N = (ode.getStateVec().length-1) / 2;
        int totalVecs = 2*N+1;
        //int N = ode.getStateVec().length;
        rate1 = new vec2[totalVecs];
        rate2 = new vec2[totalVecs];
        for (int i = 0; i < totalVecs; i++) {
            rate1[i] = new vec2();
            rate2[i] = new vec2();
        }
        this.rateCounter = -1;
    }
    @Override
    public double step() {
        vec2[] state = ode.getStateVec();
        int N = (state.length - 1) / 2;

        // Compute full rateVec at current state
        rateCounter = 0;
        ode.getRateVec(state, rate1);

        double h = stepSize;
        double h2 = 0.5 * h * h;

        // Update positions: rᵢ ← rᵢ + h*vᵢ + (1/2)*h²*aᵢ
        for (int i = 0; i < N; i++) {
            state[2*i].x += h * rate1[2*i].x + h2 * rate1[2*i + 1].x;
            state[2*i].y += h * rate1[2*i].y + h2 * rate1[2*i + 1].y;
        }

        // Recalculate rateVec at new positions
        rateCounter = 1;
        ode.getRateVec(state, rate2);
        rateCounter = 2;

        // Update velocities: vᵢ ← vᵢ + (1/2)*h*(a₀ + a₁)
        for (int i = 0; i < N; i++) {
            state[2*i + 1].x += 0.5 * h * (rate1[2*i + 1].x + rate2[2*i + 1].x);
            state[2*i + 1].y += 0.5 * h * (rate1[2*i + 1].y + rate2[2*i + 1].y);
        }

        // Update time: t ← t + h (stored as vec2 {t, t})
        int timeIndex = 2 * N;
        state[timeIndex].x += h;
        state[timeIndex].y += h;

        return stepSize;
    }

    public int getRateCounter() {
        return rateCounter;
    }
}