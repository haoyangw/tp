package wellnus.focus.command;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import wellnus.command.Command;
import wellnus.exception.BadCommandException;
import wellnus.exception.FocusException;
import wellnus.exception.WellNusException;
import wellnus.focus.feature.FocusManager;
import wellnus.focus.feature.Session;
import wellnus.ui.TextUi;


/**
 *
 */
//@@author nichyjt
public class ConfigCommand extends Command {
    public static final String COMMAND_DESCRIPTION = "config - Change the number of cycles"
            + " and the times of the work, break and long break of your session!\n"
            + "Note that the minimum cycles is 2 mins,\n"
            + "the maximum number of cycles is 5 mins,\n"
            + "the maximum work/break times is 60 mins,\n"
            + "the minimum work/break times is 1 min"
            + "This is to ensure your well-being as higher values might be counter-productive!";
    public static final String COMMAND_USAGE = "usage: config ([--cycle number] [--work minutes]"
            + "[--break minutes] [--longbreak minutes])";
    private static final String PRINT_CONFIG_MESSAGE = "Okay, here's your new session details!\n";
    private static final String PRINT_CONFIG_CYCLES = "Cycles: ";
    private static final String PRINT_CONFIG_WORK = "Work: ";
    private static final String PRINT_CONFIG_BREAK = "Break: ";
    private static final String PRINT_CONFIG_LONG_BREAK = "Long break: ";
    private static final String SINGLE_SPACE_PAD = " ";
    private static final String PRINT_CONFIG_MINS = "minutes";

    private static final String COMMAND_KEYWORD = "config";
    private static final String ARGUMENT_CYCLE = "cycle";
    private static final String ARGUMENT_WORK = "work";
    private static final String ARGUMENT_BREAK = "break";
    private static final String ARGUMENT_LONG_BREAK = "longbreak";
    private static final int COMMAND_MAX_NUM_ARGUMENTS = 5;
    private static final int COMMAND_MIN_NUM_ARGUMENTS = 2;
    private static final int MAX_MINUTES = 60;
    private static final int MIN_MINUTES = 1;
    private static final int MAX_CYCLES = 5;
    private static final int MIN_CYCLES = 2;
    // Message constants
    private static final String ASSERT_STRING_INPUT_NOT_NULL = "String input should not be null!";
    private static final String ERROR_NOT_A_NUMBER = "You didn't input a valid number!";
    private static final String ERROR_LARGE_CYCLES = "Sorry, the max cycles you can set is " + MAX_CYCLES;
    private static final String ERROR_LESS_EQUAL_MIN_CYCLES = "Sorry, the min cycles you can set is " + MIN_CYCLES;
    private static final String ERROR_LARGE_MINUTES = "Sorry, the max time you can set is " + MAX_MINUTES;
    private static final String ERROR_LESS_EQUAL_MIN_MINUTES = "Sorry, the min time you can set is " + MIN_MINUTES;
    private static final String ERROR_TOO_MANY_ARGUMENTS = "Sorry, you seem to have added too many arguments!";
    private static final String ERROR_TOO_FEW_ARGUMENTS = "Sorry, you seem to have not added in any arguments!";

    private static final Logger LOGGER = Logger.getLogger("ConfigCommandLogger");
    private static final String LOG_VALIDATION_ASSUMPTION_FAIL = "New cycle/break/work time is assumed to "
            + "have passed the validation bounds and type checking, but has"
            + "unexpectedly failed the second redundant check! This may be a developer error.";


    // Final is OK to be used here since the command will be constructed on a need-to basis
    private final TextUi textUi;
    private final Session session;
    private int newCycle;
    private int newWork;
    private int newBreak;
    private int newLongBreak;


    /**
     * @param arguments
     */
    public ConfigCommand(HashMap<String, String> arguments, Session session) {
        super(arguments);
        this.textUi = new TextUi();
        this.session = session;
        newCycle = session.getCycle();
        newWork = session.getWork();
        newBreak = session.getBrk();
        newLongBreak = session.getLongBrk();
    }

    /**
     * Identifies this Command's keyword. Override this in subclasses so
     * toString() returns the correct String representation.
     *
     * @return String Keyword of this Command
     */
    @Override
    protected String getCommandKeyword() {
        return COMMAND_KEYWORD;
    }

