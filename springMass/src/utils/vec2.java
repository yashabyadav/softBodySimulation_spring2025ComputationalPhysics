package utils;

public class vec2 {
    public double x;
    public double y;
    public vec2(){
        this.x = 0;
        this.y = 0;
    }
    public vec2(double X, double Y){
        this.x = X;
        this.y = Y;
    }
    public vec2 add(vec2 other){
        return new vec2(this.x+other.x, this.y+other.y);
    }
    public void addSelf(vec2 other){
        this.x += other.x;
        this.y += other.y;
    }
    public vec2 addAll(vec2... others) {
        for (vec2 other : others) {
            this.x += other.x;
            this.y += other.y;
        }
        return this;
    }
    public vec2 sub(vec2 other){
        return new vec2(this.x-other.x, this.y-other.y);
    }
    public void subSelf(vec2 other){
        this.x -= other.x;
        this.y -= other.y;
    }
    public double dot(vec2 other){
        return (this.x*other.x)+(this.y*other.y);
    }
    public double mag(){
        return Math.sqrt(this.x*this.x + this.y*this.y);
    }
    //public double distanceFormula(vec2 a, vec2 b){
    public static double distanceFormula(vec2 a, vec2 b){
        return Math.sqrt(Math.pow((a.x-b.x),2)+Math.pow((a.y-b.y),2));
    }
    public vec2 scalerMult(double scaler){

        vec2 c = new vec2();
        c.x = scaler*this.x;
        c.y = scaler*this.y;
        return c;

    }
    public static vec2 unitVec2(vec2 a, vec2 b){
        /**
         * @return a unit vector from a to b
         */
        vec2 c = new vec2();
        //c.x = a.x-b.x;
        //c.y = a.y-b.y;
        c.x = b.x - a.x;
        c.y = b.y - a.y;
        double mag = Math.sqrt(c.x*c.x+c.y*c.y);
        if (mag!=0){
            c.x /= mag;
            c.y /= mag;
        }
        return c;


    }
    public void assign(vec2 other){
        this.x = other.x;
        this.y = other.y;
    }
    public void assign(double other_x, double other_y){
        this.x = other_x;
        this.y = other_y;
    }
    public vec2 calcNormalToLineJoining(vec2 p1, vec2 p2){
        double lenp1p2 = distanceFormula(p1, p2);
        vec2 Np1P2 = new vec2(-(p2.y-p1.y),(p2.x-p1.x));
        return Np1P2.scalerMult(lenp1p2);
    }
    public static double areaOfTriangleBetweenThreePoints(vec2 p1, vec2 p2, vec2 p3){
        return 0.5*(p1.x*(p2.y-p3.y)+p2.x*(p3.y-p1.y)+p3.x*(p1.y-p2.y));
    }
}
