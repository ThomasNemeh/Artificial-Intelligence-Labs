/*
 * Program to perform BFS, UCS, A* search, and IDDFS on given map to find a goal state.
 * Author: Thomas Nemeh
 */
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class Main {
	
	/* Comparator for priority queue in Uniform Cost Search */
	private static class StateComparator implements Comparator<Triplet> { 
        public int compare(Triplet s1, Triplet s2) { 
            if (s1.getCost() > s2.getCost()) 
                return 1; 
            else if (s1.getCost() < s2.getCost()) 
                return -1; 
            return 0; 
       } 
    }
	
	/* Comparator for priority queue in A* search */
	private static class AStarComparator implements Comparator<AStar> {
		public int compare(AStar s1, AStar s2) { 
            if (s1.getCostEstimate() > s2.getCostEstimate()) 
                return 1; 
            else if (s1.getCostEstimate() < s2.getCostEstimate()) 
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
		
		//A* search
		ASS(problem);
		
		//Iterative deepening depth first search
		IDDFS(problem);
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
		//System.out.println(curr.lat + "    " + );
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
			if (!visited.containsKey(s)) {
				queue.add(next);
				visited.put(s, 1);
			}
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
	
	/* Performs A* search of state space 
	* @param problem loaded from map
	* @return void 
	*/
	public static void ASS(Problem problem) {
		//start timer
		long startTime = System.currentTimeMillis();
		
		//priority queue ordered by increasing f(n) value, where f(n) = path cost so far + distance to nearst goal estimate
		Queue<AStar> queue = new PriorityQueue<AStar>(11, new AStarComparator());
		State start = problem.startState();
		double costEstimate = nearestGoalDistance(start, problem);
		AStar initial = new AStar(problem.startState(), 0.0, 0, costEstimate);
		queue.add(initial);
		int nodesExpanded = 0;
		
		//If state not contained in the HashMap, state not seen. If in Hashmap, it can either be on the frontier or visited,
		//depending the value of the visited flag in the AStar object. 1 => visited. 0 => on frontier
		HashMap<State,AStar> stateInfo = new HashMap<State, AStar>();
		
		while (queue.size() > 0) {
			// get state with minimum cost path
			AStar newOption = queue.remove();
			
			//if state is a goal state
			if (problem.goal(newOption.getState())) {
				//end timer
				long endTime = System.currentTimeMillis();
				long duration = endTime - startTime;
				//print information contained in triplet associated with goal state
				printPathInfo(newOption, "A*", nodesExpanded, duration);
				return;
			}
			// visit state
			visitASS(problem, newOption, queue, stateInfo);
			nodesExpanded++;
		}
		
		//indicate that there is an error if goal state not found
		printPathInfo(null, "A*", -1, (long) -1);
	}
	
	/* Add adjacent states of current state to queue. If already on frontier, add to queue if cost estimate is less.
	 * @param problem, AStar associated with current state, queue of AStars for bfs, HashMap of visited and frontier states
	 * @return AStar associated with goal state if found. null otherwise.
	 */
	public static void visitASS(Problem problem, AStar curr, Queue<AStar> queue, HashMap<State,AStar> stateInfo) {
		State currState = curr.getState();
		AStar info = stateInfo.get(currState);
		if (info != null) {info.flagVisited();}
		
		//get adjacent states
		List<State> actions = problem.actions(currState);
		for (State s : actions) {
			double cost = curr.getCost() + problem.cost(currState, s);
			int pathLen = curr.getPathLen() + 1; 
			double costEst = nearestGoalDistance(s, problem) + cost;
			AStar next = new AStar(s, cost, pathLen, costEst);
			//If state has never been seen, add its triplet to the queue
			if (!stateInfo.containsKey(s)) {
				queue.add(next);
				stateInfo.put(s, next);
			}
			//If state is on the frontier or visited but path cost estimate is less, add AStar to queue
			else {
				AStar seenBefore = stateInfo.get(s);
				if (seenBefore.getVisited() && costEst < seenBefore.getCostEstimate()) {
					queue.add(next);
					stateInfo.put(s, next);
				}
				else if (costEst < seenBefore.getCostEstimate()) {
					queue.remove(seenBefore);
					queue.add(next);
					stateInfo.put(s, next);
				}
			}
			
		}
	}
	
	/*
	 * gets the Euclidean distance from the current state to the nearest goal state
	 * @param current state, Problem problem
	 * @return Euclidean distance to nearest goal state
	 */
	public static double nearestGoalDistance(State state, Problem problem) {
		List<State> goals = problem.goalStates();
		double min = Integer.MAX_VALUE;
		for (State s : goals) {
			double distance = Math.sqrt(Math.pow(state.lat-s.lat, 2) + Math.pow(state.lon-s.lon, 2));
			if (distance < min) {
				min = distance;
			}
		}
		
		return min;
	}
	
	/*
	 * Iterative depth first search. 
	 * @param problem to explore
	 * @return voide.
	 */
	public static void IDDFS(Problem problem) {
		int[] nodesExpanded = new int[]{0};
		
		//start timer
		long startTime = System.currentTimeMillis();
		
		Triplet initial = new Triplet(problem.startState(), 0.0, 0);
		
		//keep track of visited nodes during iteration of for loop
		HashMap visited = new HashMap<State, Boolean>();
		
		//continually increment maximum depth, but put to avoid infinite loop if goal state does not exist
		for (int i = 0; i <= Integer.MAX_VALUE; i++) {
			Triplet goal = DLS(initial, problem, i, nodesExpanded, visited);
			if (goal != null) {
				long endTime = System.currentTimeMillis();
				long duration = endTime - startTime;
				printPathInfo(goal, "IDDFS", nodesExpanded[0], duration);
				return;
			}
			
			//empty visited table
			visited.clear();
		}
		
		//indicate that there is an error if goal state not found
		printPathInfo(null, "IDDFS", -1, (long) -1);
	}
	
	/*
	 * perform depth first search to given depth.
	 * @param first node to visit, problem, depth to search to, number of nodes expanded, Hashmap of visited nodes during current iteration
	 * @return Triplet associated with goal state. Null otherwise.
	 */
	public static Triplet DLS(Triplet curr, Problem problem, int limit, int[] nodesExpanded, HashMap<State, Boolean> visited) {
		State currState = curr.getState();
		visited.put(currState, true);
		//System.out.println(currState.lat + "    " + currState.lon);
		nodesExpanded[0]++;
		//return current Triplet if it contains a goal state
		if (problem.goal(currState)) {
			return curr;
		}
		
		//return null if we have passed the given depth
		if (limit <= 0) {
			return null;
		}
		//System.out.println("limit" + limit);
		limit--;
		
		//get adjacent states
		List<State> actions = problem.actions(currState);
		//System.out.println("size" + actions.size());
		for (State s : actions) {
			if (!visited.containsKey(s)) {
				//System.out.println(currState.equals(s));
				double cost = curr.getCost() + problem.cost(currState, s);
				int pathLen = curr.getPathLen() + 1; 
				Triplet next = new Triplet(s, cost, pathLen);
				//System.out.println(limit);
				Triplet goal = DLS(next, problem, limit, nodesExpanded, visited);
				if (goal != null) {
					return goal;
				}
			}
		}
		
		return null;
	}
	
	/*
	 * Print path length, path cost, latitude and longitude of the goal found, nodes expanded, and time spent searching
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
