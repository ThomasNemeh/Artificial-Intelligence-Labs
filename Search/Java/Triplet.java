public class Triplet {
	private final State state;
	private final double cost;
	private final int pathLen;

    public Triplet(State first, double second, int third) {
        this.state = first;
        this.cost = second;
        this.pathLen = third;
    }

    public State getState() { return state; }
    public double getCost() { return cost; }
	public int getPathLen() { return pathLen; }
	
}