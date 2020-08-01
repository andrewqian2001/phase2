package backend.tradesystem.general_managers;

import backend.exceptions.AuthorizationException;
import backend.exceptions.UserNotFoundException;
import backend.models.Report;
import backend.models.users.Admin;
import backend.tradesystem.Manager;

import java.io.*;
import java.util.ArrayList;

/**
 * Used for managing reports
 */
public class ReportManager extends Manager {


    /**
     * Initialize the objects to get items from databases
     *
     * @throws IOException if something goes wrong with getting database
     */
    public ReportManager() throws IOException {
        super();
    }

    /**
     * Making the database objects with set file paths
     *
     * @param userFilePath         the user database file path
     * @param tradableItemFilePath the tradable item database file path
     * @param tradeFilePath        the trade database file path
     * @throws IOException issues with getting the file path
     */
    public ReportManager(String userFilePath, String tradableItemFilePath, String tradeFilePath) throws IOException {
        super(userFilePath, tradableItemFilePath, tradeFilePath);
    }

    /**
     * Reporting a user
     *
     * @param fromUserId the user that sent the report
     * @param toUserId   user being reported
     * @param message    what the report is about
     * @return whether or not the report successfully went through
     * @throws UserNotFoundException  user wasn't found
     * @throws AuthorizationException report is invalid
     */
    public boolean reportUser(String fromUserId, String toUserId, String message) throws UserNotFoundException, AuthorizationException {
        boolean successful = false;
        if (fromUserId.equals(toUserId)) throw new AuthorizationException("You cannot report yourself.");
        if (getUser(fromUserId).isFrozen())
            throw new AuthorizationException("This user is frozen and can't report others.");
        Report report = new Report(fromUserId, toUserId, message);

        // Add the report to all admins so that they can see the report.
        for (String userId : getUserDatabase().getItems().keySet()) {
            if (getUser(userId) instanceof Admin) {
                Admin admin = ((Admin) getUser(userId));
                admin.getReports().add(report);
                updateUserDatabase(admin);
                successful = true;
            }
        }
        return successful;
    }

    /**
     * Gets all reports
     * Each element in the list is structured like such: [fromUserId, reportedUserId, message, reportId]
     *
     * @return all reports
     */
    public ArrayList<String[]> getReports() {
        for (String userId : getUserDatabase().getItems().keySet()) {
            try {
                if (getUser(userId) instanceof Admin) {
                    Admin admin = ((Admin) getUser(userId));
                    ArrayList<String[]> reports = new ArrayList<>();
                    for (Report report : admin.getReports()) {
                        String[] item = {report.getFromUserId(), report.getReportOnUserId(), report.getMessage(), report.getId()};
                        reports.add(item);
                    }
                    return reports;
                }
            } catch (UserNotFoundException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    /**
     * Remove a report from the list of reports
     *
     * @param reportId the report being removed
     */
    public void clearReports(String reportId) {
        for (String userId : getUserDatabase().getItems().keySet()) {
            try {
                if (getUser(userId) instanceof Admin) {
                    Admin admin = ((Admin) getUser(userId));
                    ArrayList<Report> reports = admin.getReports();
                    reports.removeIf(report -> report.getId().equals(reportId));
                    updateUserDatabase(admin);
                }
            } catch (UserNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Clears all reports
     */
    public void clearReports() {
        for (String userId : getUserDatabase().getItems().keySet()) {
            try {
                if (getUser(userId) instanceof Admin) {
                    Admin admin = ((Admin) getUser(userId));
                    admin.setReports(new ArrayList<>());
                    updateUserDatabase(admin);
                }
            } catch (UserNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
