import java.util.*;
import java.io.FileReader;
import java.io.BufferedReader;

/**
 * The {@link Map} class defines a graph representation for the city map.
 *
 * @author Adam Eck
 * @date 02/20/2019
 */
public class Map {
    /** The {@link MapNode} that the search should start from. */
    public MapNode startNode;

    /** The list of all {@link MapNode}s in the map. */
    public final List<MapNode> nodes;

    /** A reverse lookup for finding {@link MapNode}s by lat/lon. */
    public final HashMap<Double, HashMap<Double, MapNode>> nodeMap;

    /** The list of all {@link MapNode} goals. */
    public final List<MapNode> goalNodes;

    /**
     * Constructs a new {@link Map}.
     */
    public Map() {
        // create the fields
        startNode = null;

        // create the collections
        nodes = new ArrayList<>();
        nodeMap = new HashMap<>();
        goalNodes = new ArrayList<>();
    }

    /**
     * Adds a {@link MapNode} to the {@link Map}.
     *
     * @param node The {@link MapNode} to add
     */
    public void addMapNode(MapNode node) {
        if (!nodes.contains(node)) {
            nodes.add(node);

            if (!nodeMap.containsKey(node.lat)) {
                nodeMap.put(node.lat, new HashMap<>());
            }
            nodeMap.get(node.lat).put(node.lon, node);
        }
    }

    /**
     * A utility method for reading a {@link Map} from a given file.
     *
     * @param filename The path to the file to read the {@link Map} from
     *
     * @return The {@link Map} contained in {@code filename}
     */
    public static Map readFromFile(String filename) {
        Map map = new Map();

        // read from the file
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));

            boolean inNodes = false;
            boolean inEdges = false;
            boolean inStart = false;
            boolean inGoals = false;

            String line, loc, neighbors;
            String[] split, splitLoc, neighborsSplit, tupleSplit;
            int id, nodeNum, neighborNum;
            double lat, lon, cost;
            MapNode node;
            while (reader.ready()) {
                line = reader.readLine();

                // did we switch sections
                if ("".equals(line)) {
                    continue;
                } else if (line.startsWith("Nodes")) {
                    inNodes = true;
                    continue;
                } else if (line.startsWith("Edges")) {
                    inNodes = false;
                    inEdges = true;
                    continue;
                } else if (line.startsWith("Start")) {
                    inEdges = false;
                    inStart = true;
                    continue;
                } else if (line.startsWith("Goal")) {
                    inStart = false;
                    inGoals = true;
                    continue;
                }

                // process the info on the line
                if (inNodes) {
                    // split up the text
                    split = line.split(":");
                    loc = split[1];
                    splitLoc = loc.replaceAll("\\(", "").replaceAll("\\)", "")
                            .replaceAll(" ", "").split(",");

                    // parse the values
                    lat = Double.parseDouble(splitLoc[0]);
                    lon = Double.parseDouble(splitLoc[1]);

                    // create the node
                    node = new MapNode(lat, lon);
                    map.addMapNode(node);
                } else if (inEdges) {
                    split = line.split(":");
                    neighbors = split[1];
                    neighborsSplit = neighbors.replaceAll(" ", "").split(",");

                    nodeNum = Integer.parseInt(split[0]);
                    node = map.nodes.get(nodeNum);
                    for (String str : neighborsSplit) {
                        tupleSplit = str.replaceAll("\\(", "").replaceAll("\\)", "")
                                .replaceAll(" ", "").split(";");

                        neighborNum = Integer.parseInt(tupleSplit[0]);
                        cost = Double.parseDouble(tupleSplit[1]);
                        node.addNeighbor(map.nodes.get(neighborNum), cost);
                    }
                } else if (inStart) {
                    nodeNum = Integer.parseInt(line);
                    map.startNode = map.nodes.get(nodeNum);
                } else if (inGoals) {
                    split = line.replaceAll(" ", "").split(",");
                    for (String str : split) {
                        nodeNum = Integer.parseInt(str);
                        map.goalNodes.add(map.nodes.get(nodeNum));
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("Problem reading from file!");
            ex.printStackTrace();
            System.exit(-1);
        }

        return map;
    }
}
