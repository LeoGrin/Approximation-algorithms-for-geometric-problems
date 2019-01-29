/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Jcg.geometry.Point_3;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Marc
 */
public class WSPD {

    // the result
    List<OctreeNode[]> listOfWSPD;
    int i = 0;

    WSPD(Octree T, double s) {

        this.listOfWSPD = WSPD_rec(T.root, T.root, s, new LinkedList<OctreeNode[]>());
    }

    public List<OctreeNode[]> getWSPD() {
        System.out.println("i = "+i/1000);
        return listOfWSPD;
    }

    // auxiliary function
    public LinkedList<OctreeNode[]> WSPD_rec(OctreeNode u, OctreeNode v, double s, LinkedList<OctreeNode[]> l) {
        i++;
        // insure that the level of u is <= the level of v
        if (u.level > v.level) {
            return WSPD_rec(v, u, s, l);
        }

        // if there is no point or if u and v are the same leaf
        if (u.p == null || v.p == null || (u.children == null && v.children == null && u.p.equals(v.p))) {
            return l;
        } 
        // if u and v are well s separated add (u,v)       
        else if (sSeparated(u, v, s)) {
            //System.out.println(u.level + " l: "+v.level+"                      Pu: "+Arrays.toString(u.test.toArray()) +" Pv: "+Arrays.toString(v.test.toArray()) );
            //System.out.println("OK");
            OctreeNode[] AB = {u, v};
            l.add(AB);
            return l;
            // go down the tree
        } else if (u.children != null) {

            for (OctreeNode child_u : u.children) {

                // use of a linkedlist in parameters to avoid concatenations
                l = WSPD_rec(child_u, v, s, l);
            }
            return l;
        } else {

            for (OctreeNode child_v : v.children) {

                // use of a linkedlist in parameters to avoid concatenations
                l = WSPD_rec(u, child_v, s, l);
            }
            return l;
        }
    }

    // returns the boolean : "u and v are well s separated"
    boolean sSeparated(OctreeNode u, OctreeNode v, double s) {

        if (u.hasExactlyOnePoint() && v.hasExactlyOnePoint()) {
            return true;
        }
        // the ball radius
        double r_u;
        double r_v;
        if (u.children == null) {
            r_v = (v.a / 2.0) * Math.sqrt(3.);
            r_u = 0;
        }
        else if (v.children == null){
            r_u = (u.a / 2.0) * Math.sqrt(3.);
            r_v = 0;
        }
        else{
            r_v = (v.a / 2.0) * Math.sqrt(3.);
            r_u = (u.a / 2.0) * Math.sqrt(3.);
        }

        return u.p.distanceFrom(v.p).doubleValue() - (r_u + r_v) > s * r_u;

    }
}
