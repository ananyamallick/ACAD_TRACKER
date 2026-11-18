
package acadtracker.dao;

import acadtracker.model.Attendance;
import java.sql.SQLException;
import java.util.List;

public interface AttendanceDAO {

    boolean markAttendance(Attendance attendance)
            throws SQLException;

    List<Attendance> findByEnrollmentId(int enrollmentId)
            throws SQLException;

    double calculateAttendancePercentage(int enrollmentId)
            throws SQLException;

    boolean enrollmentExists(int enrollmentId)
            throws SQLException;

boolean updateAttendance(Attendance attendance)
            throws SQLException;
}
