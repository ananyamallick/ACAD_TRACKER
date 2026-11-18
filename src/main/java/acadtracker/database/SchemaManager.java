package acadtracker.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Brings an existing acad_tracker database in line with registration
 * and role-based editing. The update is safe to run more than once.
 *
 * Changes:
 * - assignment status becomes ASSIGNED, PENDING, COMPLETED
 * - assignment_status.grade stores a faculty grade
 * - faculty_subjects links a faculty member to every course they teach
 */
public final class SchemaManager {

    private static volatile boolean ready;

    private SchemaManager() {
    }

    public static void ensure(Connection connection) throws SQLException {
        if (ready) {
            return;
        }

        synchronized (SchemaManager.class) {
            if (ready) {
                return;
            }

            migrateAssignmentStatus(connection);
            ensureGradeColumn(connection);
            ensureFacultySubjects(connection);
            ready = true;
        }
    }

    private static void migrateAssignmentStatus(Connection connection)
            throws SQLException {

        String columnType = columnType(
                connection,
                "assignment_status",
                "status"
        );

        if (columnType == null || !columnType.contains("IN_PROGRESS")) {
            return;
        }

        execute(connection, """
                ALTER TABLE assignment_status
                MODIFY status ENUM(
                    'PENDING', 'IN_PROGRESS', 'COMPLETED', 'ASSIGNED'
                ) NOT NULL DEFAULT 'ASSIGNED'
                """);

        // Old PENDING meant "not started", which is now Assigned.
        // Do this before renaming IN_PROGRESS, or those rows would be lost.
        execute(connection, """
                UPDATE assignment_status
                SET status = 'ASSIGNED'
                WHERE status = 'PENDING'
                """);

        execute(connection, """
                UPDATE assignment_status
                SET status = 'PENDING'
                WHERE status = 'IN_PROGRESS'
                """);

        execute(connection, """
                ALTER TABLE assignment_status
                MODIFY status ENUM(
                    'ASSIGNED', 'PENDING', 'COMPLETED'
                ) NOT NULL DEFAULT 'ASSIGNED'
                """);
    }

    private static void ensureGradeColumn(Connection connection)
            throws SQLException {

        if (columnType(connection, "assignment_status", "grade") != null) {
            return;
        }

        execute(connection, """
                ALTER TABLE assignment_status
                ADD COLUMN grade VARCHAR(8) NULL AFTER status
                """);
    }

    private static void ensureFacultySubjects(Connection connection)
            throws SQLException {

        execute(connection, """
                CREATE TABLE IF NOT EXISTS faculty_subjects (
                    faculty_id INT NOT NULL,
                    subject_id INT NOT NULL,
                    PRIMARY KEY (faculty_id, subject_id),
                    CONSTRAINT fk_faculty_subjects_faculty
                        FOREIGN KEY (faculty_id) REFERENCES users (user_id),
                    CONSTRAINT fk_faculty_subjects_subject
                        FOREIGN KEY (subject_id) REFERENCES subjects (subject_id)
                )
                """);

        // The subject row still names the concerned faculty.
        // This table records every faculty member linked to that course.
        execute(connection, """
                INSERT IGNORE INTO faculty_subjects (faculty_id, subject_id)
                SELECT faculty_id, subject_id
                FROM subjects
                """);
    }

    private static String columnType(
            Connection connection,
            String table,
            String column
    ) throws SQLException {

        String sql = """
                SELECT COLUMN_TYPE
                FROM information_schema.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE()
                  AND TABLE_NAME = ?
                  AND COLUMN_NAME = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, table);
            statement.setString(2, column);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getString(1);
                }
            }
        }

        return null;
    }

    private static void execute(Connection connection, String sql)
            throws SQLException {

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }
}
