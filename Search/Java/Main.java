import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class Main {
	
	/* Comparator for priority queue in Uniform Cost Search */
	private static class StateComparator implements Comparator<Triplet>{ 
        public int compare(Triplet s1, Triplet s2) { 
            if (s1.getCost() > s2.getCost()) 
                return 1; 
            else if (s1.getCost() < s2.getCost()) 
                return -1; 
            return 0; 
       } 
    } 
	
	public static void main(String[] args) {
		//Load map from args
		String path = "Maps/" + args[0];
		Map map = Map.readFromFile(path);
		Problem problem = new Problem(map);
		
		//Breadth first search
		BFS(problem);
		
		//Uniform cost search
		UCS(problem);
	}
	
	/* Performs BFS of state space 
	 * @param problem loaded from map
	 * @return void */
	public static void BFS(Problem problem) {
		//Start timer
		long startTime = System.currentTimeMillis();
		
		//initialize queue of triplets containing state, path cost, and path length
		Queue<Triplet> queue = new LinkedList<Triplet>();
		Triplet initial = new Triplet(problem.startState(), 0.0, 0);
		queue.add(initial);
		
		//keep track of nodes exapnded during search
		int nodesExpanded = 0;
		
		//HashMap of visited states for constant search. Value performs no purpose.
		HashMap<State,Integer> visited = new HashMap<State, Integer>();
		
		while (queue.size() > 0) {
			Triplet newOption = queue.remove();
			Triplet goal = null;
			//visit state
			goal = visitBFS(problem, newOption, queue, visited);
			nodesExpanded++;
			
			//print results if goal state found
			if (goal != null) {
				//end timer
				long endTime = System.currentTimeMillis();
				long duration = endTime - startTime;
				
				//print information contained in triplet associated with goal state
				printPathInfo(goal, "BFS", nodesExpanded, duration);
				return;
			}
		}
		
		//indicate that there is an error if goal state was not found
		printPathInfo(null, "BFS", -1, (long) -1);
	}
	
	/* Add adjacent states of current state to queue. If adj state is a goal, return associated triplet.
	 * @param problem, triplet associated with current state, queue of triplets for bfs, HashMap of visited states
	 * @return triplet assoicated with goal state if found. null otherwise.
	 */
	public static Triplet visitBFS(Problem problem, Triplet curr, Queue<Triplet> queue, HashMap<State,Integer> visited) {
		State currState = curr.getState();
		visited.put(currState, 1);
		
		//get adjacent states
		List<State> actions = problem.actions(currState);
		for (State s : actions) {
			double cost = curr.getCost() + problem.cost(currState, s);
			int pathLen = curr.getPathLen() + 1; 
			Triplet next = new Triplet(s, cost, pathLen);
			//return triplet associated with adj state if adj state is a goal
			if (problem.goal(s)) return next;
			//add triplet if not visited and not on the frontier
			if (!visited.containsKey(s) && !queue.contains(s)) queue.add(next);
		}
		
		return null;
	}
	
	/* Performs UCS of state space 
	 * @param problem loaded from map
	 * @return void */
	public static void UCS(Problem problem) {
		//start timer
		long startTime = System.currentTimeMillis();
		
		//priority queue ordered by increasing cost
		Queue<Triplet> queue = new PriorityQueue<Triplet>(11, new StateComparator()); 
		Triplet initial = new Triplet(problem.startState(), 0.0, 0);
		queue.add(initial);
		int nodesExpanded = 0;
		
		// If state not contained in the HashMap, state not seen. if value is null, the key state has been visited. 
		//If value is a triplet, the key state is on the frontier.
		HashMap<State,Triplet> stateInfo = new HashMap<State, Triplet>();
		
		while (queue.size() > 0) {
			// get state with minimum cost path
			Triplet newOption = queue.remove();
			
			//if state is a goal state
			if (problem.goal(newOption.getState())) {
				//end timer
				long endTime = System.currentTimeMillis();
				long duration = endTime - startTime;
				//print information contained in triplet associated with goal state
				printPathInfo(newOption, "UCS", nodesExpanded, duration);
				return;
			}
			// visit state
			visitUCS(problem, newOption, queue, stateInfo);
			nodesExpanded++;
		}
		
		//indicate that there is an error if goal state not found
		printPathInfo(null, "UCS", -1, (long) -1);
		
	}
	
	/* Add adjacent states of current state to queue. If already on frontier, add to queue if cost is less.
	 * @param problem, triplet associated with current state, queue of triplets for bfs, HashMap of visited states
	 * @return triplet assoicated with goal state if found. null otherwise.
	 */
	public static void visitUCS(Problem problem, Triplet curr, Queue<Triplet> queue, HashMap<State,Triplet> stateInfo) {
		State currState = curr.getState();
		stateInfo.put(currState, null);
		
		//get adjacent states
		List<State> actions = problem.actions(currState);
		for (State s : actions) {
			//do not add new state to queue if it has been visited (we do not have negative paths)
			if (!stateInfo.containsKey(s) || stateInfo.get(s) != null) {
				double cost = curr.getCost() + problem.cost(currState, s);
				int pathLen = curr.getPathLen() + 1; 
				Triplet next = new Triplet(s, cost, pathLen);
				//If state has never been seen, add its triplet to the queue
				if (!stateInfo.containsKey(s)) {
					queue.add(next);
					stateInfo.put(s, next);
				}
				//If state is on the frontier but path cost is less, add triplet to queue
				else {
					Triplet frontierState = stateInfo.get(s);
					if (cost < frontierState.getCost()) {
						queue.remove(frontierState);
						queue.add(next);
						stateInfo.put(s, next);
					}
				}
			}
			
		}
	}
	
	/*
	 * Print path length, path cost, latitude and longitude of state, nodes expanded, and time spent searching
	 * @param Triplet goal state found, String search algorithm used, int nodes expanded in search, long duration
	 * @return void
	 */
	public static void printPathInfo(Triplet finalState, String searchType, int nodesExpanded, long duration) {
		if (finalState == null) System.out.println("Error: no goal state found with " + searchType + "!");
		else {
			System.out.println(searchType + " path length: " + finalState.getPathLen());
			System.out.println(searchType + " path cost: " + finalState.getCost());
			System.out.println(searchType + " goal reached: (" + finalState.getState().lat + ", " + finalState.getState().lon + ")");
			System.out.println("Nodes expanded with " + searchType + ": " + nodesExpanded);
			System.out.println("Time spend with " + searchType + ": " + duration);
			System.out.println();
		}
	}
	
	

}
