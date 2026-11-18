package acadtracker.model;

/**
 * A student's progress on one assignment.
 * Students may move only one step at a time:
 * Assigned, then Pending, then Completed.
 */
public enum AssignmentStatus {

    ASSIGNED,
    PENDING,
    COMPLETED;

    /**
     * The only status a student is allowed to choose next.
     * Completed work has no further step.
     */
    public AssignmentStatus next() {
        return switch (this) {
            case ASSIGNED -> PENDING;
            case PENDING -> COMPLETED;
            case COMPLETED -> null;
        };
    }

    public String label() {
        return switch (this) {
            case ASSIGNED -> "Assigned";
            case PENDING -> "Pending";
            case COMPLETED -> "Completed";
        };
    }

    /**
     * Reads a value stored before or after the status migration.
     * Older rows used IN_PROGRESS for work that is now Pending.
     */
    public static AssignmentStatus fromDatabase(String value) {
        if (value == null || value.isBlank()) {
            return ASSIGNED;
        }

        String normalized = value.trim().toUpperCase();

        if ("IN_PROGRESS".equals(normalized)) {
            return PENDING;
        }

        return valueOf(normalized);
    }
}
