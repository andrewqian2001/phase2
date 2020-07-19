package backend.models.users;

import backend.models.Report;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents an admin
 */
public class Admin extends User implements Serializable{


    final ArrayList<Report> reports;

    /**
     * Constructs an admin with a given username and password.
     *
     * @param username the user's username.
     * @param password the user's password.
     */
    public Admin(String username, String password){
        super(username,password);
        this.reports = new ArrayList<>();;
    }
    public ArrayList<Report> getReports() {
        return reports;
    }

    /**
     * Adding a new report
     * @param fromUserId the user that sent the report
     * @param toUserId the user being reported on
     * @param message the message of the report
     */
    public void addReport(String fromUserId, String toUserId, String message){
        reports.add(new Report(fromUserId, toUserId, message));
    }


    /**
     * if the user is frozen, admins cannot be frozen so this is always false
     * @return admins cannot be frozen
     */
    @Override
    public boolean isFrozen() {
        return false;
    }

    /**
     * An admin's frozen status is always false
     * @param frozen regardless of input, frozen status is always false
     */
    @Override
    public void setFrozen(boolean frozen){
        this.setFrozen(false);
    }
}
