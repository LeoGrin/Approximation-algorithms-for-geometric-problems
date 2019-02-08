
import java.util.ArrayList;
import Jcg.geometry.Point_3;

/**
 * A class for representing a node of an Octree
 * 
 * @author Luca Castelli Aleardi, Ecole Polytechnique
 * @version december 2018
 */

public class OctreeNode {
    
    final static int COMPLET_NODE=1,COMPLET_LEAF=2,NON_COMPLET =0;
	public int level;
	public OctreeNode[] children=null;
	public OctreeNode father;
	public Point_3 p; //point stored in a leaf or the middle of the box if the node isn't a leaf
	public double a; // length of the side of the cube
	int label=-1;// to identify the node
        int complet = NON_COMPLET; // NON_COMPLET if we are not in the precomputation
        double aMin; // best radius
	/**
	 * Create the octree for storing an input point cloud
	 */
	public OctreeNode(ArrayList<Point_3> points, Point_3 p, OctreeNode father, double a, int level,int label) {
		int i;
		Point_3 new_center;
		this.father = father;
		this.p = p;
		this.a = a;
		this.level = level;
                this.label = label;
                
                // compute the best radius
                double rMax = 0;
                for(Point_3 p1 :points){
                    if((double)p1.distanceFrom(p)>rMax){
                        rMax = (double)p1.distanceFrom(p);
                    }
        }
                this.aMin = (rMax*2.000000001)/Math.sqrt(3.0);
                
          
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
						children[i] = new OctreeNode(children_points[i], new_center , this, a / 2, level + 1,8*label+i+1);
						i++;
					}
				}

			}

 
		}




	}
	
        
        public boolean hasExactlyOnePoint(){
            return (this.children == null && p!=null) || complet==this.COMPLET_LEAF;
        }
        
        @Override
	public String toString(){
            if(p==null) return null;
            return "l: "+level+" px: "+Integer.toString((int)(10*p.x))+" a: "+(int)(a*10)/10.+" label: "+Integer.toString(label);
        }
        
}
