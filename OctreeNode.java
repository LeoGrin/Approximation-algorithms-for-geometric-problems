import java.util.List;

import Jcg.geometry.Point_3;

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
	public Point_3 p; //point stored in a leaf
	
	/**
	 * Create the octree for storing an input point cloud
	 */
	public OctreeNode(List<Point_3> points) {
		throw new Error("To be completed");
	}
	
	/**
	 * Add a node into the OctreeNode
	 */
	public void add(Point_3 p) {
		throw new Error("To be completed");
	}
		
}
