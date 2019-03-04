import java.util.*;

/**
 * The {@link MapNode} class represents a single node in a Map.
 *
 * @author Adam Eck
 * @date 02/20/2019
 */
public class MapNode {
    /** The latitude of the node. */
    public final double lat;

    /** The longitude of the node. */
    public final double lon;

    /** The list of {@link MapNode} neighbors. */
    public final List<MapNode> neighbors;

    /** The hashmap of neighbors to costs. */
    public final HashMap<MapNode, Double> costs;

    /**
     * Creates a new {@link MapNode}.
     *
     * @param lat the latitude coordinate
     * @param lon The longitude coordinate
     */
    public MapNode(double lat, double lon) {
        // save the params
        this.lat = lat;
        this.lon = lon;

        // create the collections
        neighbors = new ArrayList<>();
        costs = new HashMap<>();
    }

    /**
     * Adds a {@link MapNode} as a neighbor.
     *
     * @param neighbor The {@link MapNode} neighbor to add
     * @param cost The cost of moving to {@code neighbor}
     */
    public void addNeighbor(MapNode neighbor, double cost) {
        if (!neighbors.contains(neighbor)) {
            neighbors.add(neighbor);
            costs.put(neighbor, cost);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapNode mapNode = (MapNode) o;
        return Double.compare(mapNode.lat, lat) == 0 &&
                Double.compare(mapNode.lon, lon) == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(lat, lon);
    }
}
