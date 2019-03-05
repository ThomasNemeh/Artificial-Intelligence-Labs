/*
 * Class to store data associated with a state in order to aid A* search specifically.
 * Author: Thomas Nemeh
 */

public class AStar extends Triplet {
	private final double costEstimate;	//path cost from root + estimate distance to goal
	private boolean visited;

	//constructor
    public AStar(State first, double second, int third, double fourth) {
        super(first, second, third);
        this.costEstimate = fourth;
        visited = false;
    }
    
    //getter methods
	public double getCostEstimate() { return costEstimate; }
	public boolean getVisited() {return visited; }
	
	//setter methods
	public void flagVisited() {
		visited = true;
	}
	
	public void unflagVisited() {
		visited = false;
	}
	
}
