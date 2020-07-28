package backend.tradesystem.managers;

import backend.exceptions.AuthorizationException;
import backend.exceptions.UserNotFoundException;
import backend.models.Report;
import backend.models.users.Admin;
import backend.models.users.User;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Used for managing notifications and messages
 */
public class MessageManager extends Manager {


    /**
     * Initialize the objects to get items from databases
     *
     * @throws IOException if something goes wrong with getting database
     */
    public MessageManager() throws IOException {
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
    public MessageManager(String userFilePath, String tradableItemFilePath, String tradeFilePath) throws IOException {
        super(userFilePath, tradableItemFilePath, tradeFilePath);
    }

    /**
     * Send a message
     * @param userId the user sending the message
     * @param toUserId the user receiving the message
     * @param message the message
     * @throws UserNotFoundException if one of the users don't exist
     * @throws AuthorizationException if not allowed to send the message
     */
    public void sendMessage(String userId, String toUserId, String message) throws UserNotFoundException, AuthorizationException {
        if (userId.equals(toUserId)) throw new AuthorizationException("Cannot send a message to self");
        if (getUser(userId).isFrozen()) throw new AuthorizationException("You are frozen and cant send a message.");
        getUser(userId); // Ensures the user exists
        User toUser = getUser(toUserId);
        toUser.addMessage(userId, message);
        updateUserDatabase(toUser);
    }

    /**
     * Empty out messages that were received
     * @param userId the user being checked
     * @throws UserNotFoundException if the user isn't found
     */
    public void clearMessages(String userId) throws UserNotFoundException {
        User user = getUser(userId);
        user.clearMessages();
        updateUserDatabase(user);
    }

    /**
     * Get all messages received by a user
     * @param userId the user being checked for
     * @return user to messages
     * @throws UserNotFoundException if the user isn't found
     */
    public HashMap<User, ArrayList<String>> getMessages(String userId) throws UserNotFoundException {
        User user = getUser(userId);
        HashMap<String, ArrayList<String>> unpopulatedMessages = user.getMessages();
        HashMap<User, ArrayList<String>> populatedMessages = new HashMap<>();
        unpopulatedMessages.forEach((id, message) -> {
            try {
                populatedMessages.put(getUser(id), message);
            } catch (UserNotFoundException ignored) {
            }
        });
        return populatedMessages;
    }

    /**
     * Reporting a user
     *
     * @param fromUserId the user that sent the report
     * @param toUserId   user being reported
     * @param message    what the report is about
     * @return whether or not the report successfully went through
     * @throws UserNotFoundException user wasn't found
     * @throws AuthorizationException report is invalid
     */
    public boolean reportUser(String fromUserId, String toUserId, String message) throws UserNotFoundException, AuthorizationException {
        boolean successful = false;
        if (fromUserId.equals(toUserId)) throw new AuthorizationException("You cannot report yourself.");
        if (getUser(fromUserId).isFrozen()) throw new AuthorizationException("This user is frozen and can't report others.");
        Report report = new Report(fromUserId, toUserId, message);

        // Add the report to all admins so that they can see the report.
        for (User user : getUserDatabase().getItems()) {
            if (user instanceof Admin) {
                Admin admin = ((Admin) user);
                admin.getReports().add(report);
                updateUserDatabase(admin);
                successful = true;
            }
        }
        return successful;
    }

    /**
     * Gets all reports
     * @return all reports
     */
    public ArrayList<Report> getReports(){
        for (User user : getUserDatabase().getItems()) {
            if (user instanceof Admin) {
                Admin admin = ((Admin) user);
                return admin.getReports();
            }
        }
        return new ArrayList<>();
    }

    /**
     * Set the list of all reports that admins see
     * @param reports list of all reports
     */
    public void setReports(ArrayList<Report> reports){
        for (User user : getUserDatabase().getItems()) {
            if (user instanceof Admin) {
                Admin admin = ((Admin) user);
                admin.setReports(reports);
                updateUserDatabase(admin);
            }
        }
    }

    /**
     * Clears all reports
     */
    public void clearReports(){
        for (User user : getUserDatabase().getItems()) {
            if (user instanceof Admin) {
                Admin admin = ((Admin) user);
                admin.setReports(new ArrayList<>());
                updateUserDatabase(admin);
            }
        }
    }

}
