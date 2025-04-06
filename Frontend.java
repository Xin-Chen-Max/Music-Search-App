import java.util.List;
import java.util.Scanner;
import java.io.IOException;

/**
 * Frontend - CS400 Project 1: iSongly
 * <p>
 * This class implements FrontendInterface. This class must rely rely only on the provided Scanner
 * to read input from the user, and must use the provided BackendInterface reference to compute the
 * results of a command requested by the user.
 */
public class Frontend implements FrontendInterface {
    private Scanner in;
    private BackendInterface backend;

    /**
     * A constructor
     *
     * @param in      Scanner to read input from the user
     * @param backend the provided BackendInterface reference to compute the results of an command
     *                requested by the user
     */
    public Frontend(Scanner in, BackendInterface backend) {
        this.in = in;
        this.backend = backend;
    }

    /**
     * Displays instructions for the syntax of user commands.  And then repeatedly gives the user an
     * opportunity to issue new commands until they enter "quit".  Uses the evaluateSingleCommand
     * method below to parse and run each command entered by the user.  If the backend ever throws
     * any exceptions, they should be caught here and reported to the user.  The user should then
     * continue to be able to issue subsequent commands until they enter "quit".  This method must
     * use the scanner passed into the constructor to read commands input by the user.
     */
    @Override
    public void runCommandLoop() {
        displayCommandInstructions();
		boolean status = true;
        while (status) {
            System.out.print("Enter here:");
            // if quit app directly
            if (!in.hasNextLine()) {
                break;
            }
            // ignore space at the start and the end
            String command = in.nextLine().trim();
			if (command.equalsIgnoreCase("quit")) {
				status = false;
				continue;	
			}
            // execute command
            try {
                executeSingleCommand(command);
            } catch (Exception e) { // invalid command
                System.out.println("Invalid Command" + e.getMessage());
            }
        }
    }

    /**
     * Displays instructions for the user to understand the syntax of commands
     * that they are able to enter.  This should be displayed once from the
     * command loop, before the first user command is read in, and then later
     * in response to the user entering the command: help.
     *
     * The lowercase words in the following examples are keywords that the
     * user must match exactly in their commands, while the upper case words
     * are placeholders for arguments that the user can specify.  The following
     * are examples of valid command syntax that your frontend should be able
     * to handle correctly.
     *
     * load FILEPATH
     * year MAX
     * year MIN to MAX
     * loudness MAX
     * show MAX_COUNT
     * show most danceable
     * help
     * quit
     */
    @Override
    public void displayCommandInstructions() {
        System.out.println("Here are some instructions on how to use the app:");
        System.out.println("load FILEPATH         :load data from a FILEPATH you chose");
        System.out.println("year MAX              :set the upper bound for the year range");
        System.out.println("year MIN to MAX       :set the lower and upper bounds for the year range");
        System.out.println("loudness MAX          :set the loudness filter threshold");
        System.out.println("show MAX_COUNT        :display up to first MAX_COUNT number of songs");
        System.out.println("show most danceable   :display five most danceable songs");
        System.out.println("help                  :display command instructions");
        System.out.println("quit                  :exit the app");
    }

    /**
     * This method takes a command entered by the user as input. It parses
     * that command to determine what kind of command it is, and then makes
     * use of the backend (which was passed to the constructor) to update the
     * state of that backend.  When a show or help command are issued, this
     * method prints the appropriate results to standard out.  When a command
     * does not follow the syntax rules described above, this method should
     * print out an error message that describes at least one defect in the
     * syntax of the provided command argument.
     *
     * Some notes on the expected behavior of the different commands:
     *     load: results in backend loading data from specified path
     *     year: updates backend's range of songs to return
     *                 should not result in any songs being displayed
     *     loudness: updates backend's filter threshold
     *                   should not result in any songs being displayed
     *     show: displays list of songs with currently set thresholds
     *           MAX_COUNT: argument limits the number of song titles displayed
     *           to the first MAX_COUNT in the list returned from backend
     *           most danceable: argument displays results returned from the
     *           backend's fiveMost method
     *     help: displays command instructions
     *     quit: ends this program (handled by runCommandLoop method above)
     *           (do NOT use System.exit(), as this will interfere with tests)
     */
    @Override
    public void executeSingleCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            System.out.println("Command cannot be empty.");
            return;
        }
        // convert to lower lowercase to be less restrictive
        String lowerCase = command.toLowerCase();
        // load FILEPATH
        if (lowerCase.startsWith("load ")) {
            String[] parts = command.split("\\s+", 2); // split into 2 parts
            if (parts.length < 2 || parts[1].trim().isEmpty()) {
                System.out.println("No file path detected");
            } else {
                try {
                    backend.readData(parts[1].trim());
                    System.out.println("Successfully loaded the path！");
                } catch (IOException e) {
                    System.out.println("Can't find this path.");
                }
            }
            return;
        }
        // year MAX or year MIN to MAX
        if (lowerCase.startsWith("year ")) {
            String year = command.substring(5).trim();
            String[] yearBound = year.split("\\s+");
            // Only MAX
            if (yearBound.length == 1) {
                try {
                    int maxYear = Integer.parseInt(yearBound[0]);
                    // default min is 0
                    backend.getRange(0, maxYear);
                    System.out.println("Year range set to 0 through " + maxYear + ".");
                } catch (NumberFormatException e) {
                    System.out.println("Please type in integer numbers.");
                }
            } else if (yearBound.length == 3 && yearBound[1].equalsIgnoreCase("to")) {
                try {
                    int minYear = Integer.parseInt(yearBound[0]);
                    int maxYear = Integer.parseInt(yearBound[2]);
                    if (minYear > maxYear) {
                        System.out.println("MINYear cannot be greater than MAXYear");
                        return;
                    }
                    backend.getRange(minYear, maxYear);
                    System.out.println("Year range set to " + minYear + " through " + maxYear +
                            ".");
                } catch (NumberFormatException e) {
                    System.out.println("Please type in integer number.");
                }
            } else {
                System.out.println("Invalid Command, please use 'year MAX' or 'year MIN to MAX'.");
            }
            return;
        }

        // loudness MAX
        if (lowerCase.startsWith("loudness ")) {
            String MAX = command.substring(9).trim();
            try {
				int max = Integer.parseInt(MAX);
                backend.filterSongs(max);
                System.out.println("Loudness threshold set to " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Please enter an integer.");
            }
            return;
        }
        // show MAX_COUNT or show most danceable
        if (lowerCase.startsWith("show ")) {
            String MAX_COUNT = command.substring(5).trim();
            // "show most danceable" command
            if (MAX_COUNT.equalsIgnoreCase("most danceable")) {
                List<String> songs = backend.fiveMost();
                System.out.println("Most danceable songs:");
                for (String song : songs) {
                    System.out.println("   " + song);
                }
            } else { // entered a number
                try {
                    int number = Integer.parseInt(MAX_COUNT);
                    List<String> songs = backend.fiveMost();
                    System.out.println("Showing up to " + number + " songs:");
                    for (int i = 0; i < Math.min(number, songs.size()); i++) {
                        System.out.println("  " + songs.get(i));
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter 'most danceable' or an integer.");
                }
            }
            return;
        }
        // help command
        if (lowerCase.equals("help")) {
            displayCommandInstructions();
            return;
        }
        // if user enter quit, then end the app
        // handled in runCommandLoop
        // Unrecognized command
        System.out.println("Invalid Command, please enter help for command instructions");
    }

}
