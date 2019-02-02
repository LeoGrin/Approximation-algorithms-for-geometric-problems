

import Jcg.geometry.Point_3;
import Jcg.geometry.Vector_3;
import jdg.graph.AdjacencyListGraph;
import jdg.graph.Node;
import jdg.layout.Layout;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * A class implementing the Fruchterman and Reingold method with fast approximation of repulsive forces
 * using a WSPD
 * 
 * @author Luca Castelli Aleardi, Ecole Polytechnique
 * @version december 2018
 */
public class FastFR91Layout extends Layout {
	// parameters of the algorithm by Fruchterman and Reingold
	public double k; // natural spring length
	public double area; // area of the drawing (width times height)
	public double C; // step
	public double temperature; // initial temperature
	public double minTemperature; // minimal temperature (strictly positive)
	public double coolingConstant; // constant term: the temperature decreases linearly at each iteration
	public boolean useCooling; // say whether performing simulated annealing
	
	public int iterationCount=0; // count the number of performed iterations
	private int countRepulsive=0; // count the number of computed repulsive forces (to measure time performances)
	
	private double accumulated_time = 0;
	
	/**
	 * Initialize the parameters of the force-directed layout
	 * 
	 *  @param g  input graph to draw
	 *  @param w  width of the drawing area
	 *  @param h  height of the drawing area
	 *  @param C  step length
	 */
	public FastFR91Layout(AdjacencyListGraph g, double w, double h) {

		System.out.print("Initializing force-directed method: Fruchterman-Reingold 91...");
		if(g==null) {
			System.out.println("Input graph not defined");
			System.exit(0);
		}
		this.g=g;
		int N=g.sizeVertices();

		// set the parameters of the algorithm FR91
		this.C=5.;
		this.w=w;
		this.h=h;
		this.area=w*h;
		this.k=C*Math.sqrt(area/N);
		this.temperature=w/2.; // the temperature is a fraction of the width of the drawing area
		this.minTemperature=0.1;
		this.coolingConstant=0.97;

		System.out.println("done ("+N+" nodes)");
		//System.out.println("k="+k+" - temperature="+temperature);
		System.out.println(this.toString());
	}
	
	/**
	 * Compute the (intensity of the) attractive force between two nodes at a given distance
	 * 
	 * @param distance  distance between two nodes
	 */	
	public double attractiveForce(double distance) {
		return (distance*distance)/k;
	}
	
	/**
	 * Compute the (intensity of the) repulsive force between two nodes at a given distance
	 * 
	 * @param distance  distance between two nodes
	 */	
	public double repulsiveForce(double distance) {
		countRepulsive++;
		return (k*k)/distance;
	}


	/**
	 * Compute the displacement of vertex 'v', due to the attractive forces of its neighbors
	 *
	 * @param v  the vertex to which attractive forces are applied
	 * @return 'disp' a 3d vector storing the displacement of vertex 'u'
	 */
	private Vector_3 computeAttractiveForce(Node v) {
		Vector_3 delta;
		double norm;
		Vector_3 disp = new Vector_3(0,0,0);
		for (Node u : g.getNeighbors(v)){
			delta= new Vector_3(v.p, u.p);  // compute delat=u-v
//			System.out.println("delta = " + delta);
			norm=Math.sqrt((double) delta.squaredLength());  //compute norm of delta
			if (norm!=0){
				disp=disp.sum(delta.multiplyByScalar(attractiveForce(norm)/norm));  //compute displacement of v due to u
//				System.out.println("disp = " + disp);

			}
		}
		return disp;
	}


	/**
	 * Compute, for each pair in the WSPD, compute the displacement due to repulsive forces
	 *
	 * @return an octree with each node storing the force applied to itself by the nodes paired with itself in the wspd
	 */
	private OctreeGraph computeAllRepulsiveForcesWSPD() {

		//Compute Octree from the points
		OctreeGraph T = new OctreeGraph(g);
		//Compute wspd from octree
		List<OctreeNodeGraph[]> wspd = new WSPDGraph(T,1).getWSPD(); // which s ??



		//Compute the forces between each node of the wspd only

		Vector_3 delta;
		double norm;
		Vector_3 displacement = new Vector_3(0,0,0);
		for (OctreeNodeGraph[] pair:wspd){
			delta= new Vector_3(pair[0].barycenter, pair[1].barycenter);  // compute delat=u-v
			norm=Math.sqrt((double) delta.squaredLength());  //compute norm of delta

//			System.out.println("delta = " + delta);


			if (norm!=0){
				displacement=delta.multiplyByScalar(repulsiveForce(norm)/norm); //compute displacement of pair[1] due to pair[0]
//				System.out.println("displacement = " + displacement);
			}
			pair[1].force = pair[1].force.sum(displacement.multiplyByScalar(pair[0].n_points));
		}
		return T;
	}


