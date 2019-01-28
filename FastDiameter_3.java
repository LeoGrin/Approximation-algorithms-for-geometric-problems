import Jcg.geometry.*;

import java.util.List;

/**
 * Implementation of a fast algorithm for computing an approximation of the diameter of a 3D point cloud,
 * based on WSP.
 *
 * @author Code by Luca Castelli Aleardi (INF421 2018, Ecole Polytechnique)
 *
 */
public class FastDiameter_3 implements Diameter_3 {
	
	/** approximation factor (for approximating the diameter) **/
	public double epsilon;
	
	public FastDiameter_3(double epsilon) {
		this.epsilon=epsilon;
	}
	
	/**
	 * Compute a farthest pair of points realizing an (1-epsilon)-approximation of the diameter of a set of points in 3D space
	 * 
	 * @param points the set of points
	 * @return a pair of farthest points
	 */
    public Point_3[] findFarthestPair(Point_3[] points) {
		if(points.length<2) throw new Error("Error: too few points");
		System.out.print("Computing farthest pair: fast computation...");
		double time = System.currentTimeMillis();

		double diameter = 0;
		Point_3[] final_pair = new Point_3[2];
		double distance;

		Octree tree = new Octree(points);
		double s = 8 / epsilon; // we need a 4/epsilon wspd
		List<OctreeNode[]> wspd = new WSPD(tree,s).getWSPD();
		System.out.println(System.currentTimeMillis() - time);
		for (OctreeNode[] octree_pair : wspd){
			distance = octree_pair[0].p.distanceFrom(octree_pair[1].p).doubleValue();
			if (distance > diameter)
				diameter = distance;
				final_pair[0] = octree_pair[0].p;
				final_pair[1] = octree_pair[1].p;
		}

		System.out.println("found diameter " + diameter);
		System.out.println("in time" + (System.currentTimeMillis() - time));

		System.out.println("done");

		return final_pair;

    }

}