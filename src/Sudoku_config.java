import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * This is the file that will make an instance of the board
 * It is also responsible for creating other instances of the board with possible solutions
 * @author Pat Sammon
 */

public class Sudoku_config implements Configuration{

    //field variables
    /** Number of rows on a Sudoku board*/
    final private static int NUMROWS = 9;
    /** Number of columns on a Sudoku board*/
    final private static int NUMCOLS = 9;
    /** Representation of the board*/
    private String[][] board;
    /** Max occurances a number can be on a board*/
    final private static int MAX_OCCURENCE = 9;
    /** Array holding all of the num variables in it e.) num7 = numList[6]*/
    private List<Integer> numList;
    /** Array holding all of the dictionary variables in it*/
    private List<Map<Integer,Integer>> squareDicts;
    private String lastNumAdded;

    /**
     * Default constructor that willl make the original Sudoku_config
     * @param filename - the file that is being read for the original board contents
     * @throws FileNotFoundException - if the name of the file is incorrect
     */
    public Sudoku_config(String filename) throws FileNotFoundException
    {
        File file = new File(filename);
        try(Scanner scanner = new Scanner(file)) {
            //initiallize the field variables
            board = new String[NUMROWS][NUMCOLS];
            numList = new ArrayList<>(9);
            lastNumAdded = "";
            //initialize the numlist with all 0's
            for(int i = 0; i < 9; i++)
            {
                numList.add(i,0);
            }
            squareDicts = new ArrayList<>(9);
            //initialize the squareDict with all empty dicts
            for (int i =0; i < 9; i++){
                Map map = new HashMap();
                squareDicts.add(map);
            }

            //go through the file now and read all of the values for the board
            int lineCounter = 0;
            while (scanner.hasNextLine()) {
                String[] lineContents = scanner.nextLine().split(" ");
                //go through now assigning the values
                for (int i = 0; i < lineContents.length; i++) {
                    //add the contents directly to the board
                    board[lineCounter][i] = lineContents[i];

                    //see if it is a number and if it is then increase that specific num count
                    if (isNumber(lineContents[i])) {
                        int index = Integer.parseInt(lineContents[i]) - 1;
                        int value = numList.get(index) + 1;
                        numList.set(index, value);
                        //add the num to the proper dictionary
                        addToDict(lineCounter, i, index+1);
                    }
                }
                lineCounter += 1;
            }
        }
    }

    /**
     * Method that will make a deep copy of the Sudoku config instance data
     * @param other - the Sudoku config data being passed in
     */
    protected Sudoku_config(Sudoku_config other)
    {
        this.squareDicts = new ArrayList<>(9);
        //initialize the squareDict with all empty dicts
        for (int i =0; i < 9; i++){
            Map map = new HashMap();
            this.squareDicts.add(map);
        }
        this.board= new String[NUMROWS][NUMCOLS];
        //copy the field variables
        for (int i = 0; i < NUMROWS; i++)
        {
            for (int z = 0; z < NUMCOLS; z++)
            {
                this.board[i][z] = other.board[i][z];
                if (isNumber(board[i][z])){
                    //add the num to the proper dictionary
                    addToDict(i, z, Integer.parseInt(board[i][z]));
                }
            }
        }
        this.lastNumAdded = other.lastNumAdded;
        numList = new ArrayList<>(9);
        //initialize the numlist with all 0's
        for(int i = 0; i < 9; i++)
        {
            int temp = other.numList.get(i);
            numList.add(i,temp);
        }
    }

    /**
     * Method that will return all possible successors whether it is valid or invalid
     * @return
     */
    @Override
    public Collection<Configuration> getSuccessors()
    {
        LinkedList collection = new LinkedList();
        //first make 9 copies of the current configuration
        ArrayList<Sudoku_config> configs = new ArrayList<>();
        for (int i = 0; i < 9; i++)
        {
            configs.add(new Sudoku_config(this));
        }
        //then find the next blank spot, and insert a different number into each of those
        boolean flag = false;
        for (int i = 0; i < NUMROWS; i++) {
            for (int z = 0; z < NUMCOLS; z++) {
                if (board[i][z].equals(".")){
                    //if the board is blank, then try putting in all the possible solutions here
                    for (int k = 0; k < configs.size(); k++) {
                        configs.get(k).board[i][z] = String.valueOf(k+1);
                        configs.get(k).lastNumAdded = i + " " + z;
                    }
                    //set the flag to be true
                    flag = true;
                    break;
                }
            }
            if (flag) {
                break;
            }
        }
        collection.addAll(configs);
        //finally add them to the Collection<Configuration> and then return that
        return collection;
    }

