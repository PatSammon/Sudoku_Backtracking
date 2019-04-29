import java.io.FileNotFoundException;
import java.util.Optional;

/**
 * Class that will run the main program of the Sodoku problem
 * @author Pat Sammon
 */
public class Sudoku {

    /**
     * Main method of this whole program
     * First arg is the filename, and the second will be either true or false depending on if they want debugging on
     * @param args - the arguments sent in when the program is run
     */
    public static void main(String[] args) throws FileNotFoundException {
        //check to see if the number of arguments is correct
        if(args.length != 2){
            System.err.println("Invalid number of arguments");
        }
        else{
            //create the initial configuration for the Sudoku file
            Sudoku_config initial = new Sudoku_config(args[0]);

            //create the backtracker with the debug
            boolean debug = args[1].equals("true");
            Backtracker tracker = new Backtracker(debug);

            //start the clock to see how long it will take
            double start = System.currentTimeMillis();

            //attempt to solve the problem now
            Optional<Configuration> solution = tracker.solve(initial);

            //now grab the time again since it is done computing
            System.out.println("Elapsed time: " + ((System.currentTimeMillis() - start)/1000) + " seconds");

            //indicate if there was a solution or not
            if (solution.isPresent()){
                System.out.println("Solution: "+ "\n" + solution.get());
            }
            else{
                System.out.println("No Solution");
            }
        }
    }
}
