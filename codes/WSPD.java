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
    
    // the precomputation is stored here
    ArrayList<LinkedList<int[]>> lComplet = new ArrayList<>();

    //main function
    WSPD(Octree T, double s) {
        lComplet.add(null);
        this.listOfWSPD = WSPD_rec(T.root, T.root, s, new LinkedList<OctreeNode[]>());
    }

    public List<OctreeNode[]> getWSPD() {
        return listOfWSPD;
    }

    // auxiliary function
    public LinkedList<OctreeNode[]> WSPD_rec(OctreeNode u, OctreeNode v, double s, LinkedList<OctreeNode[]> l) {

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
            OctreeNode[] AB = {u, v};
            l.add(AB);
            return l;
            
            // the precomputation optimization
        } else if ( u.level == v.level && u.p.equals(v.p) && u.children!=null) {
            ArrayList<OctreeNode> labels = new ArrayList<>();
            labels.add(u);
            int k0 = -1;
            
            //counts how many generations have no leaf
            boolean continuer = true;
            while (continuer) {
                int m = labels.size();
                k0++;
                for (int i = m - (int) Math.pow(8, k0); i < m; i++) {
                    OctreeNode currentNode = labels.get(i);       
                    if (currentNode.children==null ) {
                        continuer = false;
                        i = m;
                    } else {
                        labels.addAll(Arrays.asList(currentNode.children));
                    }
                }
            }
            
            //apply the precomputation to the considered node
            k0=Math.max(1,Math.min(k0-1, lComplet.size()-1));
            LinkedList<int[]> c = new LinkedList(complet(k0, s));
            while(!c.isEmpty()){
                int[] pair = c.pop();
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
            
           /*
           //    go down the tree
           */
            
           //if u is not a leaf 
        } else if (u.children != null) {
            for (OctreeNode child_u : u.children) {
                // use of a linkedlist in parameters to avoid concatenations
                l = WSPD_rec(child_u, v, s, l);
            }
            return l;
            
            //if u is a leaf
        } else {
            for (OctreeNode child_v : v.children) {
                l = WSPD_rec(u, child_v, s, l);
            }
            return l;
        }
    }

    
    
    // returns the boolean : "u and v are well s separated"
    boolean sSeparated(OctreeNode u, OctreeNode v, double s) {
        //special case : both are leaves
        if (v.p == null || u.p == null) {
            return false;
        }
        if (u.hasExactlyOnePoint() && v.hasExactlyOnePoint()) {
            return true;
        }
        
        /*
        // the ball radius
        */
        double r_u;
        double r_v;
        double au = u.a, av=v.a;
        //if we are not inside the precomputation, use the best radius optimization
        if(u.complet==OctreeNode.NON_COMPLET) au = u.aMin;
        if(v.complet==OctreeNode.NON_COMPLET) av = v.aMin;
        
        
        if (u.children == null && !(u.complet==OctreeNode.COMPLET_LEAF)) {
            r_v = (av / 2.0) * Math.sqrt(3.);
            r_u = 0;
        }
        else if (v.children == null && !(v.complet==OctreeNode.COMPLET_LEAF)){
            r_u = (au / 2.0) * Math.sqrt(3.);
            r_v = 0;
        }
        else{
            r_v = (av / 2.0) * Math.sqrt(3.);
            r_u = (au / 2.0) * Math.sqrt(3.);
        }

        return u.p.distanceFrom(v.p).doubleValue() - (r_u + r_v) > s * Math.max(r_u,r_v);
    }
    
    
    /*
    // the precomputation
    */

    LinkedList<int[]> complet(int k, double s) {
        // if already computed
        if (lComplet.size() > k) {
            return lComplet.get(k);
        }
        // check if it is computed until level k-1
        for (int l = 1; l < k; l++) {
            complet(l, s);
        }
        
        // compute for level k using level 1,...,k-1
        
        // make a set of points dense enough to have a complete octree on k levels
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
        
        
        Octree T = new Octree(points.toArray(new Point_3[points.size()]));
        // destroy any node of level > k
        reduceTree(T.root, k);
        //compute the level k
        lComplet.add(WSPD_complet(k, T.root, T.root, s, new LinkedList<OctreeNode[]>()));

        return lComplet.get(k);

    }

    
    /*
    // the precomputation of level k
    */
    public LinkedList<int[]> WSPD_complet(int k, OctreeNode u, OctreeNode v, double s, LinkedList<OctreeNode[]> l) {
        //go down one level to avoid self reference
        for (OctreeNode child_u : u.children) {
            l = WSPD_rec(child_u, v, s, l);
        }
        
        //add the missing pairs
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
    }
    
       
     
    void reduceTree(OctreeNode o, int k) {
        if (k == 0) {
            o.children = null;
            o.complet = OctreeNode.COMPLET_LEAF;
            return;
        }
        for (OctreeNode on : o.children) {
            on.complet = OctreeNode.COMPLET_NODE;
            reduceTree(on, k - 1);
        }
    }
}
