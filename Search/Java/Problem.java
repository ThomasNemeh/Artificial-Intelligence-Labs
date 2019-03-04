import java.util.*;

/**
 * The {@link Problem} class represents the problem model used by the agent
 * to find a path from the start state to a goal state in a given {@link Map}.
 *
 * @author Adam Eck
 * @date 02/20/2019
 */
public class Problem {
    /** The {@link Map} wrapped by this {@link Problem}. */
    public final Map map;

    /**
     * Construct a new {@link Problem}.
     *
     * @param map The {@link Map} used in this problem
     */
    public Problem(Map map) {
        this.map = map;
    }

    /**
     * Converts a {@link MapNode} to a {@link State}.
     *
     * @param node The {@link MapNode} to convert
     *
     * @return {@code node} as a {@link State}
     */
    private State convertToState(MapNode node) {
        return new State(node.lat, node.lon);
    }

    /**
     * Converts a {@link State} into its corresponding {@link MapNode}.
     *
     * @param state The {@link State} to convert
     *
     * @return {@code state} as a {@link MapNode}
     */
    private MapNode convertToNode(State state) {
        // make sure the node exists
        if (!map.nodeMap.containsKey(state.lat) || !map.nodeMap.get(state.lat).containsKey(state.lon)) {
            throw new IllegalArgumentException("The Map does not contain the state: ("
                    + state.lat + ", " + state.lon + ")");
        }

        return map.nodeMap.get(state.lat).get(state.lon);
    }

    /**
     * Returns the starting {@link State} of the {@link Problem}.
     *
     * @return The start {@link State}
     */
    public State startState() {
        return convertToState(map.startNode);
    }

    /**
     * Finds the set of all possible actions from a {@link State}.
     *
     * @param state The {@link State} to find actions for
     *
     * @return The possible locations to move to from {@code state}
     */
    public List<State> actions(State state) {
        List<State> actionList = new ArrayList<>();

        // look up the node for this state
        MapNode node = convertToNode(state);
        for (MapNode neighbor : node.neighbors) {
            actionList.add(convertToState(neighbor));
        }

        return actionList;
    }

    /**
     * Determines the next {@link State} of taking an action in a {@link State}.
     *
     * @param state The current {@link State}
     * @param action The chosen action
     *
     * @return The resulting next {@link State}
     */
    public State result(State state, State action) {
        MapNode node = convertToNode(state);
        MapNode nextNode = convertToNode(action);

        // make sure that action is a neighbor of state
        if (!node.neighbors.contains(nextNode)) {
            throw new IllegalArgumentException("Sorry, but that transition is not valid.");
        }

        return convertToState(nextNode);
    }

    /**
     * Determines the cost of taking an action in a {@link State}.
     *
     * @param state The {@link State}
     * @param action The action
     *
     * @return The cost of taking {@code action} in {@code state}
     */
    public double cost(State state, State action) {
        MapNode node = convertToNode(state);
        MapNode neighbor = convertToNode(action);

        // make sure that action is a neighbor of state
        if (!node.neighbors.contains(neighbor)) {
            throw new IllegalArgumentException("Sorry, but that transition is not valid.");
        }

        return node.costs.get(neighbor);
    }

    /**
     * Determines whether a given {@link State} is a goal state.
     *
     * @param state The {@link State} in question
     *
     * @return True if {@code state} is a goal state, else False
     */
    public boolean goal(State state) {
        MapNode node = convertToNode(state);
        return map.goalNodes.contains(node);
    }

    /**
     * Creates a list of all goal {@link State}s in the {@link Problem}.
     *
     * @return The list of possible goal {@link State}s
     */
    public List<State> goalStates() {
        List<State> goalStates = new ArrayList<>();

        // convert each goal node to a state
        for (MapNode goal : map.goalNodes) {
            goalStates.add(convertToState(goal));
        }

        return goalStates;
    }
}
