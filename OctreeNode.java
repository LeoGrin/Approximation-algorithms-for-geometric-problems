
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Jcg.geometry.PointCloud_3;
import Jcg.geometry.Point_3;
import java.util.LinkedList;

/**
 * A class for representing a node of an Octree
 * 
 * @author Luca Castelli Aleardi, Ecole Polytechnique
 * @version december 2018
 */
public class OctreeNode {
	public int level;
	public OctreeNode[] children=null;
	public OctreeNode father;
	public Point_3 p; //point stored in a leaf or the middle of the box if the node isn't a leaf
	public double a; // length of the side of the cube
	public double force; // force for graph layout
        List<Point_3> test= new LinkedList<>();
	
	/**
	 * Create the octree for storing an input point cloud
	 */
	public OctreeNode(List<Point_3> points, Point_3 p, OctreeNode father, double a, int level) {
		int i;
		Point_3 new_center;
		this.father = father;
		this.p = p;
		this.a = a;
		this.level = level;
                this.test = points;

		/**
		 * If the node is a leaf, set the point stored
		 */
		if (points.size() <= 1){
			if (points.size() == 0)
				this.p = null;
			else
				this.p = points.get(0);
		}
		else{
			/**
			 * Compute the points going in each new cube
			 */
			children = new OctreeNode[8];
			ArrayList<Point_3>[] children_points = new ArrayList[8];

			i = 0;
			for (int add_0=-1; add_0 <= 1; add_0 = add_0 + 2) {
				for (int add_1 = -1; add_1 <= 1; add_1 = add_1 + 2) {
					for (int add_2 = -1; add_2 <= 1; add_2 = add_2 + 2) {
                                                children_points[i] = new ArrayList<>();
						for (Point_3 point : points) {
							if ((point.x - p.x) * add_0 >= 0 && (point.y - p.y) * add_1 >=0 &&(point.z - p.z) * add_2 >=0)
								children_points[i].add(point);
						}
						i++;
					}

				}
			}

			/**
			 * Initialize each children
			 */
			i = 0;
			for (int add_0=-1; add_0 <= 1; add_0 = add_0 + 2){
				for (int add_1=-1; add_1 <= 1; add_1 = add_1 + 2){
					for (int add_2=-1; add_2 <= 1; add_2 = add_2 + 2){
						new_center = new Point_3(p.x + add_0 * (a / 4), p.y + add_1 * (a / 4), p.z + add_2 * (a / 4)); //compute the center of the new node
						children[i] = new OctreeNode(children_points[i], new_center , this, a / 2, level + 1);
						i++;
					}
				}

			}


		}




	}
	
	/**
	 * Add a node into the OctreeNode
	 */
	public void add(Point_3 p) {
		;
	}

	public boolean hasExactlyOnePoint(){
		return this.children == null && p!=null;
	}
        @Override
	public String toString(){
            return "l: "+level+" px: "+Integer.toString((int)(10*p.x))+" a: "+a;
        }
}
