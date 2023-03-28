package wellnus.focus.command;

import java.util.ArrayList;
import java.util.HashMap;

import wellnus.command.Command;
import wellnus.exception.BadCommandException;
import wellnus.focus.feature.FocusManager;
import wellnus.ui.TextUi;

/**
 * Implementation of Focus Timer WellNus' <code>help</code> command. Explains to the user what commands are supported
 * by Focus Timer and how to use each command.
 */
public class HelpCommand extends Command {
    public static final String COMMAND_DESCRIPTION = "help - Get help on what commands can be used "
            + "in Focus Timer WellNUS++";
    public static final String COMMAND_USAGE = "usage: help (command-to-check)";
    private static final String BAD_COMMAND_MESSAGE = "help does not take in any arguments!";
    private static final String COMMAND_KEYWORD = "help";
    private static final String NO_FEATURE_KEYWORD = "";
    private static final String HELP_PREAMBLE = "Here are all the commands available for you!";
    private static final String PADDING = " ";
    private static final String DOT = ".";
    private static final int ONE_OFFSET = 1;
    private static final int EMPTY_ARG_LENGTH = 0;
    private static final int EXPECTED_PAYLOAD_SIZE = 1;
    private final TextUi textUi;

    /**
     * Initialises a HelpCommand Object using the command arguments issued by the user.
     *
     * @param arguments Command arguments issued by the user
     */
    public HelpCommand(HashMap<String, String> arguments) {
        super(arguments);
        this.textUi = new TextUi();
    }

    private TextUi getTextUi() {
        return this.textUi;
    }
    private ArrayList<String> getCommandDescriptions() {
        ArrayList<String> commandDescriptions = new ArrayList<>();
        commandDescriptions.add(CheckCommand.COMMAND_DESCRIPTION);
        commandDescriptions.add(ConfigCommand.COMMAND_DESCRIPTION);
        commandDescriptions.add(HelpCommand.COMMAND_DESCRIPTION);
        commandDescriptions.add(HomeCommand.COMMAND_DESCRIPTION);
        commandDescriptions.add(NextCommand.COMMAND_DESCRIPTION);
        commandDescriptions.add(PauseCommand.COMMAND_DESCRIPTION);
        commandDescriptions.add(ResumeCommand.COMMAND_DESCRIPTION);
        commandDescriptions.add(StartCommand.COMMAND_DESCRIPTION);
        commandDescriptions.add(StopCommand.COMMAND_DESCRIPTION);
        return commandDescriptions;
    }

    private ArrayList<String> getCommandUsages() {
        ArrayList<String> commandUsages = new ArrayList<>();
        commandUsages.add(CheckCommand.COMMAND_USAGE);
        commandUsages.add(ConfigCommand.COMMAND_USAGE);
        commandUsages.add(HelpCommand.COMMAND_USAGE);
        commandUsages.add(HomeCommand.COMMAND_USAGE);
        commandUsages.add(NextCommand.COMMAND_USAGE);
        commandUsages.add(PauseCommand.COMMAND_USAGE);
        commandUsages.add(ResumeCommand.COMMAND_USAGE);
        commandUsages.add(StartCommand.COMMAND_USAGE);
        commandUsages.add(StopCommand.COMMAND_USAGE);
        return commandUsages;
    }

    /**
     * Lists all features available in Focus Timer WellNUS++ and a short description.
     */
    private void printHelpMessage() {
        ArrayList<String> commandDescriptions = getCommandDescriptions();
        ArrayList<String> commandUsages = getCommandUsages();
        // Add in description
        String outputMessage = FocusManager.FEATURE_HELP_DESCRIPTION;
        outputMessage = outputMessage.concat(System.lineSeparator());
        outputMessage = outputMessage.concat(HELP_PREAMBLE);
        outputMessage = outputMessage.concat(System.lineSeparator() + System.lineSeparator());

        for (int i = 0; i < commandUsages.size(); i += 1) {
            outputMessage = outputMessage.concat(i + ONE_OFFSET + DOT + PADDING);
            outputMessage = outputMessage.concat(commandDescriptions.get(i) + System.lineSeparator());
            outputMessage = outputMessage.concat(commandUsages.get(i) + System.lineSeparator());
        }
        this.getTextUi().printOutputMessage(outputMessage);
    }

    @Override
    protected String getCommandKeyword() {
        return HelpCommand.COMMAND_KEYWORD;
    }

    @Override
    protected String getFeatureKeyword() {
        return HelpCommand.NO_FEATURE_KEYWORD;
    }

    /**
     * Executes the issued help command.<br>
     * <p>
     * Prints a brief description of all of Focus Timer WellNus' supported commands if
     * the basic 'help' command was issued.<br>
     * <p>
     * Prints a detailed description of a specific feature if the specialised
     * 'help' command was issued.
     */
    @Override
    public void execute() {
        try {
            validateCommand(getArguments());
        } catch (BadCommandException exception) {
            getTextUi().printOutputMessage(exception.getMessage());
            return;
        }
        this.printHelpMessage();
    }

    /**
     * Checks whether the given arguments are valid for our help command.
     *
     * @param arguments Argument-Payload map generated by CommandParser using user's command
     * @throws BadCommandException If the command is invalid
     */
    @Override
    public void validateCommand(HashMap<String, String> arguments) throws BadCommandException {
        assert arguments.containsKey(COMMAND_KEYWORD) : "HelpCommand's payload map does not contain 'help'!";
        // Check if user put in unnecessary payload or arguments
        if (arguments.get(COMMAND_KEYWORD).length() > EMPTY_ARG_LENGTH || arguments.size() > EXPECTED_PAYLOAD_SIZE) {
            throw new BadCommandException(BAD_COMMAND_MESSAGE);
        }
    }

    /**
     * Abstract method to ensure developers add in a command usage.
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
