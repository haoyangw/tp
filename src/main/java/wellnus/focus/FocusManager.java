package wellnus.focus;


import wellnus.atomichabit.command.HomeCommand;
import wellnus.command.Command;
import wellnus.exception.BadCommandException;
import wellnus.exception.WellNusException;
import wellnus.manager.Manager;
import wellnus.ui.TextUi;

import java.util.HashMap;
import java.util.Scanner;

public class FocusManager extends Manager {

    private static final String START_COMMAND_KEYWORD = "start";
    private static final String PAUSE_COMMAND_KEYWORD = "pause";
    private static final String RESUME_COMMAND_KEYWORD = "resume";
    private static final String CONFIG_COMMAND_KEYWORD = "config";
    private static final String STOP_COMMAND_KEYWORD = "stop";
    private static final String CHECK_COMMAND_KEYWORD = "check";
    private static final String UNKNOWN_COMMAND_MESSAGE = "No such command in atomic habits!";
    private static final String GREETING_MESSAGE = "Welcome to Focus Timer";

    private int Work = 25;
    private int Break = 5;
    private int LongBreak = 15;
    private int Cycle = 4;
    private final TextUi textUi;
    private final Session session;

    public FocusManager() {
        this.textUi = new TextUi();
        this.session = new Session();
    }

    /**
     * Parses the given command from the user and determines the correct Command
     * subclass that can handle its execution.
     *
     * @param commandString Full command issued by the user
     * @return Command object that can execute the user's command
     * @throws BadCommandException If an unknown command was issued by the user
     */
    private Command getCommandFor(String commandString) throws BadCommandException {
        HashMap<String, String> arguments = getCommandParser().parseUserInput(commandString);
        String commandKeyword = getCommandParser().getMainArgument(commandString);
        switch (commandKeyword) {
        case START_COMMAND_KEYWORD:
            return new StartCommand(arguments, session);
        case PAUSE_COMMAND_KEYWORD:
            return new PauseCommand(arguments, session);
        case RESUME_COMMAND_KEYWORD:
            return new ResumeCommand(arguments, session);
        case STOP_COMMAND_KEYWORD:
            return new StopCommand(arguments, session);
        case CONFIG_COMMAND_KEYWORD:
            //return new UpdateCommand(arguments, getHabitList());
        case CHECK_COMMAND_KEYWORD:
            return new CheckCommand(arguments, session);
        default:
            throw new BadCommandException(UNKNOWN_COMMAND_MESSAGE);
        }
    }


    private void runCommands() {
//        Thread inputThread = new Thread(() -> {
//            System.out.print("Enter your command: ");
//            String name = scanner.nextLine();
//        });
//        inputThread.start();
        boolean isExit = false;
        while (!isExit) {
            try {
                String commandString = textUi.getCommand();
                Command command = getCommandFor(commandString);
                command.execute();
                isExit = HomeCommand.isExit(command);
            } catch (BadCommandException badCommandException) {
                String NO_ADDITIONAL_MESSAGE = "";
                textUi.printErrorFor(badCommandException, NO_ADDITIONAL_MESSAGE);
            } catch (WellNusException exception) {
                textUi.printErrorFor(exception, "Check user guide for valid commands!");
            }
        }
    }

    /**
     * Utility function to get the featureName this Manager is administering
     *
     * @return name of the feature that this Manager handles
     */
    @Override
    public String getFeatureName() {
        return null;
    }

    /**
     * Utility function to get a summary description of the feature this Manager is administering
     *
     * @return summary description of the feature that this Manager handles
     */
    @Override
    public String getBriefDescription() {
        return null;
    }

    /**
     * Utility function to get the full description of the feature this Manager is administering
     *
     * @return full description of the feature that this Manager handles
     */
    @Override
    public String getFullDescription() {
        return null;
    }

    /**
     * Utility function to set a list of main commands the feature supports <br>
     * <br>
     * Suggested implementation: <br>
     * <code> this.supportedCommands.add([cmd1, cmd2, ...]); </code>
     */
    @Override
    protected void setSupportedCommands() {

    }

    /**
     * runEventDriver is the entry point into a feature's driver loop <br>
     * <br>
     * This should be the part that contains the infinite loop and switch cases,
     * but it is up to the implementer. <br>
     * Its implementation should include the following:
     * <li>A way to terminate runEventDriver</li>
     * <li>A way to read input from console</li>
     */
    @Override
    public void runEventDriver() throws BadCommandException {
        System.out.println(GREETING_MESSAGE);
        runCommands();
    }
}
