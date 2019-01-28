

import Jcg.geometry.*;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of a fast algorithm for computing the closest pair,
 * based on WSP.
 *
 * @author Code by Luca Castelli Aleardi (INF421 2018, Ecole Polytechnique)
 *
 */
public class FastClosestPair_3 implements ClosestPair_3 {
	
	/**
	 * Compute the closest pair of a set of points in 3D space
	 * 
	 * @param points the set of points
	 * @return a pair of closest points
	 */
    public Point_3[] findClosestPair(Point_3[] points) {
		if(points.length<2) throw new Error("Error: too few points");
		
                Octree T = new Octree(points);
                List<OctreeNode[]> wspd = new WSPD(T,2.2).getWSPD();
                System.out.println(wspd.size());
                //System.out.println(Arrays.deepToString(wspd.toArray()));
                Point_3[] closestPair =  {points[0],points[1]};
                double dMin = (double) points[0].distanceFrom(points[1]);
                for(OctreeNode[] nodePair : wspd){
                    //System.out.println(nodePair[0]);
                    if(nodePair[0].hasExactlyOnePoint() && nodePair[1].hasExactlyOnePoint()){
                        if((double)nodePair[0].p.distanceFrom(nodePair[1].p)<dMin){
                            closestPair[0] = nodePair[0].p;
                            closestPair[1] = nodePair[1].p;
                            dMin = (double)closestPair[0].distanceFrom(closestPair[1]);
                            
                        }
                    }
                }
                return closestPair;
    }

}