    /**
     * Identifies the feature that this Command is associated with. Override
     * this in subclasses so toString() returns the correct String representation.
     *
     * @return String Keyword for the feature associated with this Command
     */
    @Override
    protected String getFeatureKeyword() {
        return FocusManager.FEATURE_NAME;
    }

    /**
     * Executes the specified command from the user.<br>
     * <p>
     * May throw Exceptions if command fails.
     *
     * @throws wellnus.exception.WellNusException If command fails
     */
    @Override
    public void execute() throws WellNusException {
        HashMap<String, String> argumentPayloads = getArguments();
        try {
            validateCommand(argumentPayloads);
        } catch (BadCommandException exception) {
            throw new WellNusException(exception.getMessage());
        }
        // Set all the session details as necessary
        if (argumentPayloads.containsKey(ARGUMENT_CYCLE)) {
            setSessionCycle(argumentPayloads.get(ARGUMENT_CYCLE));
        }
        if (argumentPayloads.containsKey(ARGUMENT_WORK)) {
            setSessionWork(argumentPayloads.get(ARGUMENT_WORK));
        }
        if (argumentPayloads.containsKey(ARGUMENT_BREAK)) {
            setSessionBreak(argumentPayloads.get(ARGUMENT_BREAK));
        }
        if (argumentPayloads.containsKey(ARGUMENT_LONG_BREAK)) {
            setSessionLongBreak(argumentPayloads.get(ARGUMENT_LONG_BREAK));
        }
        // Notify the user of the new configuration for user-side verification
        printNewConfiguration();
    }

    /**
     * Validate the arguments and payloads from a commandMap generated by CommandParser. <br>
     * <br>
     * The validation logic and strictness is up to the implementer. <br>
     * <br>
     * As a guideline, <code>isValidCommand</code> should minimally: <br>
     * <li>Verify that ALL MANDATORY arguments exist</li>
     * <li>Verify that ALL MANDATORY payloads exist</li>
     * <li>Safely verify the payload type (int, date, etc should be properly processed)</li>
     * <br>
     * Additionally, payload value cleanup (such as trimming) is also possible. <br>
     * As Java is pass (object reference) by value, any changes made to commandMap
     * will persist out of the function call.
     *
     * @param arguments Argument-Payload map generated by CommandParser
     * @throws wellnus.exception.BadCommandException If the arguments have any issues
     */
    @Override
    public void validateCommand(HashMap<String, String> arguments) throws BadCommandException {
        assert arguments.containsKey(COMMAND_KEYWORD) : "Missing command keyword";
        if (arguments.size() > COMMAND_MAX_NUM_ARGUMENTS) {
            throw new BadCommandException(ERROR_TOO_MANY_ARGUMENTS);
        }
        if (arguments.size() < COMMAND_MIN_NUM_ARGUMENTS) {
            throw new BadCommandException(ERROR_TOO_FEW_ARGUMENTS);
        }
        // Validate all the argument payload pairs
        for (Map.Entry<String, String> argumentPair : arguments.entrySet()) {
            switch (argumentPair.getKey()) {
            case COMMAND_KEYWORD:
                continue;
            case ARGUMENT_CYCLE:
                validateCycles(argumentPair.getValue());
                break;
            case ARGUMENT_BREAK:
            case ARGUMENT_WORK:
            case ARGUMENT_LONG_BREAK:
                validateTimes(argumentPair.getValue());
                break;
            default:
                throw new BadCommandException("Unknown argument!");
            }
        }
    }

    private int getIntegerFromString(String inputString) throws BadCommandException {
        assert inputString != null : ASSERT_STRING_INPUT_NOT_NULL;
        int result;
        try {
            result = Integer.parseInt(inputString);
        } catch (NumberFormatException exception) {
            throw new BadCommandException(ERROR_NOT_A_NUMBER);
        }
        return result;
    }

    private int validateCycles(String cyclePayload) throws BadCommandException {
        assert cyclePayload != null : ASSERT_STRING_INPUT_NOT_NULL;
        int newCycle = getIntegerFromString(cyclePayload);
        if (newCycle > MAX_CYCLES) {
            throw new BadCommandException(ERROR_LARGE_CYCLES);
        }
        if (newCycle < MIN_CYCLES) {
            throw new BadCommandException(ERROR_LESS_EQUAL_MIN_CYCLES);
        }
        return newCycle;
    }

