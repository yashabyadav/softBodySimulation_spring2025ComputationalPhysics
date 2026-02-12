package testingThrowaway;

import utils.*;
public class testing {

    //vec2[] v1 = new vec2[3];
    static vec2[] v1 = {new vec2(0,0), new vec2(0,0), new vec2(0,0)};
    public static void main(String args[]){
//        System.out.println("v1 len= "+v1.length);
        for(int i = 0; i<3; i++){
            vec2 v2 = new vec2(i,0);
            v1[i].assign(v2);
        }

    }


}
