
package acadtracker.utils;

import acadtracker.exceptions.StudentNotFoundException;
import acadtracker.exceptions.DuplicateEmailException;
import acadtracker.exceptions.InvalidAttendanceException;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GlobalExceptionHandler {

    private static final Logger LOGGER =
        AppLogger.getLogger();

    private GlobalExceptionHandler() {
    }

    public static void handle(Exception e) {

        if (e instanceof StudentNotFoundException) {

            LOGGER.log(Level.WARNING, e.getMessage());

            System.out.println(
                "Student Error: " + e.getMessage()
            );

        } else if (e instanceof DuplicateEmailException) {

            LOGGER.log(Level.WARNING, e.getMessage());

            System.out.println(
                "Registration Error: " + e.getMessage()
            );

        } else if (e instanceof InvalidAttendanceException) {

            LOGGER.log(Level.WARNING, e.getMessage());

            System.out.println(
                "Attendance Error: " + e.getMessage()
            );

        } else if (e instanceof IllegalArgumentException) {

            LOGGER.log(
                Level.WARNING,
                "Invalid application input.",
                e
            );

            System.out.println(
                "Input Error: " + e.getMessage()
            );

        } else if (e instanceof SQLException) {

            LOGGER.log(
                Level.SEVERE,
                "Database operation failed.",
                e
            );

            System.out.println(
                "A database error occurred. Please try again."
            );

        } else {

            LOGGER.log(
                Level.SEVERE,
                "Unexpected application error.",
                e
            );

            System.out.println(
                "An unexpected error occurred. Please contact support."
            );
        }
    }
}
