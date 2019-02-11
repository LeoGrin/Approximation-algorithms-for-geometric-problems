
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Jcg.geometry.PointCloud_3;
import Jcg.geometry.Point_3;
import Jcg.geometry.Point_3.*;


import Jcg.geometry.Vector_3;
import jdg.graph.AdjacencyListGraph;
import jdg.graph.Node;

import java.util.LinkedList;

/**
 * A class for representing a node of an Octree for graph layout computation (nodes store forces, barycenter of subtree and graph vertices)
 *
 * @author Luca Castelli Aleardi, Ecole Polytechnique
 * @version december 2018
 */
public class OctreeNodeGraph {
    public int level;
    public OctreeNodeGraph[] children=null;
    public OctreeNodeGraph father;
    public Point_3 p; //point stored in a leaf or the middle of the box if the node isn't a leaf
    public Point_3 barycenter;
    public Node graph_node;
    public double a; // length of the side of the cube
    public int n_points;
    public Vector_3 force; // force for the graph layout computation
    List<Point_3> test= new LinkedList<>();
    int label=-1;
    /**
     * Create the quadree for storing an input graph
     */
    public OctreeNodeGraph(ArrayList<Node> nodes, Point_3 p, OctreeNodeGraph father, double a, int level, int label) {
        int i;
        Point_3 new_center;
        this.father = father;
        this.p = p;//Octree.calc_p(points);
        this.a = a;//Octree.calc_a(points);
        this.level = level;
        this.label = label;
        this.n_points = nodes.size();
        this.force = new Vector_3(0, 0, 0);



        /**
         * If the node is a leaf, set the point stored
         */
        if (nodes.size() <= 1){
            if (nodes.size() == 0) {
                this.p = null;
                this.graph_node = null;
                this.barycenter = null;
            }
            else {
                this.p = nodes.get(0).p;
                this.graph_node = nodes.get(0);
                this.barycenter = nodes.get(0).p;
            }
        }
        else{
            /**
             * Compute the points going in each new square
             */
            children = new OctreeNodeGraph[4];
            Point_3[] children_barycenters = new Point_3[4];
            int[] barycenter_weights = new int[4];
            ArrayList<Node>[] children_nodes = new ArrayList[4];

            i = 0;
            for (int add_0=-1; add_0 <= 1; add_0 = add_0 + 2) {
                for (int add_1 = -1; add_1 <= 1; add_1 = add_1 + 2) {
                        children_nodes[i] = new ArrayList<>();
                        for (Node u : nodes) {
                            if ((u.p.x - this.p.x) * add_0 >= 0 && (u.p.y - this.p.y) * add_1 >=0)
                                children_nodes[i].add(u);
                        }
                        i++;
                    }

                }



            /**
             * Initialize each children
             */
            i = 0;
            for (int add_0=-1; add_0 <= 1; add_0 = add_0 + 2) {
                for (int add_1 = -1; add_1 <= 1; add_1 = add_1 + 2) {
                    new_center = new Point_3(p.x + add_0 * (a / 4), p.y + add_1 * (a / 4), 0); //compute the center of the new node
                    children[i] = new OctreeNodeGraph(children_nodes[i], new_center, this, a / 2, level + 1, 8 * label + i + 1);
                    children_barycenters[i] = children[i].barycenter; // used to compute this.barycenter as the weighted barycenter of its children barycenters
                    barycenter_weights[i] = children[i].n_points; //the weights are the size of each subtree (nb of points)
                    i++;
                }
            }



            /**
             * Compute the barycenter of the node from the barycenters of its children
             */
            if (nb_null(children_barycenters) < children_barycenters.length) {

                Point_3[] children_barycenters_without_null = new Point_3[children_barycenters.length - nb_null(children_barycenters)];
                int[] barycenter_weights_without_null = new int[children_barycenters.length - nb_null(children_barycenters)];

                int index = 0;
                for (int k = 0; k < children_barycenters.length; k++) {
                    if (children_barycenters[k] != null) {
                        children_barycenters_without_null[index] = children_barycenters[k];
                        barycenter_weights_without_null[index] = barycenter_weights[k];
                        index ++;
                    }
                }



                this.barycenter = this.barycenter.linearCombination(children_barycenters_without_null, divideByScalar(barycenter_weights_without_null, sum(barycenter_weights_without_null)));
            }
            else{
                this.barycenter = null;
            }

        }





    }

    /**
     * Add a node into the OctreeNode
     */
    public void add(Point_3 p) {
        ;
    }

    public int sum(int[] ar){
        int s = 0;
        for (int n:ar)
            s += n;
        return s;
    }

    public Number[] divideByScalar(int[] ar, int scal){
        Number[] res = new Number[ar.length];
        for (int i=0; i<ar.length; i++)
            res[i] = (float)ar[i] / scal;
        return res;
    }

    public int nb_null(Point_3[] ar){
        // compute the nb of null in an array
        int res = 0;
        for (Point_3 point : ar){
            if (point == null)
                res ++;
        }
        return res;
    }

    public boolean hasExactlyOnePoint(){
        return this.children == null && p!=null;
    }
    @Override
    public String toString(){
        if(p==null) return null;
        return "l: "+level+" px: "+Integer.toString((int)(10*p.x))+" a: "+(int)(a*10)/10.+" label: "+Integer.toString(label);
    }

}
