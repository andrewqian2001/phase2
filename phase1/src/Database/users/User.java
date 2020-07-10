package Database.users;

import Database.DatabaseItem;

import java.io.Serializable;

/**
 * Represents a typical account
 */
public class User extends DatabaseItem implements Serializable, Permissible {
    private String username;
    private String password;
    private boolean isFrozen;
    private boolean isUnfrozenRequested;

    /**
     * Constructs a user with a given username and password.
     *
     * @param username the user's username.
     * @param password the user's password.
     */
    public User(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    /**
     * @return this user's username.
     */
    String getUsername() {
        return username;
    }

    /**
     * @param username to be set to this user.
     */
    void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return this user's password
     */
    String getPassword() {
        return password;
    }

    /**
     * @param password to be set to this user.
     */
    void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return if this user is frozen.
     */
    boolean isFrozen() {
        return isFrozen;
    }

    /**
     * @param frozen if this user is now frozen.
     */
    void setFrozen(boolean frozen) {
        isFrozen = frozen;
    }


    /**
     * @return if this user requested to be unfrozen
     */
    boolean isUnfrozenRequested() {
        return isUnfrozenRequested;
    }

    /**
     * @param unfrozenRequested if this user requested to be unfrozen
     */
    void setUnfrozenRequested(boolean unfrozenRequested) {
        isUnfrozenRequested = unfrozenRequested;
    }

    /**
     * @return if this user has permission
     */
    public boolean hasPermission(Permission permission) {
        return false;
    }
}
