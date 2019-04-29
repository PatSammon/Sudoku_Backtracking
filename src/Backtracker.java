import java.util.Optional;

/**
 * Class that will represent the classic Backtracking recursive solution
 * @author Pat Sammon
 */

public class Backtracker {

    /** Should debug be allowed?*/
    private boolean debugAllowed;

    /**
     * Initialize a new Backtracker
     * @param debugAllowed - whether debug is allowed or not
     */
    public Backtracker(boolean debugAllowed) {
        this.debugAllowed = debugAllowed;
    }

    /**
     * Method that will print the different configurations to the console
     * @param msg - the type of config being looked at
     * @param config - the config to display
     */
    private void debugPrint(String msg, Configuration config){
        if (this.debugAllowed){
            System.out.println(msg + ": " + "\n" + config);
        }
    }

    public Optional<Configuration> solve(Configuration config){
        //print to the console what the current configuration is
        debugPrint("Current Config", config);

        //check to see if the board is full
        if (config.isGoal()){
            debugPrint("Goal Config", config);
            //return it since this is the answer
            return Optional.of(config);
        }
        else{
            //go through all of the possible successors
            for (Configuration child : config.getSuccessors())
            {
                //print to the console what the child config is
                debugPrint("Successor", child);
                //check to see if the child is valid
                if (child.isValid()){
                    if (debugAllowed){
                        //State whether it is valid or not
                        System.out.println("Valid!");
                    }
                    //recurse with this child
                    Optional<Configuration> solution =  solve(child);
                    //check to see if the solution is present, if not then it isn't right
                    if (solution.isPresent()){
                        return solution;
                    }
                }else{
                    //state that it is invalid
                    if (debugAllowed){
                        System.out.println("Invalid!");
                    }
                }
            }
        }
        //if it reaches this point then return an empty Optional
        return Optional.empty();
    }
}
