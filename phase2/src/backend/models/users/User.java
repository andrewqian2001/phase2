package backend.models.users;


import backend.models.DatabaseItem;

import java.io.Serializable;

/**
 * Represents a typical account
 */
public abstract class User extends DatabaseItem implements Serializable {
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
     * this user's username
     *
     * @return this user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * to be set to this user
     *
     * @param username to be set to this user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * this user's password
     *
     * @return this user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * new password
     *
     * @param password to be set to this user
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * current frozen status
     *
     * @return if this user is frozen
     */
    public boolean isFrozen() {
        return isFrozen;
    }

    /**
     * new frozen status
     *
     * @param frozen if this user is now frozen
     */
    public void setFrozen(boolean frozen) {
        isFrozen = frozen;
    }


    /**
     * if this user requested to be unfrozen
     *
     * @return if this user requested to be unfrozen
     */
    public boolean isUnfrozenRequested() {
        return isUnfrozenRequested;
    }

    /**
     * if this user requested to be unfrozen
     *
     * @param unfrozenRequested if this user requested to be unfrozen
     */
    public void setUnfrozenRequested(boolean unfrozenRequested) {
        isUnfrozenRequested = unfrozenRequested;
    }

}
