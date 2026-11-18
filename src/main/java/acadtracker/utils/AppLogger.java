
package acadtracker.utils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class AppLogger {

    private static final Logger LOGGER =
        Logger.getLogger("ACAD_TRACKER");

    static {
        try {
            FileHandler fileHandler =
                new FileHandler("acad-tracker.log", true);

            fileHandler.setFormatter(new SimpleFormatter());

            LOGGER.addHandler(fileHandler);

            // Avoid printing the same log to the console.
            LOGGER.setUseParentHandlers(false);

        } catch (IOException e) {
            System.err.println(
                "Unable to initialize application logger: "
                + e.getMessage()
            );
        }
    }

    private AppLogger() {
        // Prevent object creation.
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}
