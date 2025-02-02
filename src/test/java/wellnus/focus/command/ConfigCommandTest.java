package wellnus.focus.command;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

import wellnus.exception.WellNusException;
import wellnus.focus.feature.Session;


/**
 * Test that ConfigCommand's public functions work as intended
 * <p>
 * Only execute() is called for testing rather than the other public/protected method calls
 * as it covers almost all the main logic and branches.
 */
//@@author nichyjt
public class ConfigCommandTest {
    private boolean isSessionCorrectlyUpdated(Session session, int cycle, int work, int brk, int longbrk) {
        if (session.getWork() != work) {
            return false;
        }
        if (session.getBrk() != brk || session.getLongBrk() != longbrk) {
            return false;
        }
        return session.getCycle() == cycle;
    }

    private HashMap<String, String> generateArguments(String cycle, String work, String brk, String longbrk) {
        HashMap<String, String> argumentPayload = new HashMap<>();
        argumentPayload.put("config", "");
        if (cycle != null) {
            argumentPayload.put(ConfigCommand.ARGUMENT_CYCLE, cycle);
        }
        if (work != null) {
            argumentPayload.put(ConfigCommand.ARGUMENT_WORK, work);
        }
        if (brk != null) {
            argumentPayload.put(ConfigCommand.ARGUMENT_BREAK, brk);
        }
        if (longbrk != null) {
            argumentPayload.put(ConfigCommand.ARGUMENT_LONG_BREAK, longbrk);
        }
        return argumentPayload;
    }

    @Test
    public void executeTest_success() {
        ConfigCommand command;
        Session session = new Session();

        // Test with missing arguments
        HashMap<String, String> argumentPayload = generateArguments("3", "10", null, null);
        command = new ConfigCommand(argumentPayload, session);
        try {
            command.execute();
        } catch (WellNusException exception) {
            fail("Expected execution to pass but failed!");
        }
        assertTrue(isSessionCorrectlyUpdated(session, 3, 10, 1, 1));

        // Test with numbers within range
        argumentPayload = generateArguments("3", "20", "10", "15");
        command = new ConfigCommand(argumentPayload, session);
        try {
            command.execute();
        } catch (WellNusException exception) {
            fail("Expected execution to pass but failed!");
        }
        assertTrue(isSessionCorrectlyUpdated(session, 3, 20, 10, 15));

    }

    /**
     * Ensure that at extreme valid values, executes still works
     */
    @Test
    void executeTest_minMaxRanges_success() {
        ConfigCommand command;
        Session session = new Session();
        // Test with edge values (max accepted values)
        HashMap<String, String> argumentPayload = generateArguments("5", "60", "60", "60");
        command = new ConfigCommand(argumentPayload, session);
        try {
            command.execute();
        } catch (WellNusException exception) {
            fail("Expected execution to pass but failed!");
        }
        assertTrue(isSessionCorrectlyUpdated(session, 5, 60, 60, 60));

        // Test with edge values (min accepted values)
        argumentPayload = generateArguments("2", "1", "1", "1");
        command = new ConfigCommand(argumentPayload, session);
        try {
            command.execute();
        } catch (WellNusException exception) {
            fail("Expected execution to pass but failed!");
        }
        assertTrue(isSessionCorrectlyUpdated(session, 2, 1, 1, 1));
    }

    /**
     * Ensure that negative numbers cause an error to be thrown
     */
    @Test
    public void executeTest_negativeNumbers_exceptionThrown() {
        ConfigCommand command;
        Session session = new Session();

        // Test with negative time values
        HashMap<String, String> argumentPayload = generateArguments("5", "10", "-5", "20");
        command = new ConfigCommand(argumentPayload, session);
        assertThrows(WellNusException.class, command::execute);

        // Test with negative cycle values
        argumentPayload = generateArguments("-5", "10", "10", "20");
        command = new ConfigCommand(argumentPayload, session);
        assertThrows(WellNusException.class, command::execute);
    }

    /**
     * Ensure that large numbers (in config's context) cause an error to be thrown
     */
    @Test
    public void executeTest_largeNumbers_exceptionThrown() {
        ConfigCommand command;
        Session session = new Session();

        // Test with large time values
        HashMap<String, String> argumentPayload = generateArguments("5", "61", "10", "20");
        command = new ConfigCommand(argumentPayload, session);
        assertThrows(WellNusException.class, command::execute);

        // Test with large cycle values
        argumentPayload = generateArguments("10", "10", "10", "20");
        command = new ConfigCommand(argumentPayload, session);
        assertThrows(WellNusException.class, command::execute);
    }

    /**
     * Ensure that NaN values are correctly handled
     */
    @Test
    public void executeTest_notANumber_exceptionThrown() {
        ConfigCommand command;
        Session session = new Session();

        // Test with NaN time value
        HashMap<String, String> argumentPayload = generateArguments("5", "foo", "5", "20");
        command = new ConfigCommand(argumentPayload, session);
        assertThrows(WellNusException.class, command::execute);

        // Test with NaN cycle value
        argumentPayload = generateArguments("bar", "5", "10", "20");
        command = new ConfigCommand(argumentPayload, session);
        assertThrows(WellNusException.class, command::execute);
    }

    /**
     * Ensure that length of argument errors are caught (too few & too many)
     */
    @Test
    public void executeTest_invalidArguments_exceptionThrown() {
        ConfigCommand command;
        Session session = new Session();

        // Test with too many arguments
        HashMap<String, String> argumentPayload = generateArguments("5", "10", "5", "20");
        argumentPayload.put("foo", "bar");
        command = new ConfigCommand(argumentPayload, session);
        assertThrows(WellNusException.class, command::execute);

        // Test with too few arguments
        argumentPayload = generateArguments(null, null, null, null);
        command = new ConfigCommand(argumentPayload, session);
        assertThrows(WellNusException.class, command::execute);
    }
}
