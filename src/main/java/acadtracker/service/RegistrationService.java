package acadtracker.service;

import acadtracker.dao.RegistrationDAO;
import acadtracker.dao.RegistrationDAOImpl;
import acadtracker.exceptions.DuplicateEmailException;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Validates sign-up details, then creates the account and subject links.
 */
public class RegistrationService {

    private final RegistrationDAO registrationDAO;

    public RegistrationService() {
        this.registrationDAO = new RegistrationDAOImpl();
    }

    public List<String[]> getSubjects() throws SQLException {
        return registrationDAO.findSubjects();
    }

    /**
     * @param subjectIds courses selected from the catalog
     * @param newSubjects extra courses a faculty member teaches, each {name, code}
     * @return a short summary of the faculty mapped to each selected course
     */
    public String register(
            String name,
            String email,
            String password,
            String role,
            List<Integer> subjectIds,
            List<String[]> newSubjects
    ) throws SQLException, DuplicateEmailException {

        String cleanName = requireName(name);
        String cleanEmail = requireEmail(email);
        requirePassword(password);

        if (!"STUDENT".equals(role) && !"FACULTY".equals(role)) {
            throw new IllegalArgumentException("Choose Student or Faculty.");
        }

        List<Integer> selectedIds = distinctIds(subjectIds);
        List<String[]> createdSubjects = cleanNewSubjects(
                role,
                newSubjects
        );

        if ("STUDENT".equals(role) && selectedIds.isEmpty()) {
            throw new IllegalArgumentException(
                    "Students must select at least one subject."
            );
        }

        if ("FACULTY".equals(role) && createdSubjects.isEmpty()) {
            throw new IllegalArgumentException(
                    "Faculty must provide the subject they teach and its subject code."
            );
        }

        if ("STUDENT".equals(role) && !createdSubjects.isEmpty()) {
            throw new IllegalArgumentException(
                    "Students choose from the subjects already offered."
            );
        }

        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());

        try {
            registrationDAO.register(
                    cleanName,
                    cleanEmail,
                    passwordHash,
                    role,
                    selectedIds,
                    createdSubjects
            );
        } catch (SQLIntegrityConstraintViolationException exception) {
            throw constraintMessage(exception);
        }

        return mappingSummary(role, selectedIds, createdSubjects);
    }

    private String mappingSummary(
            String role,
            List<Integer> selectedIds,
            List<String[]> createdSubjects
    ) throws SQLException {

        if ("FACULTY".equals(role)) {
            int total = selectedIds.size() + createdSubjects.size();
            return "Your account is linked to " + total
                    + (total == 1 ? " subject." : " subjects.");
        }

        List<String[]> catalog = registrationDAO.findSubjects();
        List<String> lines = new ArrayList<>();

        for (String[] subject : catalog) {
            int subjectId = Integer.parseInt(subject[0]);
            if (selectedIds.contains(subjectId)) {
                lines.add(subject[1] + " → " + subject[3]);
            }
        }

        return "Faculty assigned from your subjects:\n"
                + String.join("\n", lines);
    }

    private List<Integer> distinctIds(List<Integer> subjectIds) {
        Set<Integer> ids = new LinkedHashSet<>();
        if (subjectIds != null) {
            for (Integer subjectId : subjectIds) {
                if (subjectId != null && subjectId > 0) {
                    ids.add(subjectId);
                }
            }
        }
        return new ArrayList<>(ids);
    }

    private List<String[]> cleanNewSubjects(
            String role,
            List<String[]> newSubjects
    ) {
        List<String[]> cleaned = new ArrayList<>();
        Set<String> codes = new LinkedHashSet<>();

        if (newSubjects == null) {
            return cleaned;
        }

        for (String[] subject : newSubjects) {
            if (subject == null || subject.length < 2) {
                continue;
            }

            String name = subject[0] == null ? "" : subject[0].trim();
            String code = subject[1] == null
                    ? ""
                    : subject[1].trim().toUpperCase(Locale.ROOT);

            if (name.isEmpty() && code.isEmpty()) {
                continue;
            }

            if (!"FACULTY".equals(role)) {
                throw new IllegalArgumentException(
                        "Only faculty can add a subject."
                );
            }

            if (name.length() < 2 || name.length() > 100) {
                throw new IllegalArgumentException(
                        "Each new subject needs a name between 2 and 100 characters."
                );
            }

            if (!code.matches("[A-Z0-9-]{2,20}")) {
                throw new IllegalArgumentException(
                        "Subject codes use 2 to 20 letters or digits."
                );
            }

            if (!codes.add(code)) {
                throw new IllegalArgumentException(
                        "Subject code " + code + " was entered twice."
                );
            }

            cleaned.add(new String[]{name, code});
        }

        return cleaned;
    }

    private String requireName(String name) {
        String cleanName = name == null ? "" : name.trim();

        if (cleanName.length() < 2 || cleanName.length() > 100) {
            throw new IllegalArgumentException(
                    "Enter your name (2 to 100 characters)."
            );
        }

        return cleanName;
    }

    private String requireEmail(String email) {
        String cleanEmail = email == null ? "" : email.trim();

        if (cleanEmail.length() > 150
                || !cleanEmail.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            throw new IllegalArgumentException(
                    "Enter a valid email address."
            );
        }

        return cleanEmail;
    }

    private void requirePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters."
            );
        }
    }

    private SQLException constraintMessage(
            SQLIntegrityConstraintViolationException exception
    ) throws DuplicateEmailException {

        String message = exception.getMessage() == null
                ? ""
                : exception.getMessage().toLowerCase(Locale.ROOT);

        if (message.contains("email")) {
            throw new DuplicateEmailException(
                    "An account with this email already exists."
            );
        }

        if (message.contains("subject_code")) {
            return new SQLException(
                    "That subject code is already used. Choose another code."
            );
        }

        return exception;
    }
}