    /**
     * Method that will return whether the board is full or not
     * @return true or false depending on if the board is full or not
     * Time Complexity- O(n^2)
     */
    @Override
    public boolean isGoal()
    {
        //go through the board and search for any blank slots
        for (int i = 0; i < NUMROWS; i++)
        {
            for (int z = 0; z < NUMCOLS; z++)
            {
                if (board[i][z].equals("."))
                {
                    return false;
                }
            }
        }
        //if it makes it down here, it didn't find any blanks and thus is fully filled
        return true;
    }

    /**
     * Method that will return whether the config passed in is valid or invalid
     * @return true or false depending on if it is valid or not
     */
    @Override
    public boolean isValid()
    {
        //todo need to change it from a dict to an array or somethign else
        //first check to see if any of the numbers are over the MAXOCCURENCE
        if (!overMaxOccurence()) {
            return false;
        }
        //get the last number that was added
        String[] temp = this.lastNumAdded.split(" ");
        int lastRow = Integer.parseInt(temp[0]);
        int lastCol = Integer.parseInt(temp[1]);
        int number = Integer.parseInt(this.board[lastRow][lastCol]);
        String lastNum = this.board[lastRow][lastCol];

        //Check to see if it is in the row
        if(!checkHorz(lastRow,lastNum)) {
            return false;
        }
        //check to see if it is in the column
        if (!checkVert(lastCol,lastNum)) {
            return false;
        }
        //check to see if it is in its particular square
        if (!checkDict(lastCol,lastRow,lastNum)) {
            return false;
        }
        //add the number to the dictionary now since it is valid
        //addToDict(lastRow,lastCol,number);
        //if it makes it all the way through, then it is a valid configuration
        return true;
    }

    /**
     * Method that will check to see if that number appears anywhere else in the row
     * Helper method of isValid
     * @param row - the number of the row which the number is in
     * @param num - the number that is being checked
     * @return - true if the number doesn't appear in that row, or false if it does appear
     */
    private boolean checkHorz(int row, String num)
    {
        int numCount = 0;
        for (int i = 0; i < NUMCOLS; i++)
        {
            if (this.board[row][i].equals(num)) {
                numCount += 1;
            }
        }
        if (numCount >1) {
            return false;
        }
        return true;
    }

    /**
     * Method that will check to see if the number appears anywhere else in the column
     * Helper method of isValid
     * @param col - the number of the column that is being checked
     * @param num - the number that is being searched for
     * @return - true if the number doesn't appear in that row, or false if it does appear
     */
    private boolean checkVert(int col, String num)
    {
        int numCount = 0;

        for (int i = 0; i < NUMROWS; i++)
        {
            if (this.board[i][col].equals(num))
            {
                numCount += 1;
            }
        }
        if  (numCount > 1) {
            return false;
        }
        return true;
    }

    /**
     * Method that will check to see if the number appears anywhere else in that 3x3 square
     * Helper method of isValid
     * @param col - the location of the column that is being checked
     * @param row - the location of the row that is being checked
     * @param num - the number that is being checked
     * @return true if the number doesn't appear in that square, false otherwise
     */
    private boolean checkDict(int col, int row, String num)
    {
        if(row < 3){
            //check to see if its in the upper left
            if(col < 3){
                //check to see if its contained in that dict
                if(squareDicts.get(0).containsKey(Integer.parseInt(num))){
                    return false;
                }
            }
            //check to see if its in the upper center
            else if (col < 6){
                if (squareDicts.get(1).containsKey(Integer.parseInt(num))){
                    return false;
                }
            }
            //check to see if its in the upper right
            else{
                if (squareDicts.get(2).containsKey(Integer.parseInt(num))){
                    return false;
                }
            }
        }
        else if(row < 6){
            //check to see if its in the middle left
            if (col < 3)
            {
                if (squareDicts.get(3).containsKey(Integer.parseInt(num))){
                    return false;
                }
            }
            //check the middle center
            else if (col < 6){
                if (squareDicts.get(4).containsKey(Integer.parseInt(num))){
                    return false;
                }
            }
            //check the middle right
            else{
                if (squareDicts.get(5).containsKey(Integer.parseInt(num))){
                    return false;
                }
            }
        }
        else{
            //check the bottom left corner
            if (col < 3)
            {
                if (squareDicts.get(6).containsKey(Integer.parseInt(num))){
                    return false;
                }
            }
            //check the bottom center
            else if (col < 6){
                if (squareDicts.get(7).containsKey(Integer.parseInt(num))){
                    return false;
                }
            }
            //check the bottom right corner
            else{
                if (squareDicts.get(8).containsKey(Integer.parseInt(num))){
                    return false;
                }
            }

        }
        //add the num to the proper dictionary
        //addToDict(row, col, Integer.parseInt(num));
        //if it made it here then it wasnt found in that square
        return true;
    }

