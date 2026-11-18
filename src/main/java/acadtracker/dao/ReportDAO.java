
package acadtracker.dao;

import acadtracker.model.ReportRow;

import java.sql.SQLException;
import java.util.List;

public interface ReportDAO {

    List<ReportRow> getAttendanceReport(int facultyId)
            throws SQLException;

    List<ReportRow> getSubjectAttendanceReport(
            int facultyId,
            int subjectId
    ) throws SQLException;

    List<String[]> getAssignmentReport(int facultyId)
            throws SQLException;
}
