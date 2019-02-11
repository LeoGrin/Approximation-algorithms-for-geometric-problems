
import Jcg.geometry.Point_3;
import Jcg.geometry.PointCloud_3;

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

    public Octree(Point_3[] points) {
        /**
         * Compute the bounding box
         */
        ArrayList<Point_3> points_list = new ArrayList<>(points.length);
        for (Point_3 p : points) {
            points_list.add(p);
        }
        PointCloud_3 pointCloud = new PointCloud_3(points_list);
        Point_3[] cube = pointCloud.boundingBox();

        /**
         * Compute the center and the side length of the bounding box
         */
        Number coefficient[] = new Number[2]; // we want the mean of the cube, so we take the mean of the bounding box. 
        Arrays.fill(coefficient, 0.5);
        Point_3 center = Point_3.linearCombination(cube, coefficient);
        double a = Math.max(Math.max(cube[1].x - cube[0].x, cube[1].y - cube[0].y), cube[1].z - cube[0].z) * 1.000000001;

        
        /**
         * Initialize the root node
         */
        root = new OctreeNode(points_list, center, null, a, 0,0);

    }

    // compute the smallest radius of a centered sphere containing a given set of points in a cube
   static double calc_a(ArrayList<Point_3> points_list,Point_3 p) {
       if(points_list.size()<2) return 0;
        PointCloud_3 pointCloud = new PointCloud_3(points_list);
        Point_3[] cube = pointCloud.boundingBox();

        /**
         * Compute the center and the side length of the bounding box
         */
       // return Math.max(Math.max(cube[1].x - cube[0].x, cube[1].y - cube[0].y), cube[1].z - cube[0].z) * 1.000000001;
       return 2*Math.max(Math.max(Math.max(cube[1].x - p.x,p.x - cube[0].x), Math.max(cube[1].y-p.y,p.y-cube[0].y)), Math.max(cube[1].z-p.z,p.z-cube[0].z)) * 1.000000001;
    }

   
   // compute the center of the bounding box of a given set of points
  static  Point_3 calc_p(ArrayList<Point_3> points_list) {
      if(points_list.isEmpty()) return null;
        PointCloud_3 pointCloud = new PointCloud_3(points_list);
        Point_3[] cube = pointCloud.boundingBox();
        Number coefficient[] = new Number[2]; // we want the mean of the cube, so we take the mean of the bounding box. But which form has the bounding box ???
        Arrays.fill(coefficient, 0.5);
        return Point_3.linearCombination(cube, coefficient);
    }
}
