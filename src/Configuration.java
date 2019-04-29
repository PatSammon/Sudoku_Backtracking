import java.util.Collection;

/**
 * Interface that will represent a single configuration of a puzzle solution
 * All puzzle solutions must rely on this Configuration
 * @author Pat Sammon
 */
public interface Configuration {
    /**
     * Method that will return all possible successors whether it is valid or invalid
     */
    public Collection<Configuration> getSuccessors();

    /**
     * method that will return whether the particular solution is valid or not
     */
    public boolean isValid();

    /**
     * Method that will return whether the board is completely filled or not
     */
    public boolean isGoal();
}
