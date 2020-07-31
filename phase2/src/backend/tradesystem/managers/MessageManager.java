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
     * Empty out messages that were received from a single user
     * @param userId the user being checked
     * @param clearUserId the messages received from this user that will get cleared
     * @throws UserNotFoundException if the user isn't found
     */
    public void clearMessagesFromUser(String userId, String clearUserId) throws UserNotFoundException {
        User user = getUser(userId);
        user.getMessages().remove(clearUserId);
        updateUserDatabase(user);
    }

    /**
     * Get all messages received by a user
     * @param userId the user being checked for
     * @return user to messages
     * @throws UserNotFoundException if the user isn't found
     */
    public HashMap<String, ArrayList<String>> getMessages(String userId) throws UserNotFoundException {
        User user = getUser(userId);
        return user.getMessages();
    }
}
