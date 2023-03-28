package wellnus.reflection.command;

import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import wellnus.command.Command;
import wellnus.exception.BadCommandException;
import wellnus.exception.StorageException;
import wellnus.exception.TokenizerException;
import wellnus.reflection.feature.QuestionList;
import wellnus.reflection.feature.ReflectUi;

//@@author wenxin-c
/**
 * Like command to add commands to favorite list.
 */
public class LikeCommand extends Command {
    public static final String COMMAND_DESCRIPTION = "like (index) - Add a particular question to favorite list.";
    public static final String COMMAND_USAGE = "usage: like (index)";
    private static final String COMMAND_KEYWORD = "like";
    private static final String FEATURE_NAME = "reflect";
    private static final String INVALID_COMMAND_MSG = "Command is invalid.";
    private static final String INVALID_COMMAND_NOTES = "Please check the available commands "
            + "and the format of commands.";
    private static final String WRONG_INDEX_MSG = "Please input the correct index of the question you like!";
    private static final String COMMAND_KEYWORD_ASSERTION = "The key should be like.";
    private static final String MISSING_SET_QUESTIONS = "A set of questions has not been gotten";
    private static final String MISSING_SET_QUESTIONS_NOTES = "Please get a set of questions before adding to favorite "
            + "list!";
    private static final String TOKENIZER_ERROR = "The data cannot be tokenized for storage properly!!";
    private static final String STORAGE_ERROR = "The file data cannot be stored properly!!";
    private static final int ARGUMENT_PAYLOAD_SIZE = 1;
    private static final int UPPER_BOUND = 5;
    private static final int LOWER_BOUND = 1;
    private static final int INDEX_ONE = 1;
    private static final Logger LOGGER = Logger.getLogger("ReflectLikeCommandLogger");
    private static final ReflectUi UI = new ReflectUi();
    private Set<Integer> randomQuestionIndexes;
    private QuestionList questionList;

    /**
     * Set up the argument-payload pairs for this command.<br/>
     * Pass in a questionList object from ReflectionManager to access the indexes of the previous set of questions.
     *
     * @param arguments Argument-payload pairs from users
     * @param questionList Object that contains the data about questions
     */
    public LikeCommand(HashMap<String, String> arguments, QuestionList questionList) {
        super(arguments);
        this.questionList = questionList;
        this.randomQuestionIndexes = questionList.getRandomQuestionIndexes();
    }

    /**
     * Get the command itself.
     *
     * @return Command: like
     */
    @Override
    protected String getCommandKeyword() {
        return COMMAND_KEYWORD;
    }

    /**
     * Get the name of the feature in which this get command is generated.
     *
     * @return Feature name: reflect
     */
    @Override
    protected String getFeatureKeyword() {
        return FEATURE_NAME;
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

    /**
     * Validate the command.<br/>
     * <br/>
     * Conditions for command to be valid:<br/>
     * <li>Only one argument-payload pair
     * <li>The pair contains key: like
     * <li>Payload must be string which parse into integer ranges from 1 to 5
     * Whichever mismatch will cause the command to be invalid.
     *
     * @param commandMap Argument-Payload map generated by CommandParser
     * @throws BadCommandException If an invalid command is given
     */
    @Override
    public void validateCommand(HashMap<String, String> commandMap) throws BadCommandException, NumberFormatException {
        if (commandMap.size() != ARGUMENT_PAYLOAD_SIZE) {
            throw new BadCommandException(INVALID_COMMAND_MSG);
        } else if (!commandMap.containsKey(COMMAND_KEYWORD)) {
            throw new BadCommandException(INVALID_COMMAND_MSG);
        } else {
            int questionIndex = Integer.parseInt(commandMap.get(COMMAND_KEYWORD));
            if (questionIndex > UPPER_BOUND || questionIndex < LOWER_BOUND) {
                throw new BadCommandException(WRONG_INDEX_MSG);
            }
        }
        assert getArguments().containsKey(COMMAND_KEYWORD) : COMMAND_KEYWORD_ASSERTION;
    }

    /**
     * Entry point to this command.<br/>
     * Check the validity of commands and add into favorite list.<br/>
     */
    @Override
    public void execute() {
        try {
            validateCommand(getArguments());
        } catch (BadCommandException badCommandException) {
            LOGGER.log(Level.INFO, INVALID_COMMAND_MSG);
            UI.printErrorFor(badCommandException, INVALID_COMMAND_NOTES);
            return;
        } catch (NumberFormatException numberFormatException) {
            LOGGER.log(Level.INFO, WRONG_INDEX_MSG);
            UI.printErrorFor(numberFormatException, WRONG_INDEX_MSG);
            return;
        }
        try {
            addFavQuestion(getArguments().get(COMMAND_KEYWORD));
        } catch (BadCommandException badCommandException) {
            LOGGER.log(Level.INFO, MISSING_SET_QUESTIONS);
            UI.printErrorFor(badCommandException, MISSING_SET_QUESTIONS_NOTES);
        } catch (TokenizerException tokenizerException) {
            LOGGER.log(Level.WARNING, TOKENIZER_ERROR);
            UI.printErrorFor(tokenizerException, TOKENIZER_ERROR);
        } catch (StorageException storageException) {
            LOGGER.log(Level.WARNING, STORAGE_ERROR);
            UI.printErrorFor(storageException, STORAGE_ERROR);
        }
    }

    /**
     * User input index ranges from 1 to 5.
     * Since the questions have a unique and fixed index, this function maps the user input index to the real index
     * of the question in the questions list.
     *
     * @return indexQuestionMap The hashmap with display index as key and question index as value.
     */
    public HashMap<Integer, Integer> mapInputToQuestion() {
        HashMap<Integer, Integer> indexQuestionMap = new HashMap<>();
        int displayIndex = INDEX_ONE;
        for (int index : this.randomQuestionIndexes) {
            indexQuestionMap.put(displayIndex, index);
            displayIndex += INDEX_ONE;
        }
        return indexQuestionMap;
    }

    /**
     * Add this index to favorite list and print the question to be added.<br/>
     * <br/>
     * A valid index will only be added(i.e. passed validateCommand()) if there is a set of questions gotten previously
     *
     * @param questionIndex User input of the index of question to be added to favorite list.
     * @throws BadCommandException If there is not a set of question generated yet.
     */
    public void addFavQuestion(String questionIndex) throws BadCommandException, TokenizerException, StorageException {
        int questionIndexInt = Integer.parseInt(questionIndex);
        if (!questionList.hasRandomQuestionIndexes()) {
            throw new BadCommandException(MISSING_SET_QUESTIONS);
        }
        HashMap<Integer, Integer> indexQuestionMap = mapInputToQuestion();
        int indexToAdd = indexQuestionMap.get(questionIndexInt);
        questionList.addFavListIndex(indexToAdd);
    }
}

