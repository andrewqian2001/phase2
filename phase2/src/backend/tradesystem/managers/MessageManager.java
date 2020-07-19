package backend.tradesystem.managers;

import backend.exceptions.AuthorizationException;
import backend.exceptions.UserNotFoundException;
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

    public void sendMessage(String userId, String toUserId, String message) throws UserNotFoundException, AuthorizationException {
        if (userId.equals(toUserId)) throw new AuthorizationException("Cannot send a message to self");
        getUser(userId); // Ensures the user exists
        User toUser = getUser(toUserId);
        toUser.addMessage(userId, message);
        updateUserDatabase(toUser);
    }

    public void clearMessages(String userId) throws UserNotFoundException {
        User user = getUser(userId);
        user.clearMessages();
        updateUserDatabase(user);
    }

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

}
