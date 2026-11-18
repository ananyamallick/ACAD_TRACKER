
package acadtracker.service;

import acadtracker.dao.ReportDAO;
import acadtracker.dao.ReportDAOImpl;
import acadtracker.model.AuthenticatedUser;
import acadtracker.model.ReportRow;

import java.sql.SQLException;
import java.util.List;

public class ReportService {

    private final ReportDAO reportDAO;
    private final int facultyId;

    public ReportService(AuthenticatedUser faculty) {

        if (faculty == null ||
                !"FACULTY".equals(faculty.getRole())) {

            throw new IllegalArgumentException(
                    "A valid faculty account is required."
            );
        }

        this.facultyId = faculty.getUserId();
        this.reportDAO = new ReportDAOImpl();
    }

    public List<ReportRow> getAttendanceReport()
            throws SQLException {

        return reportDAO.getAttendanceReport(facultyId);
    }

    public List<ReportRow> getSubjectAttendanceReport(int subjectId)
            throws SQLException {

        if (subjectId <= 0) {
            throw new IllegalArgumentException(
                    "Please select a valid subject."
            );
        }

        return reportDAO.getSubjectAttendanceReport(
                facultyId,
                subjectId
        );
    }

    public List<String[]> getAssignmentReport()
            throws SQLException {

        return reportDAO.getAssignmentReport(facultyId);
    }
}