    private int validateTimes(String timePayload) throws BadCommandException {
        assert timePayload != null : ASSERT_STRING_INPUT_NOT_NULL;
        int newTime = getIntegerFromString(timePayload);
        if (newTime > MAX_MINUTES) {
            throw new BadCommandException(ERROR_LARGE_MINUTES);
        }
        if (newTime < MIN_MINUTES) {
            throw new BadCommandException(ERROR_LESS_EQUAL_MIN_MINUTES);
        }
        return newTime;
    }

    private void setSessionCycle(String sessionCycle) throws FocusException {
        // Assume that session cycle must be within the correct range
        // Re-run through the validation logic for redundancy & safety
        try {
            this.newCycle = validateCycles(sessionCycle);
            session.setCycle(newCycle);
        } catch (BadCommandException exception) {
            LOGGER.log(Level.SEVERE, LOG_VALIDATION_ASSUMPTION_FAIL);
            throw new FocusException(exception.getMessage());
        }
    }

    private void setSessionWork(String sessionWork) throws FocusException {
        // Assume that session work must be within the correct range
        // Re-run through the validation logic for redundancy & safety
        try {
            this.newWork = validateTimes(sessionWork);
            session.setWork(newWork);
        } catch (BadCommandException exception) {
            LOGGER.log(Level.SEVERE, LOG_VALIDATION_ASSUMPTION_FAIL);
            throw new FocusException(exception.getMessage());
        }
    }

    private void setSessionBreak(String sessionBreak) throws FocusException {
        // Assume that session break must be within the correct range
        // Re-run through the validation logic for redundancy & safety
        try {
            this.newBreak = validateTimes(sessionBreak);
            session.setBrk(newBreak);
        } catch (BadCommandException exception) {
            LOGGER.log(Level.SEVERE, LOG_VALIDATION_ASSUMPTION_FAIL);
            throw new FocusException(exception.getMessage());
        }
    }

    private void setSessionLongBreak(String sessionLongBreak) throws FocusException {
        // Assume that session work must be within the correct range
        // Re-run through the validation logic for redundancy & safety
        try {
            this.newLongBreak = validateTimes(sessionLongBreak);
            session.setLongBrk(newLongBreak);
        } catch (BadCommandException exception) {
            LOGGER.log(Level.SEVERE, LOG_VALIDATION_ASSUMPTION_FAIL);
            throw new FocusException(exception.getMessage());
        }
    }

    private void printNewConfiguration() {
        String message = PRINT_CONFIG_MESSAGE;
        message = message.concat(PRINT_CONFIG_CYCLES + this.newCycle);
        message = message.concat(System.lineSeparator());
        message = message.concat(PRINT_CONFIG_WORK + this.newWork);
        message = message.concat(SINGLE_SPACE_PAD + PRINT_CONFIG_MINS);
        message = message.concat(System.lineSeparator());
        message = message.concat(PRINT_CONFIG_BREAK + this.newBreak);
        message = message.concat(SINGLE_SPACE_PAD + PRINT_CONFIG_MINS);
        message = message.concat(System.lineSeparator());
        message = message.concat(PRINT_CONFIG_LONG_BREAK + this.newLongBreak);
        message = message.concat(SINGLE_SPACE_PAD + PRINT_CONFIG_MINS);
        textUi.printOutputMessage(message);
    }

    /**
     * Method to ensure that developers add in a command usage.
     * <p>
     * For example, for the 'add' command in AtomicHabit package: <br>
     * "usage: add --name (name of habit)"
     *
     * @return String of the proper usage of the habit
     */
    @Override
    public String getCommandUsage() {
        return COMMAND_USAGE;
    }

    /**
     * Method to ensure that developers add in a description for the command.
     * <p>
     * For example, for the 'add' command in AtomicHabit package: <br>
     * "add - add a habit to your list"
     *
     * @return String of the description of what the command does
     */
    @Override
    public String getCommandDescription() {
        return COMMAND_DESCRIPTION;
    }
}
