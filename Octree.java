


import Jcg.geometry.Point_3;
import Jcg.geometry.Vector_3;
import Jcg.geometry.PointCloud_3;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A class for representing an Octree
 * 
 * @author Luca Castelli Aleardi, Ecole Polytechnique
 * @version december 2018
 */
public class Octree {
	public OctreeNode root;
	
	public Octree(Point_3[] points){
        /**
         * Compute the bounding box
         */
        ArrayList<Point_3> points_list = new ArrayList<>(points.length);
        for(Point_3 p : points){points_list.add(p);}
        PointCloud_3 pointCloud = new PointCloud_3(points_list);
        Point_3[] cube = pointCloud.boundingBox();

        /**
         * Compute the center and the side length of the bounding box
         */
        Number coefficient[] = new Number[2]; // we want the mean of the cube, so we take the mean of the bounding box. But which form has the bounding box ???
        Arrays.fill(coefficient, 0.5);
        Point_3 center = Point_3.linearCombination(cube, coefficient);
        double a = (double)pointCloud.min(0).distanceFrom(pointCloud.max(0)); //length of the side of the cube

        /**
         * Initialize the root node
         */
        root = new OctreeNode(Arrays.asList(points), center, null, a, 0);



    }
	
}
