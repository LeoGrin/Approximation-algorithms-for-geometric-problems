
import Jcg.geometry.Point_2;
import Jcg.geometry.Point_3;
import Jcg.geometry.Vector_3;
import Jcg.geometry.PointCloud_3;
import jdg.graph.AdjacencyListGraph;
import jdg.graph.Node;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A class for representing an Octree
 *
 * @author Luca Castelli Aleardi, Ecole Polytechnique
 * @version december 2018
 */
public class OctreeGraph {

    public OctreeNodeGraph root;

    public OctreeGraph(AdjacencyListGraph g) {
        /**
         * Compute the bounding box
         */
        ArrayList<Point_3> points_list = new ArrayList<>(g.sizeVertices());
        ArrayList<Node> nodes = new ArrayList<>(g.sizeVertices());

        for (Node u:g.vertices) {
            points_list.add(u.p);
            nodes.add(u);
        }


        PointCloud_3 pointCloud = new PointCloud_3(points_list);
        System.out.println();
        System.out.println("nb of points = "+ pointCloud.size());

        // compute bounding box in dim 2
        Point_3[] cube = new Point_3[]{new Point_3(pointCloud.min(0).getX(), pointCloud.min(1).getY(),0), new Point_3(pointCloud.max(0).getX(), pointCloud.max(1).getY(), 0)};



        /**
         * Compute the center and the side length of the bounding box
         */
        Number coefficient[] = new Number[2]; // we want the mean of the cube, so we take the mean of the bounding box. But which form has the bounding box ???
        Arrays.fill(coefficient, 0.5);
        Point_3 center = Point_3.linearCombination(cube, coefficient);
        double a = Math.max(Math.max(cube[1].x - cube[0].x, cube[1].y - cube[0].y), cube[1].z - cube[0].z) * 1.000000001;

        //(double)pointCloud.min(0).distanceFrom(pointCloud.max(0)); //length of the side of the cube
        /**
         * Initialize the root node
         */
        root = new OctreeNodeGraph(nodes, center, null, a, 0,0);

    }

    static double calc_a(ArrayList<Point_3> points_list) {
        if(points_list.isEmpty()) return 0;
        PointCloud_3 pointCloud = new PointCloud_3(points_list);
        Point_3[] cube = pointCloud.boundingBox();

        /**
         * Compute the center and the side length of the bounding box
         */
        return Math.max(Math.max(cube[1].x - cube[0].x, cube[1].y - cube[0].y), cube[1].z - cube[0].z) * 1.000000001;
    }

    static  Point_3 calc_p(ArrayList<Point_3> points_list) {
        if(points_list.isEmpty()) return null;
        PointCloud_3 pointCloud = new PointCloud_3(points_list);
        Point_3[] cube = pointCloud.boundingBox();
        Number coefficient[] = new Number[2]; // we want the mean of the cube, so we take the mean of the bounding box. But which form has the bounding box ???
        Arrays.fill(coefficient, 0.5);
        return Point_3.linearCombination(cube, coefficient);
    }
}
