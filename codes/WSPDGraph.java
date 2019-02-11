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
 * Class to compute a WSPD but compatible with OctreeGraph and OctreeNodeGraph for graph layout computation
 */
public class WSPDGraph {

    // the result
    List<OctreeNodeGraph[]> listOfWSPD;
    int i = 0;

    WSPDGraph(OctreeGraph T, double s) {

        this.listOfWSPD = WSPD_rec(T.root, T.root, s, new LinkedList<OctreeNodeGraph[]>());
    }

    public List<OctreeNodeGraph[]> getWSPD() {
        System.out.println("i = "+i/1000);
        return listOfWSPD;
    }

    // auxiliary function
    public LinkedList<OctreeNodeGraph[]> WSPD_rec(OctreeNodeGraph u, OctreeNodeGraph v, double s, LinkedList<OctreeNodeGraph[]> l) {
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
            OctreeNodeGraph[] AB = {u, v};
            l.add(AB);
            return l;
            // go down the tree
        } else if (u.children != null) {

            for (OctreeNodeGraph child_u : u.children) {

                // use of a linkedlist in parameters to avoid concatenations
                l = WSPD_rec(child_u, v, s, l);
            }
            return l;
        } else {

            for (OctreeNodeGraph child_v : v.children) {

                // use of a linkedlist in parameters to avoid concatenations
                l = WSPD_rec(u, child_v, s, l);
            }
            return l;
        }
    }

    // returns the boolean : "u and v are well s separated"
    boolean sSeparated(OctreeNodeGraph u, OctreeNodeGraph v, double s) {

        if (u.hasExactlyOnePoint() && v.hasExactlyOnePoint()) {
            return true;
        }
        // the ball radius
        double r_u;
        double r_v;
        if (u.children == null) {
            r_v = (v.a / 2.0) * Math.sqrt(2.); //dimension 2
            r_u = 0;
        }
        else if (v.children == null){
            r_u = (u.a / 2.0) * Math.sqrt(2.);
            r_v = 0;
        }
        else{
            r_v = (v.a / 2.0) * Math.sqrt(2.);
            r_u = (u.a / 2.0) * Math.sqrt(2.);
        }

        return u.p.distanceFrom(v.p).doubleValue() - (r_u + r_v) > s *Math.max( r_u,r_v);

    }
}
