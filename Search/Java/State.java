import java.util.Objects;

/**
 * The {@link State} class represent a state in the search {@link Problem}.  A {@link State} is simply
 * a latitude/longitude pair.
 *
 * @author Adam Eck
 * @date 02/20/2019
 */
public class State {
    /** The latitude of the {@link State}. */
    public final double lat;

    /** The longitude of the {@link State}. */
    public final double lon;

    /**
     * Constructs a new {@link State}.
     *
     * @param lat The latitude
     * @param lon The longitude
     */
    public State(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return Double.compare(state.lat, lat) == 0 &&
                Double.compare(state.lon, lon) == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(lat, lon);
    }
}
