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
    int i = 0, j = 0, k1 = 0;
    ArrayList<LinkedList<int[]>> lComplet = new ArrayList<>();

    WSPD2(Octree T, double s) {
        lComplet.add(null);
        this.listOfWSPD = WSPD_rec(T.root, T.root, s, new LinkedList<OctreeNode[]>());
    }

    public List<OctreeNode[]> getWSPD() {
        System.out.println("i = " + i);

        //  System.out.println("j = "+j);
        //   System.out.println("nb niv2 = "+k);
        return listOfWSPD;
    }

    // auxiliary function
    public LinkedList<OctreeNode[]> WSPD_rec(OctreeNode u, OctreeNode v, double s, LinkedList<OctreeNode[]> l) {
        i++;

        // insure that the level of u is <= the level of v
        if (u.level > v.level) {
            return WSPD_rec(v, u, s, l);
        }
        /*  if(u.level==2 && v.level == 2 ){
           // System.out.println("lu "+u.level+" lv "+v.level);
            //System.out.println("up "+u.p+"vp "+v.p.toString());
            //System.out.println(sSeparated(u, v, s));
            k++;
            if( sSeparated(u, v, s))j++;
        }*/
        // if there is no point or if u and v are the same leaf
        if (u.p == null || v.p == null || (u.children == null && v.children == null && u.p.equals(v.p))) {
            return l;
        } // if u and v are well s separated add (u,v)       
        else if (sSeparated(u, v, s)) {
            //System.out.println(u.level + " l: "+v.level+"                      Pu: "+Arrays.toString(u.test.toArray()) +" Pv: "+Arrays.toString(v.test.toArray()) );
            //System.out.println("OK");
            OctreeNode[] AB = {u, v};
            l.add(AB);
            return l;
            // go down the tree
        } else if (u.level == v.level && u.p.equals(v.p)) {
            ArrayList<OctreeNode> labels = new ArrayList<>();
            labels.add(u);
            int k0 = -1;
            boolean continuer = true;
            while (continuer) {
                int m = labels.size();
                k0++;

                for (int i = m - (int) Math.pow(8, k0); i < m; i++) {
                    OctreeNode currentNode = labels.get(i);
                    if (currentNode.children == null) {
                        continuer = false;
                        i = m;
                    } else {
                        for (OctreeNode child : currentNode.children) {
                            labels.add(child);
                        }
                    }
                }

            }
            LinkedList<int[]> c = complet(k0, s);

            for (int[] pair : c) {
                OctreeNode w = labels.get(pair[0]);
                OctreeNode z = labels.get(pair[1]);
                if (w.level == u.level + k0 && z.level == u.level + k0) {
                    l = WSPD_rec(w, z, s, l);

                } else {
                    OctreeNode[] AB = {w, z};
                    l.add(AB);

                }
            }
            return l;
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
        if (v.p == null || u.p == null) {
            return false;
        }
        if (u.hasExactlyOnePoint() && v.hasExactlyOnePoint()) {
            return true;
        }
        // the ball radius
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

        return u.p.distanceFrom(v.p).doubleValue() - (r_u + r_v) > s * Math.max(r_u,r_v);
        /*
        double r;
        if (u.children == null) {
            r = (v.a / 2.0) * Math.sqrt(3.);
        } else {
            r = (u.a / 2.0) * Math.sqrt(3.);
        }

        return u.p.distanceFrom(v.p).doubleValue() > r * (s + 2);*/

    }

    LinkedList<int[]> complet(int k, double s) {
        if (lComplet.size() > k) {
            return lComplet.get(k);
        }
        for (int l = 1; l < k; l++) {
            complet(l, s);
        }
        double a = 100;
        ArrayList<Point_3> points = new ArrayList<>((int) Math.pow(8, k));
        int puissk = (int) Math.pow(2, k);

        for (int l = 0; l < 3 * puissk; l++) {
            for (int m = 0; m < 3 * puissk; m++) {
                for (int n = 0; n < 3 * puissk; n++) {
                    points.add(new Point_3((double) l * a / (double) (4 * puissk), (double) m * a / (double) (4 * puissk), (double) n * a / (double) (4 * puissk)));

                }

            }

        }
        System.out.print("k=   " + k + "...");
        Octree T = new Octree(points.toArray(new Point_3[points.size()]));
        for (int l = 0; l < k; l++) {

        }
        reduceTree(T.root, k);
        lComplet.add(WSPD_complet(k, T.root, T.root, s, new LinkedList<OctreeNode[]>()));
        System.out.println("Done");
        return lComplet.get(k);

    }

    public LinkedList<int[]> WSPD_complet(int k, OctreeNode u, OctreeNode v, double s, LinkedList<OctreeNode[]> l) {
        for (OctreeNode child_u : u.children) {
            l = WSPD_rec(child_u, v, s, l);
        }

        LinkedList<int[]> lLabels = new LinkedList<int[]>();
        for (OctreeNode[] pair : l) {
            
            int[] a = {pair[0].label, pair[1].label};
            lLabels.add(a);
        }
        for (int leaf = ((int) Math.pow(8, k) - 1) / 7; leaf < ((int) Math.pow(8, k + 1) - 1) / 7; leaf++) {
            int[] a = {leaf, leaf};
            lLabels.add(a);
        }
        return lLabels;
    }/*        
        // insure that the level of u is <= the level of v
        if (u.level > v.level) {
            return WSPD_complet(k,v, u, s, l);
        }
        
        
        if(u.level == k && v.level == k ){

             int[] AB = {u.label, v.label};
            l.add(AB);
            return l;

        }

        
        // if u and v are well s separated add (u,v)       
        else if (sSeparated(u, v, s)) {
            //System.out.println(u.level + " l: "+v.level+"                      Pu: "+Arrays.toString(u.test.toArray()) +" Pv: "+Arrays.toString(v.test.toArray()) );
            //System.out.println("OK");
            int[] AB = {u.label, v.label};
            l.add(AB);
            return l;
            
            // go down the tree
        } else if (u.level < k) {

            for (OctreeNode child_u : u.children) {

                // use of a linkedlist in parameters to avoid concatenations
                l = WSPD_complet(k,child_u, v, s, l);
            }
            return l;
        } 
        return null;
        }
     */
    void reduceTree(OctreeNode o, int k) {
        if (k == 0) {
            o.children = null;
            return;
        }
        for (OctreeNode on : o.children) {
            reduceTree(on, k - 1);
        }
    }
}
