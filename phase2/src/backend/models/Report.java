package backend.models;

import java.io.Serializable;

/**
 * Represents what goes into reporting someone
 */
public class Report extends DatabaseItem implements Serializable {
    private final String fromUserId;
    private final String reportOnUserId;
    private final String message;

    /**
     * Makes a new report
     *
     * @param fromUserId     the user that sent the report
     * @param reportOnUserId the user being reported on
     * @param message        the report description
     */
    public Report(String fromUserId, String reportOnUserId, String message) {
        this.fromUserId = fromUserId;
        this.reportOnUserId = reportOnUserId;
        this.message = message;
    }

    /**
     * The user that sent the report
     *
     * @return The user that sent the report
     */
    public String getFromUserId() {
        return fromUserId;
    }

    /**
     * The user that got reported on
     *
     * @return the user that got reported on
     */
    public String getReportOnUserId() {
        return reportOnUserId;
    }

    /**
     * Description of the report
     * @return what the report is about
     */
    public String getMessage() {
        return message;
    }
}