	/**
	 * Compute the final repulsive force for each node by summing the force of a node to its children in the Octree (We do a BFS)
	 *At the same time, compute the attractive forces for each leaf (so we pass through the tree only one time for both summing and computing the attractive forces)
	 * @Return nothing, just update the octree T by updating the parameter "force" of each node
	 */
	private void computeAllForcesfromTree(OctreeGraph T) {
		OctreeNodeGraph tree_node;
		Node graph_node;
		LinkedList<OctreeNodeGraph> queue = new LinkedList<OctreeNodeGraph>(); // for the BFS
		queue.add(T.root);
		while (queue.size() > 0){

			tree_node = queue.removeFirst();
			// sum the node's force to its children's
			if (tree_node.children != null){
				for (OctreeNodeGraph u:tree_node.children){
					u.force = u.force.sum(tree_node.force);
					queue.addLast(u);
				}
			}
			else if (tree_node.p != null){
				graph_node = tree_node.graph_node;
				// compute attractive force from the node stored in the leaf
				tree_node.force = tree_node.force.sum(computeAttractiveForce(graph_node));

			}
		}
	}

	/**
	 * Move the graph nodes according to the forces stored in the nodes.
	 * We do a BFS to access the leaf of the tree, which store as parameters both the graph node and the force we should apply to it
	 * @Return nothing
	 */
	private void moveGraphFromTree(OctreeGraph T) {
		OctreeNodeGraph tree_node;
		Node graph_node;
		double norm;

		LinkedList<OctreeNodeGraph> queue = new LinkedList<OctreeNodeGraph>(); // for the BFS
		queue.add(T.root);

		// BFS though the octree to access the force from the tree and the graph node associated with the force
		while (queue.size() > 0){

			tree_node = queue.removeFirst();
			if (tree_node.children != null){
				for (OctreeNodeGraph u:tree_node.children){
					queue.addLast(u);
				}
			}
			else if (tree_node.p != null){
				graph_node = tree_node.graph_node;
				// Normalize the force with the temperature
				norm = Math.sqrt((double) tree_node.force.squaredLength());  // norm of the force on graph_node
				// Move the graph node
				if (norm != 0) {
					graph_node.setPoint(graph_node.p.sum(tree_node.force.multiplyByScalar(Math.min(temperature, norm) / norm))); //modify coordinates of u in accordance with computed forces
				}
			}
		}
	}







	/**
	 * Perform one iteration of the Force-Directed algorithm.
	 * Positions of vertices are updated according to their mutual attractive and repulsive forces.
	 * 
	 * Repulsive forces are approximated using the WSPD decomposition
	 */	
	public void computeLayout() {
		System.out.print("Performing iteration (fast FR91): "+this.iterationCount);
		long startTime=System.nanoTime(), endTime; // for evaluating time performances
		
		// ---------- Complete this function ---
		// make use of the WSPD to approximate repulsive forces

		// Compute the octree associated with the graph nodes positions and compute the repulsive forces for each pair
		OctreeGraph T = computeAllRepulsiveForcesWSPD();
		// Sum these forces along to children and at the same time compute the attractive forces
		computeAllForcesfromTree(T);
		// Move the graph nodes
		moveGraphFromTree(T);




		// evaluate time performances
    	endTime=System.nanoTime();
        double duration=(double)(endTime-startTime)/1000000000.;
        accumulated_time += duration;
        System.out.println("iteration "+this.iterationCount+" done ("+duration+" seconds, accumulated time=" + accumulated_time + ")");

		this.cooling(); // update temperature
		
		this.iterationCount++; // increase counter (to count the number of performed iterations)
	}
	
	/**
	 * Cooling system: the temperature decreases linearly at each iteration
	 * 
	 * Remark: the temperature is assumed to remain strictly positive (>=minTemperature)
	 */	
	protected void cooling() {
		this.temperature=Math.max(this.temperature*coolingConstant, minTemperature);
		//this.temperature=Math.max(this.temperature-coolingConstant, minTemperature); // variant
	}
	
	public String toString() {
		String result="fast implementation of the force-directed algorihm: Fruchterman Reingold\n";
		result=result+"\t area= "+w+" x "+h+"\n";
		result=result+"\t k= "+this.k+"\n";
		result=result+"\t C= "+this.C+"\n";
		result=result+"\t initial temperature= "+this.temperature+"\n";
		result=result+"\t minimal temperature= "+this.minTemperature+"\n";
		result=result+"\t cooling constant= "+this.coolingConstant+"\n";
		
		return result;
	}

}
