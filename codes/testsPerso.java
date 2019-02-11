
import Jcg.geometry.Point_3;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Marc
 */
public class testsPerso {
    public static void main(String[] args){
    int n = 10000;
    Point_3[] ptAleatoires = new Point_3[n];
    for (int i = 0; i < ptAleatoires.length; i++) {
             ptAleatoires[i] = new Point_3(100*Math.random(),100.*Math.random(), 100*Math.random());
            
    }
    System.out.println("Slow :");
    long tpsDep = System.currentTimeMillis();
    Point_3[] resSlow = new SlowClosestPair_3().findClosestPair(ptAleatoires);
    long tpsFin = System.currentTimeMillis();
    System.out.println("dMin : " + resSlow[0].distanceFrom(resSlow[1]));
    System.out.println("Temps : "+(tpsFin-tpsDep) + " ms");
    System.out.println("");
    System.out.println("Fast :");
    tpsDep = System.currentTimeMillis();
    Point_3[] resFast = new FastClosestPair_3().findClosestPair(ptAleatoires);
    tpsFin = System.currentTimeMillis();
    System.out.println("dMin : " + resFast[0].distanceFrom(resFast[1]));
    System.out.println("Temps : "+(tpsFin-tpsDep) +" ms");


    System.out.println("Slow :");
    tpsDep = System.currentTimeMillis();
    resSlow = new SlowDiameter_3().findFarthestPair(ptAleatoires);
    tpsFin = System.currentTimeMillis();
    System.out.println("dMin : " + resSlow[0].distanceFrom(resSlow[1]));
    System.out.println("Temps : "+(tpsFin-tpsDep) + " ms");
    System.out.println("");
    System.out.println("Fast :");
    tpsDep = System.currentTimeMillis();
    resFast = new FastDiameter_3(0.7).findFarthestPair(ptAleatoires);
    tpsFin = System.currentTimeMillis();
    System.out.println("dMin : " + resFast[0].distanceFrom(resFast[1]));
    System.out.println("Temps : "+(tpsFin-tpsDep) +" ms");
    }
}