    /**
     * Method that will check to see if any of the numbers are over MAX_OCCURENCE
     * Helper method of isValid
     * @return - true if no number went over MAX_OCCURENCE, false otherwise
     */
    private boolean overMaxOccurence()
    {
        //go through and check each number
        for (int i = 0; i < numList.size(); i++)
        {
            //check to see if it has exceeded the MAX_OCCURENCE
            if (numList.get(i) >= MAX_OCCURENCE)
            {
                return false;
            }
        }
        //if it has reached this point then none of the numbers have surpassed MAX_OCCURENCE so return true
        return true;
    }

    /**
     * Method that will check to see if the string is a number or an empty space
     * @param string - the string that is being tested
     * @return - true if it is a number and false if it is not
     */
    private boolean isNumber(String string)
    {
        try
        {
            Integer.parseInt(string);

            //if it makes it here then it is a number
            return true;
        }
        catch (NumberFormatException e)
        {
            //this means it is not a Number
            return false;
        }
    }

    /**
     * Method that will add a number to the dictionary for that square
     * Precondtion: The number is not already in that dict
     * @param row - the row position of the number being added
     * @param col - the column position of the number being added
     * @param num - the number being added
     */
    private void addToDict (int row, int col, int num)
    {
        if (row < 3) {
            //first find out what dictionary it would be in
            if (col < 3) {
                //add the num to the zero index
                Map temp = this.squareDicts.get(0);
                temp.put(num, num);
                squareDicts.set(0, temp);
            } else if (col < 6) {
                //add the num to the first index
                Map temp = this.squareDicts.get(1);
                temp.put(num, num);
                squareDicts.set(1, temp);
            } else {
                //add the num to the second index
                Map temp = this.squareDicts.get(2);
                temp.put(num, num);
                squareDicts.set(2, temp);
            }
        }
        else if (row < 6) {
            //first find out what dictionary it would be in
            if (col < 3) {
                //add the num to the zero index
                Map temp = this.squareDicts.get(3);
                temp.put(num, num);
                squareDicts.set(3, temp);
            } else if (col < 6) {
                //add the num to the first index
                Map temp = this.squareDicts.get(4);
                temp.put(num, num);
                squareDicts.set(4, temp);
            } else {
                //add the num to the second index
                Map temp = this.squareDicts.get(5);
                temp.put(num, num);
                squareDicts.set(5, temp);
            }
        }
        else
        {
            //first find out what dictionary it would be in
            if (col < 3) {
                //add the num to the zero index
                Map temp = this.squareDicts.get(6);
                temp.put(num, num);
                squareDicts.set(6, temp);
            } else if (col < 6) {
                //add the num to the first index
                Map temp = this.squareDicts.get(7);
                temp.put(num, num);
                squareDicts.set(7, temp);
            } else {
                //add the num to the second index
                Map temp = this.squareDicts.get(8);
                temp.put(num, num);
                squareDicts.set(8, temp);
            }
        }
    }

    /**
     * Method that will create a string of the board to print
     * @return - a string of the board
     */
    @Override
    public String toString() {
        String board = "";
        int counter = 0;
        for (int i = 0; i < NUMROWS; i++)
        {
            for (int z = 0; z < NUMCOLS; z++)
            {
                board += this.board[i][z];
                if (z != 0 && (z+1) % 3 == 0)
                {
                    board += "|";
                }
            }
            board += "\n";
            if (counter != 0 && (counter +1) % 3 == 0)
            {
                board+= "------------" + "\n";
            }
            counter += 1;
        }
        return board;
    }
}